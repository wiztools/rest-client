package org.wiztools.restclient;

import java.io.*;
import java.net.HttpCookie;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.auth.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.mime.*;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.auth.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.*;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StreamUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.http.RESTClientCookieStore;
import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.IDNUtil;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class HTTPClientRequestExecuter implements RequestExecuter {

    private static final Logger LOG = Logger.getLogger(HTTPClientRequestExecuter.class.getName());

    public static boolean traceLog = false;

    private CloseableHttpClient httpClient;

    private boolean interruptedShutdown = false;
    private boolean isRequestCompleted = false;

    /*
     * This instance variable is for avoiding multiple execution of requests
     * on the same RequestExecuter object. We know it is not the perfect solution
     * (as it does not synchronize access to shared variable), but is
     * fine for finding this type of error during development phase.
     */
    private boolean isRequestStarted = false;

    @Override
    public void execute(Request request, View... views) {
        // Verify if this is the first call to this object:
        if(isRequestStarted){
            throw new MultipleRequestInSameRequestExecuterException(
                    "A RequestExecuter object can be used only once!");
        }
        isRequestStarted = true;

        // Proceed with execution:
        for(View view: views){
            view.doStart(request);
        }

        // Needed for specifying HTTP pre-emptive authentication:
        HttpContext httpContext = null;

        // Create all the builder objects:
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();
        final ClassicRequestBuilder reqBuilder = ClassicRequestBuilder.create(
                request.getMethod().name());

        // Retry handler (no-retries):
        hcBuilder.setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ZERO_MILLISECONDS));

        // Url:
        final URL url = IDNUtil.getIDNizedURL(request.getUrl());
        final String urlHost = url.getHost();
        final int urlPort = url.getPort()==-1?url.getDefaultPort():url.getPort();
        final String urlProtocol = url.getProtocol();
        final String urlStr = url.toString();
        reqBuilder.setUri(urlStr);

        // Set HTTP version:
        HTTPVersion httpVersion = request.getHttpVersion();
        ProtocolVersion protocolVersion =
                httpVersion==HTTPVersion.HTTP_1_1? new ProtocolVersion("HTTP", 1, 1):
                    new ProtocolVersion("HTTP", 1, 0);
        reqBuilder.setVersion(protocolVersion);

        // Set request timeout (default 1 minute--60000 milliseconds)
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        rcBuilder.setConnectionRequestTimeout(Timeout.ofMilliseconds(
                Long.parseLong(options.getProperty("request-timeout-in-millis"))));

        // Set proxy
        ProxyConfig proxy = ProxyConfig.getInstance();
        proxy.acquire();
        if (proxy.isEnabled()) {
            final HttpHost proxyHost = new HttpHost("http", proxy.getHost(), proxy.getPort());
            if (proxy.isAuthEnabled()) {
                CredentialsProvider credsProvider = new CredentialsProviderBuilder().add(
                        new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword())).build();
                hcBuilder.setDefaultCredentialsProvider(credsProvider);
            }
            hcBuilder.setProxy(proxyHost);
        }
        proxy.release();

        // HTTP Authentication
        if(request.getAuth() != null) {
            // Add auth preference:
            Auth auth = request.getAuth();
            List<String> authPrefs = new ArrayList<>();
            if(auth instanceof BasicAuth) {
                authPrefs.add("BASIC");
            }
            else if(auth instanceof DigestAuth) {
                authPrefs.add("DIGEST");
            }
            else if(auth instanceof NtlmAuth) {
                authPrefs.add("NTLM");
            }
            rcBuilder.setTargetPreferredAuthSchemes(authPrefs);

            // BASIC & DIGEST:
            if(auth instanceof BasicAuth || auth instanceof DigestAuth) {
                BasicDigestAuth a = (BasicDigestAuth) auth;
                String uid = a.getUsername();
                String pwd = new String(a.getPassword());
                String host = StringUtil.isEmpty(a.getHost()) ? urlHost : a.getHost();
                String realm = StringUtil.isEmpty(a.getRealm()) ? null : a.getRealm();

                CredentialsProvider credsProvider = new CredentialsProviderBuilder()
                        // realm needs to be added to AuthScope!
                        .add(new AuthScope(host, urlPort), new UsernamePasswordCredentials(uid, pwd.toCharArray()))
                        .build();
                hcBuilder.setDefaultCredentialsProvider(credsProvider);

                // preemptive mode:
                if (a.isPreemptive()) {
                    httpContext = ContextBuilder.create()
                            .preemptiveBasicAuth(
                                    new HttpHost(urlProtocol, urlHost, urlPort),
                                    new UsernamePasswordCredentials(uid, pwd.toCharArray())
                            ).build();
                }
            }

            // NTLM:
            if(auth instanceof NtlmAuth) {
                NtlmAuth a = (NtlmAuth) auth;
                String uid = a.getUsername();
                String pwd = new String(a.getPassword());
                CredentialsProvider credsProvider = new CredentialsProviderBuilder()
                        .add(
                                (AuthScope) null,
                                new NTCredentials(uid, pwd.toCharArray(), a.getWorkstation(), a.getDomain())
                        ).build();
                hcBuilder.setDefaultCredentialsProvider(credsProvider);
            }

            // Authorization header
            // Logic written in same place where Header is processed--a little down!
        }

        try {

            { // Authorization Header Authentication:
                Auth auth = request.getAuth();
                if(auth instanceof AuthorizationHeaderAuth) {
                    AuthorizationHeaderAuth a = (AuthorizationHeaderAuth) auth;
                    final String authHeader = a.getAuthorizationHeaderValue();
                    if(StringUtil.isNotEmpty(authHeader)) {
                        Header header = new BasicHeader("Authorization", authHeader);
                        reqBuilder.addHeader(header);
                    }
                }
            }

            // Get request headers
            MultiValueMap<String, String> header_data = request.getHeaders();
            for (String key : header_data.keySet()) {
                for(String value: header_data.get(key)) {
                    Header header = new BasicHeader(key, value);

                    reqBuilder.addHeader(header);
                }
            }

            // Cookies
            {
                // Set cookie policy:
                rcBuilder.setCookieSpec(null); // null is default!

                // Add to CookieStore:
                CookieStore store = new RESTClientCookieStore();
                List<HttpCookie> cookies = request.getCookies();
                for(HttpCookie cookie: cookies) {
                    BasicClientCookie c = new BasicClientCookie(
                            cookie.getName(), cookie.getValue());
                    // c.setVersion(cookie.getVersion());
                    c.setDomain(urlHost);
                    c.setPath("/");

                    store.addCookie(c);
                }

                // Attach store to client:
                hcBuilder.setDefaultCookieStore(store);
            }

            // POST/PUT/PATCH/DELETE method specific logic
            if (HttpUtil.isEntityEnclosingMethod(reqBuilder.getMethod())) {

                // Create and set RequestEntity
                ReqEntity bean = request.getBody();
                if (bean != null) {
                    try {
                        if(bean instanceof ReqEntitySimple) {
                            AbstractHttpEntity e = HTTPClientUtil.getEntity((ReqEntitySimple)bean);

                            reqBuilder.setEntity(e);
                        }
                        else if(bean instanceof ReqEntityMultipart multipart) {
                            MultipartEntityBuilder meb = MultipartEntityBuilder.create();

                            // multipart/mixed / multipart/form-data:
                            meb.setMimeSubtype(multipart.getSubtype().toString());

                            // Format:
                            MultipartMode mpMode = multipart.getMode();
                            switch(mpMode) {
                                case LEGACY:
                                    meb.setMode(HttpMultipartMode.LEGACY);
                                    break;
                                case EXTENDED:
                                    meb.setMode(HttpMultipartMode.EXTENDED);
                                    break;
                                case STRICT:
                                    meb.setMode(HttpMultipartMode.STRICT);
                                    break;
                            }

                            // Parts:
                            for(ReqEntityPart part: multipart.getBody()) {
                                ContentBody cb = null;
                                if(part instanceof ReqEntityStringPart) {
                                    ReqEntityStringPart p = (ReqEntityStringPart)part;
                                    String body = p.getPart();
                                    ContentType ct = p.getContentType();
                                    if(ct != null) {
                                        cb = new StringBody(body, HTTPClientUtil.getContentType(ct));
                                    }
                                    else {
                                        cb = new StringBody(body, org.apache.hc.core5.http.ContentType.DEFAULT_TEXT);
                                    }

                                }
                                else if(part instanceof ReqEntityFilePart) {
                                    ReqEntityFilePart p = (ReqEntityFilePart)part;
                                    File body = p.getPart();
                                    ContentType ct = p.getContentType();
                                    if(ct != null) {
                                        cb = new FileBody(body, HTTPClientUtil.getContentType(ct), p.getFilename());
                                    }
                                    else {
                                        cb = new FileBody(body, org.apache.hc.core5.http.ContentType.DEFAULT_BINARY, p.getFilename());
                                    }
                                }
                                FormBodyPartBuilder bodyPart = FormBodyPartBuilder
                                        .create()
                                        .setName(part.getName())
                                        .setBody(cb);
                                MultiValueMap<String, String> fields = part.getFields();
                                for(String key: fields.keySet()) {
                                    for(String value: fields.get(key)) {
                                        bodyPart.addField(key, value);
                                    }
                                }
                                meb.addPart(bodyPart.build());
                            }

                            reqBuilder.setEntity(meb.build());
                        }

                    }
                    catch (UnsupportedEncodingException ex) {
                        for(View view: views){
                            view.doError(Util.getStackTrace(ex));
                            view.doEnd();
                        }
                        return;
                    }
                }
            }

            // SSL

            // Set the hostname verifier:
            final SSLReq sslReq = request.getSslReq();
            if(sslReq != null) {
                SSLHostnameVerifier verifier = sslReq.getHostNameVerifier();
                final HostnameVerifier hcVerifier;
                switch(verifier) {
                    case ALLOW_ALL:
                        hcVerifier = new NoopHostnameVerifier();
                        break;
                    case STRICT:
                    default:
                        hcVerifier = new DefaultHostnameVerifier();
                        break;
                }

                // Register the SSL Scheme:
                final KeyStore trustStore  = sslReq.getTrustStore() == null?
                        null:
                        sslReq.getTrustStore().getKeyStore();
                final KeyStore keyStore = sslReq.getKeyStore() == null?
                        null:
                        sslReq.getKeyStore().getKeyStore();

                final TrustStrategy trustStrategy = sslReq.isTrustAllCerts()
                        ? new TrustAllStrategy(): null;

                SSLContext ctx = new SSLContextBuilder()
                        .loadKeyMaterial(keyStore, sslReq.getKeyStore()!=null? sslReq.getKeyStore().getPassword(): null)
                        .loadTrustMaterial(trustStore, trustStrategy)
                        .setSecureRandom(null)
                        .setProtocol("TLS")
                        .build();
                final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                        .setTlsSocketStrategy(new DefaultClientTlsStrategy(ctx, hcVerifier))
                        .build();
                hcBuilder.setConnectionManager(cm);
            }

            // How to handle redirects:
            rcBuilder.setRedirectsEnabled(request.isFollowRedirect());

            // Now Execute:
            final long startTime = System.currentTimeMillis();

            RequestConfig rc = rcBuilder.build();
            hcBuilder.setDefaultRequestConfig(rc);
            httpClient = hcBuilder.build();

            ClassicHttpRequest req = reqBuilder.build();
            httpClient.execute(req, httpContext, new HttpClientResponseHandler<HttpResponse>() {
                @Override
                public HttpResponse handleResponse(ClassicHttpResponse http_res) throws HttpException, IOException {
                    // Create response:
                    ResponseBean response = new ResponseBean();

                    response.setStatusCode(http_res.getCode());
                    response.setStatusLine(http_res.getCode() + " " + http_res.getReasonPhrase());

                    final Header[] responseHeaders = http_res.getHeaders();
                    for (Header header : responseHeaders) {
                        response.addHeader(header.getName(), header.getValue());
                    }

                    // Response body:
                    final HttpEntity entity = http_res.getEntity();
                    if(entity != null) {
                        if(request.isIgnoreResponseBody()) {
                            EntityUtils.consumeQuietly(entity);
                        }
                        else {
                            InputStream is = entity.getContent();
                            try{
                                byte[] responseBody = StreamUtil.inputStream2Bytes(is);
                                if (responseBody != null) {
                                    response.setResponseBody(responseBody);
                                }
                            }
                            catch(IOException ex) {
                                for(View view: views) {
                                    view.doError("Byte array conversion from response body stream failed.");
                                }
                                LOG.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }
                    }

                    // Execution time:
                    final long endTime = System.currentTimeMillis();
                    response.setExecutionTime(endTime - startTime);

                    // Now execute tests:
                    try {
                        junit.framework.TestSuite suite = TestUtil.getTestSuite(request, response);
                        if (suite != null) { // suite will be null if there is no associated script
                            TestResult testResult = TestUtil.execute(suite);
                            response.setTestResult(testResult);
                        }
                    } catch (TestException ex) {
                        for(View view: views){
                            view.doError(Util.getStackTrace(ex));
                        }
                    }

                    for(View view: views){
                        view.doResponse(response);
                    }

                    return http_res;
                }
            });
        }
        catch (IOException | KeyStoreException | InvalidKeySpecException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException | IllegalStateException ex) {
            if(!interruptedShutdown){
                for(View view: views){
                    view.doError(Util.getStackTrace(ex));
                }
            }
            else{
                for(View view: views){
                    view.doCancelled();
                }
            }
        }
        finally {
            if (!interruptedShutdown) {
                // for interrupted shutdown, httpClient is already closed
                // close it only when otherwise:
                try {
                    if(httpClient != null) httpClient.close();
                }
                catch(IOException ex) {
                    LOG.log(Level.WARNING, "Exception when closing httpClient", ex);
                }
            }
            else {
                // reset value to default:
                interruptedShutdown = false;
            }
            for(View view: views){
                view.doEnd();
            }
            isRequestCompleted = true;
        }
    }

    @Override
    public void abortExecution(){
        if(!isRequestCompleted){
            interruptedShutdown = true;
            try {
                if(httpClient != null) httpClient.close();
            }
            catch(IOException ex) {
                LOG.log(Level.WARNING, "Exception when closing httpClient", ex);
            }
        }
        else{
            LOG.info("Request already completed. Doing nothing.");
        }
    }
}
