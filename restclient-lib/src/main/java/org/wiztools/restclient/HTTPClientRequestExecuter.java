package org.wiztools.restclient;

import java.io.*;
import java.net.HttpCookie;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StreamUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.http.EntityEnclosingDelete;
import org.wiztools.restclient.http.NoValidationCookieSpecFactory;
import org.wiztools.restclient.http.RESTClientCookieStore;

/**
 *
 * @author subwiz
 */
public class HTTPClientRequestExecuter implements RequestExecuter {

    private static final Logger LOG = Logger.getLogger(HTTPClientRequestExecuter.class.getName());

    private DefaultHttpClient httpclient;

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

        final URL url = request.getUrl();
        final String urlHost = url.getHost();
        final int urlPort = url.getPort()==-1?url.getDefaultPort():url.getPort();
        final String urlProtocol = url.getProtocol();
        final String urlStr = url.toString();

        // Needed for specifying HTTP pre-emptive authentication
        HttpContext httpContext = null;

        httpclient = new DefaultHttpClient();

        // Set HTTP version
        HTTPVersion httpVersion = request.getHttpVersion();
        ProtocolVersion protocolVersion =
                httpVersion==HTTPVersion.HTTP_1_1? new ProtocolVersion("HTTP", 1, 1):
                    new ProtocolVersion("HTTP", 1, 0);
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                protocolVersion);

        // Set request timeout (default 1 minute--60000 milliseconds)
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        options.acquire();
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
                Integer.parseInt(options.getProperty("request-timeout-in-millis")));
        options.release();

        // Set proxy
        ProxyConfig proxy = ProxyConfig.getInstance();
        proxy.acquire();
        if (proxy.isEnabled()) {
            final HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort(), "http");
            if (proxy.isAuthEnabled()) {
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), new String(proxy.getPassword())));
            }
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        }
        proxy.release();

        // HTTP Authentication
        boolean authEnabled = !request.getAuthMethods().isEmpty();
        if (authEnabled) {
            // Type of authentication
            List<String> authPrefs = new ArrayList<String>(2);
            List<HTTPAuthMethod> authMethods = request.getAuthMethods();
            for(HTTPAuthMethod authMethod: authMethods){
                switch(authMethod){
                    case BASIC:
                        authPrefs.add(AuthPolicy.BASIC);
                        break;
                    case DIGEST:
                        authPrefs.add(AuthPolicy.DIGEST);
                        break;
                    case NTLM:
                        authPrefs.add(AuthPolicy.NTLM);
                        break;
                }
            }
            httpclient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authPrefs);

            // BASIC & DIGEST:
            if(request.getAuthMethods().contains(HTTPAuthMethod.BASIC)
                    || request.getAuthMethods().contains(HTTPAuthMethod.DIGEST)) {
                String uid = request.getAuthUsername();
                String pwd = new String(request.getAuthPassword());
                String host = StringUtil.isEmpty(request.getAuthHost()) ? urlHost : request.getAuthHost();
                String realm = StringUtil.isEmpty(request.getAuthRealm()) ? AuthScope.ANY_REALM : request.getAuthRealm();
            
                httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(host, urlPort, realm),
                    new UsernamePasswordCredentials(uid, pwd));
                
                // preemptive mode
                if (request.isAuthPreemptive()) {
                    AuthCache authCache = new BasicAuthCache();
                    AuthSchemeBase authScheme = request.getAuthMethods().contains(HTTPAuthMethod.BASIC)?
                            new BasicScheme(): new DigestScheme();
                    authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
                    BasicHttpContext localcontext = new BasicHttpContext();
                    localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                    httpContext = localcontext;
                }
            }
            
            // NTLM:
            if(request.getAuthMethods().contains(HTTPAuthMethod.NTLM)) {
                String uid = request.getAuthUsername();
                String pwd = new String(request.getAuthPassword());
                
                httpclient.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new NTCredentials(uid, pwd,
                        request.getAuthWorkstation(), request.getAuthDomain()));
            }

            // OAuth2 Bearer
            if(request.getAuthMethods().contains(HTTPAuthMethod.OAUTH_20_BEARER)) {
                // Logic written in same place where Header is processed--a little down!
            }
        }

        AbstractHttpMessage method = null;

        final HTTPMethod httpMethod = request.getMethod();
        try {
            switch(httpMethod){
                case GET:
                    method = new HttpGet(urlStr);
                    break;
                case POST:
                    method = new HttpPost(urlStr);
                    break;
                case PUT:
                    method = new HttpPut(urlStr);
                    break;
                case PATCH:
                    method = new HttpPatch(urlStr);
                    break;
                case DELETE:
                    method = new EntityEnclosingDelete(urlStr);
                    break;
                case HEAD:
                    method = new HttpHead(urlStr);
                    break;
                case OPTIONS:
                    method = new HttpOptions(urlStr);
                    break;
                case TRACE:
                    method = new HttpTrace(urlStr);
                    break;
            }
            method.setParams(new BasicHttpParams().setParameter(urlStr, url));
            
            // OAuth 2 Bearer Authentication:
            if(request.getAuthMethods().contains(HTTPAuthMethod.OAUTH_20_BEARER)) {
                final String bearerToken = request.getAuthBearerToken();
                if(StringUtil.isNotEmpty(bearerToken)) {
                    Header header = new BasicHeader("Authorization", "Bearer " + bearerToken);
                    method.addHeader(header);
                }
            }

            // Get request headers
            MultiValueMap<String, String> header_data = request.getHeaders();
            for (String key : header_data.keySet()) {
                for(String value: header_data.get(key)) {
                    Header header = new BasicHeader(key, value);
                    method.addHeader(header);
                }
            }
            
            // Cookies
            {
                // Set cookie policy:
                httpclient.getCookieSpecs().register(
                        NoValidationCookieSpecFactory.NAME, new NoValidationCookieSpecFactory());
                httpclient.getParams().setParameter(
                        ClientPNames.COOKIE_POLICY, NoValidationCookieSpecFactory.NAME);
                
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
                httpclient.setCookieStore(store);
            }    

            // POST/PUT/PATCH/DELETE method specific logic
            if (method instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest eeMethod = (HttpEntityEnclosingRequest) method;

                // Create and set RequestEntity
                ReqEntity bean = request.getBody();
                if (bean != null) {
                    try {
                        if(bean instanceof ReqEntitySimple) {
                            AbstractHttpEntity e = HTTPClientUtil.getEntity((ReqEntitySimple)bean);
                            e.setContentType(bean.getContentTypeCharsetFormatted());
                            eeMethod.setEntity(e);
                        }
                        else if(bean instanceof ReqEntityMultipart) {
                            ReqEntityMultipart multipart = (ReqEntityMultipart)bean;
                            MultipartEntity me = new MultipartEntity();
                            for(ReqEntityPart part: multipart.getBody()) {
                                if(part instanceof ReqEntityStringPart) {
                                    String body = ((ReqEntityStringPart)part).getPart();
                                    me.addPart(part.getName(), new StringBody(body));
                                }
                                else if(part instanceof ReqEntityFilePart) {
                                    File body = ((ReqEntityFilePart)part).getPart();
                                    me.addPart(part.getName(), new FileBody(body));
                                }
                            }
                            eeMethod.setEntity(me);
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
            SSLHostnameVerifier verifier = request.getSslHostNameVerifier();
            final X509HostnameVerifier hcVerifier;
            switch(verifier){
                case STRICT:
                    hcVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
                    break;
                case BROWSER_COMPATIBLE:
                    hcVerifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
                    break;
                case ALLOW_ALL:
                    hcVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                    break;
                default:
                    hcVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
                    break;
            }

            // Register the SSL Scheme:
            if(urlProtocol.equalsIgnoreCase("https")){
                final String trustStorePath = request.getSslTrustStore();
                final String keyStorePath = request.getSslKeyStore();

                final KeyStore trustStore  = StringUtil.isEmpty(trustStorePath)?
                        null:
                        getKeyStore(trustStorePath, request.getSslTrustStorePassword());
                final KeyStore keyStore = StringUtil.isEmpty(keyStorePath)?
                        null:
                	getKeyStore(keyStorePath, request.getSslKeyStorePassword());
                
                final TrustStrategy trustStrategy = request.isSslTrustSelfSignedCert()
                        ? new TrustSelfSignedStrategy(): null;
                
                SSLSocketFactory socketFactory = new SSLSocketFactory(
                        "TLS", // Algorithm
                        keyStore,  // Keystore
                        new String(request.getSslKeyStorePassword()),  // Keystore password
                        trustStore,
                        null,  // Secure Random
                        trustStrategy, // Trust strategy
                        hcVerifier);
                Scheme sch = new Scheme(urlProtocol, urlPort, socketFactory);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            }

            // How to handle retries and redirects:
            httpclient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());
            httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
                    request.isFollowRedirect());

            // Now Execute:
            long startTime = System.currentTimeMillis();
            HttpResponse http_res = httpclient.execute((HttpUriRequest) method,
                    httpContext);
            long endTime = System.currentTimeMillis();

            ResponseBean response = new ResponseBean();

            response.setExecutionTime(endTime - startTime);

            response.setStatusCode(http_res.getStatusLine().getStatusCode());
            response.setStatusLine(http_res.getStatusLine().toString());

            final Header[] responseHeaders = http_res.getAllHeaders();
            String contentType = null;
            for (Header header : responseHeaders) {
                response.addHeader(header.getName(), header.getValue());
                if(header.getName().equalsIgnoreCase("content-type")) {
                    contentType = header.getValue();
                }
            }

            // find out the charset:
            final Charset charset;
            {
                Charset c;
                if(contentType != null) {
                    final String charsetStr = Util.getCharsetFromContentType(contentType);
                    try{
                        c = Charset.forName(charsetStr);
                    }
                    catch(IllegalCharsetNameException ex) {
                        LOG.log(Level.WARNING, "Charset name is illegal: {0}", charsetStr);
                        c = Charset.defaultCharset();
                    }
                    catch(UnsupportedCharsetException ex) {
                        LOG.log(Level.WARNING, "Charset {0} is not supported in this JVM.", charsetStr);
                        c = Charset.defaultCharset();
                    }
                    catch(IllegalArgumentException ex) {
                        LOG.log(Level.WARNING, "Charset parameter is not available in Content-Type header!");
                        c = Charset.defaultCharset();
                    }
                }
                else {
                    c = Charset.defaultCharset();
                    LOG.log(Level.WARNING, "Content-Type header not available in response. Using platform default encoding: {0}", c.name());
                }
                charset = c;
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
                        final String msg = "Response body conversion to string using "
                                + charset.displayName()
                                + " encoding failed. Response body not set!";

                        for(View view: views) {
                            view.doError(msg);
                        }
                        LOG.log(Level.WARNING, msg);
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
        catch (IOException ex) {
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
        } catch (Exception ex) {
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
        } finally {
            if (method != null && !interruptedShutdown) {
                httpclient.getConnectionManager().shutdown();
            }
            for(View view: views){
                view.doEnd();
            }
            isRequestCompleted = true;
        }
    }

    private KeyStore getKeyStore(String storePath, char[] storePassword)
            throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {
        KeyStore store  = KeyStore.getInstance(KeyStore.getDefaultType());
        if(StringUtil.isNotEmpty(storePath)) {
            FileInputStream instream = new FileInputStream(new File(storePath));
            try{
                store.load(instream, storePassword);
            }
            finally{
                instream.close();
            }
        }
        return store;
    }

    @Override
    public void abortExecution(){
        if(!isRequestCompleted){
            ClientConnectionManager conMgr = httpclient.getConnectionManager();
            interruptedShutdown = true;
            conMgr.shutdown();
        }
        else{
            LOG.info("Request already completed. Doing nothing.");
        }
    }    
}
