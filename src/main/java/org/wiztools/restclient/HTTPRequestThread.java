package org.wiztools.restclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.TestSuite;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.wiztools.restclient.test.TestException;
import org.wiztools.restclient.test.TestUtil;

/**
 *
 * @author subwiz
 */
public class HTTPRequestThread extends Thread {

    private RequestBean request;
    private View view;

    public HTTPRequestThread(final RequestBean request,
            final View view) {
        this.request = request;
        this.view = view;
    }

    @Override
    public void run() {
        view.doStart(request);

        URL url = request.getUrl();
        String urlHost = url.getHost();
        int urlPort = url.getPort()==-1?url.getDefaultPort():url.getPort();
        String urlProtocol = url.getProtocol();
        String urlStr = url.toString();

        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        // Set HTTP version
        HTTPVersion httpVersion = request.getHttpVersion();
        ProtocolVersion protocolVersion = 
                httpVersion==HTTPVersion.HTTP_1_1? new ProtocolVersion("HTTP", 1, 1):
                    new ProtocolVersion("HTTP", 1, 0);
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                protocolVersion);

        // Set request timeout (default 1 minute--60000 milliseconds)
        GlobalOptions options = GlobalOptions.getInstance();
        options.acquire();
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), options.getRequestTimeoutInMillis());
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
        boolean authEnabled = request.getAuthMethods().size() > 0 ? true : false;
        if (authEnabled) {
            String uid = request.getAuthUsername();
            String pwd = new String(request.getAuthPassword());
            String host = Util.isStrEmpty(request.getAuthHost()) ? urlHost : request.getAuthHost();
            String realm = Util.isStrEmpty(request.getAuthRealm()) ? AuthScope.ANY_REALM : request.getAuthRealm();
            
            // Type of authentication
            List<String> authPrefs = new ArrayList<String>(2);
            List<String> authMethods = request.getAuthMethods();
            for(String authMethod: authMethods){
                if("BASIC".equals(authMethod)){
                    authPrefs.add(AuthPolicy.BASIC);
                }
                else if("DIGEST".equals(authMethod)){
                    authPrefs.add(AuthPolicy.DIGEST);
                }
            }
            httpclient.getParams().setParameter("http.auth.scheme-pref", authPrefs);

            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(host, urlPort, realm),
                    new UsernamePasswordCredentials(uid, pwd));

            // preemptive mode
            // http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/module-client/src/examples/org/apache/http/examples/client/ClientPreemptiveBasicAuthentication.java
            if (request.isAuthPreemptive()) {
                BasicHttpContext localcontext = new BasicHttpContext();
                BasicScheme basicAuth = new BasicScheme();
                localcontext.setAttribute("preemptive-auth", basicAuth);
                httpclient.addRequestInterceptor(new PreemptiveAuth(), 0);
            }
        }

        AbstractHttpMessage method = null;

        String httpMethod = request.getMethod();
        try {
            if ("GET".equals(httpMethod)) {
                method = new HttpGet(urlStr);
            } else if ("HEAD".equals(httpMethod)) {
                method = new HttpHead(urlStr);
            } else if ("POST".equals(httpMethod)) {
                method = new HttpPost(urlStr);
            } else if ("PUT".equals(httpMethod)) {
                method = new HttpPut(urlStr);
            } else if ("DELETE".equals(httpMethod)) {
                method = new HttpDelete(urlStr);
            } else if ("OPTIONS".equals(httpMethod)) {
                method = new HttpOptions(urlStr);
            } else if ("TRACE".equals(httpMethod)) {
                method = new HttpTrace(urlStr);
            }
            method.setParams(new BasicHttpParams().setParameter(urlStr, url));

            // Get request headers
            Map<String, String> header_data = request.getHeaders();
            for (String key : header_data.keySet()) {
                String value = header_data.get(key);
                Header header = new BasicHeader(key, value);
                method.addHeader(header);
            }

            // POST/PUT method specific logic
            if (method instanceof HttpEntityEnclosingRequest) {

                HttpEntityEnclosingRequest eeMethod = (HttpEntityEnclosingRequest) method;

                // Create and set RequestEntity
                ReqEntityBean bean = request.getBody();
                if (bean != null) {
                    try {
                        StringEntity entity = new StringEntity(bean.getBody());
                        entity.setContentType(bean.getContentTypeCharsetFormatted());
                        eeMethod.setEntity(entity);
                    } catch (UnsupportedEncodingException ex) {
                        view.doError(Util.getStackTrace(ex));
                        view.doEnd();
                        return;
                    }
                }
            }
            
            // SSL
            String trustStorePath = request.getSslTrustStore();
            char[] trustStorePassword = request.getSslTrustStorePassword();
            if(urlProtocol.equalsIgnoreCase("https") && !Util.isStrEmpty(trustStorePath)){
                KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
                FileInputStream instream = new FileInputStream(new File(trustStorePath));
                try{
                    trustStore.load(instream, trustStorePassword);
                }
                finally{
                    instream.close();
                }
                
                SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
                Scheme sch = new Scheme(urlProtocol, socketFactory, urlPort);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            }

            httpclient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());

            // Now Execute:
            long startTime = System.currentTimeMillis();
            HttpResponse http_res = httpclient.execute((HttpUriRequest) method);
            long endTime = System.currentTimeMillis();

            ResponseBean response = new ResponseBean();
            
            response.setExecutionTime(endTime - startTime);

            response.setStatusCode(http_res.getStatusLine().getStatusCode());
            response.setStatusLine(http_res.getStatusLine().toString());

            final Header[] responseHeaders = http_res.getAllHeaders();
            for (Header header : responseHeaders) {
                response.addHeader(header.getName(), header.getValue());
            }

            HttpEntity entity = http_res.getEntity();
            if(entity != null){
                InputStream is = entity.getContent();
                String responseBody = Util.inputStream2String(is);
                if (responseBody != null) {
                    response.setResponseBody(responseBody);
                }
            }

            // Now execute tests:
            try {
                TestSuite suite = TestUtil.getTestSuite(request, response);
                if (suite != null) { // suite will be null if there is no associated script
                    String testResult = TestUtil.execute(suite);
                    response.setTestResult(testResult);
                }
            } catch (TestException ex) {
                view.doError(Util.getStackTrace(ex));
            }

            view.doResponse(response);
        } /*catch (HttpException ex) {
            view.doError(Util.getStackTrace(ex));
        }*/ catch (IOException ex) {
            view.doError(Util.getStackTrace(ex));
        } catch (Exception ex) {
            view.doError(Util.getStackTrace(ex));
        } finally {
            if (method != null) {
                httpclient.getConnectionManager().shutdown();
            }
            view.doEnd();
        }
    }
    
    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(
                final HttpRequest request, 
                final HttpContext context) throws HttpException, IOException {
            
            AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);
            
            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(), 
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }
            
        }
        
    }
}
