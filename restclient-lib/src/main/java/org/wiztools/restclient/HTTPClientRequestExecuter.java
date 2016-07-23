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
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StreamUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.http.RESTClientCookieStore;
import org.wiztools.restclient.http.TrustAllTrustStrategy;
import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.IDNUtil;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class HTTPClientRequestExecuter implements RequestExecuter {

    private static final Logger LOG = Logger.getLogger(HTTPClientRequestExecuter.class.getName());

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
        final RequestBuilder reqBuilder = RequestBuilder.create(
                request.getMethod().name());
        
        // Retry handler (no-retries):
        hcBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        
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
        rcBuilder.setConnectionRequestTimeout(
                Integer.parseInt(options.getProperty("request-timeout-in-millis")));

        // Set proxy
        ProxyConfig proxy = ProxyConfig.getInstance();
        proxy.acquire();
        if (proxy.isEnabled()) {
            final HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort(), "http");
            if (proxy.isAuthEnabled()) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), new String(proxy.getPassword())));
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
                authPrefs.add(AuthSchemes.BASIC);
            }
            else if(auth instanceof DigestAuth) {
                authPrefs.add(AuthSchemes.DIGEST);
            }
            else if(auth instanceof NtlmAuth) {
                authPrefs.add(AuthSchemes.NTLM);
            }
            rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
            
            // BASIC & DIGEST:
            if(auth instanceof BasicAuth || auth instanceof DigestAuth) {
                BasicDigestAuth a = (BasicDigestAuth) auth;
                String uid = a.getUsername();
                String pwd = new String(a.getPassword());
                String host = StringUtil.isEmpty(a.getHost()) ? urlHost : a.getHost();
                String realm = StringUtil.isEmpty(a.getRealm()) ? AuthScope.ANY_REALM : a.getRealm();
                
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(host, urlPort, realm),
                        new UsernamePasswordCredentials(uid, pwd));
                hcBuilder.setDefaultCredentialsProvider(credsProvider);
                
                // preemptive mode:
                if (a.isPreemptive()) {
                    AuthCache authCache = new BasicAuthCache();
                    AuthSchemeBase authScheme = a instanceof BasicAuth?
                            new BasicScheme(): new DigestScheme();
                    authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
                    HttpClientContext localContext = HttpClientContext.create();
                    localContext.setAuthCache(authCache);
                    httpContext = localContext;
                }
            }
            
            // NTLM:
            if(auth instanceof NtlmAuth) {
                NtlmAuth a = (NtlmAuth) auth;
                String uid = a.getUsername();
                String pwd = new String(a.getPassword());
                
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        AuthScope.ANY,
                        new NTCredentials(
                                uid, pwd, a.getWorkstation(), a.getDomain()));
                hcBuilder.setDefaultCredentialsProvider(credsProvider);
            }
            
            // Authorization header
            // Logic written in same place where Header is processed--a little down!
        }

        try {
            
            { // Authorization Header Authentication:
                Auth auth = request.getAuth();
                if(auth != null && auth instanceof AuthorizationHeaderAuth) {
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
                rcBuilder.setCookieSpec(CookieSpecs.DEFAULT);
                
                // Add to CookieStore:
                CookieStore store = new RESTClientCookieStore();
                List<HttpCookie> cookies = request.getCookies();
                for(HttpCookie cookie: cookies) {
                    BasicClientCookie c = new BasicClientCookie(
                            cookie.getName(), cookie.getValue());
                    c.setVersion(cookie.getVersion());
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
                        else if(bean instanceof ReqEntityMultipart) {
                            ReqEntityMultipart multipart = (ReqEntityMultipart)bean;
                            
                            MultipartEntityBuilder meb = MultipartEntityBuilder.create();
                            
                            // multipart/mixed / multipart/form-data:
                            meb.setMimeSubtype(multipart.getSubtype().toString());
                            
                            // Format:
                            MultipartMode mpMode = multipart.getMode();
                            switch(mpMode) {
                                case BROWSER_COMPATIBLE:
                                    meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                                    break;
                                case RFC_6532:
                                    meb.setMode(HttpMultipartMode.RFC6532);
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
                                        cb = new StringBody(body, org.apache.http.entity.ContentType.DEFAULT_TEXT);
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
                                        cb = new FileBody(body, org.apache.http.entity.ContentType.DEFAULT_BINARY, p.getFilename());
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
                        ? new TrustAllTrustStrategy(): null;
                
                SSLContext ctx = new SSLContextBuilder()
                        .loadKeyMaterial(keyStore, sslReq.getKeyStore()!=null? sslReq.getKeyStore().getPassword(): null)
                        .loadTrustMaterial(trustStore, trustStrategy)
                        .setSecureRandom(null)
                        .useProtocol("TLS")
                        .build();
                SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(ctx, hcVerifier);
                hcBuilder.setSSLSocketFactory(sf);
            }

            // How to handle redirects:
            rcBuilder.setRedirectsEnabled(request.isFollowRedirect());

            // Now Execute:
            long startTime = System.currentTimeMillis();
            
            RequestConfig rc = rcBuilder.build();
            reqBuilder.setConfig(rc);
            HttpUriRequest req = reqBuilder.build();
            httpClient = hcBuilder.build();
            
            HttpResponse http_res = httpClient.execute(req, httpContext);
            
            long endTime = System.currentTimeMillis();
            
            // Create response:
            ResponseBean response = new ResponseBean();

            response.setExecutionTime(endTime - startTime);

            response.setStatusCode(http_res.getStatusLine().getStatusCode());
            response.setStatusLine(http_res.getStatusLine().toString());

            final Header[] responseHeaders = http_res.getAllHeaders();
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
