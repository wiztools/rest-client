/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 *
 * @author schandran
 */
public class HTTPRequestThread extends Thread {
    
    private RequestBean request;
    private View view;
    
    public HTTPRequestThread(final RequestBean request,
            final View view){
        this.request = request;
        this.view = view;
    }
    
    @Override
    public void run(){
        view.doStart(request);
        
        URL url = request.getUrl();
        String urlStr = url.toString();
        
        HttpClient client = new HttpClient();
        
        boolean authEnabled = request.getAuthMethods().size()>0?true:false;
        
        if(authEnabled){
            // Type of authentication
            List authPrefs = new ArrayList(2);
            List<String> authMethods = request.getAuthMethods();
            for(String authMethod: authMethods){
                if("BASIC".equals(authMethod)){
                    authPrefs.add(AuthPolicy.BASIC);
                }
                else if("DIGEST".equals(authMethod)){
                    authPrefs.add(AuthPolicy.DIGEST);
                }
            }
            client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
            
            // Pass the credentials
            String uid = request.getAuthUsername();
            String pwd = new String(request.getAuthPassword());
            Credentials creds = new UsernamePasswordCredentials(uid, pwd);
            
            String host = Util.isStrEmpty(request.getAuthHost()) ?
                AuthScope.ANY_HOST: request.getAuthHost();
            String realm = Util.isStrEmpty(request.getAuthRealm()) ?
                AuthScope.ANY_REALM: request.getAuthRealm();
            int port = url.getPort();
            client.getState().setCredentials(new AuthScope(host, port, realm), creds);
            
            // preemptive mode
            if(request.isAuthPreemptive()){
                client.getParams().setAuthenticationPreemptive(true);
            }
        }
        
        HttpMethod method = null;
        
        String httpMethod = request.getMethod();
        if("GET".equals(httpMethod)){
            method = new GetMethod(urlStr);
        }
        else if("HEAD".equals(httpMethod)){
            method = new HeadMethod(urlStr);
        }
        else if("POST".equals(httpMethod)){
            method = new PostMethod(urlStr);
        }
        else if("PUT".equals(httpMethod)){
            method = new PutMethod(urlStr);
        }
        else if("DELETE".equals(httpMethod)){
            method = new DeleteMethod(urlStr);
        }
        else if("OPTIONS".equals(httpMethod)){
            method = new OptionsMethod(urlStr);
        }
        else if("TRACE".equals(httpMethod)){
            method = new TraceMethod(urlStr);
        }
        
        // Get request headers
        Map<String, String> header_data = request.getHeaders();
        for(String key: header_data.keySet()){
            String value = header_data.get(key);
            Header header = new Header(key, value);
            method.addRequestHeader(header);
        }
        
        // POST/PUT method specific logic
        if(method instanceof EntityEnclosingMethod){

            EntityEnclosingMethod eeMethod = (EntityEnclosingMethod)method;
            
            // Create and set RequestEntity
            ReqEntityBean bean = request.getBody();
            if(bean != null){
                try{

                    RequestEntity entity = new StringRequestEntity(
                            bean.getBody(), bean.getContentType(), bean.getCharSet());
                    eeMethod.setRequestEntity(entity);
                }
                catch(UnsupportedEncodingException ex){
                    view.doError(Util.getStackTrace(ex));
                    view.doEnd();
                    return;
                }
            }
        }
        
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
                    new DefaultHttpMethodRetryHandler());
        
        try{
            int statusCode = client.executeMethod(method);
            
            ResponseBean response = new ResponseBean();
            
            response.setStatusCode(statusCode);
            response.setStatusLine(method.getStatusLine().toString());
            
            final Header[] responseHeaders = method.getResponseHeaders();
            for(Header header: responseHeaders){
                response.addHeader(header.getName(), header.getValue());
            }
            
            InputStream is = method.getResponseBodyAsStream();
            String responseBody = Util.inputStream2String(is);
            if(responseBody != null){
                response.setResponseBody(responseBody);
            }
            
            view.doResponse(response);
        }
        catch(HttpException ex){
            view.doError(Util.getStackTrace(ex));
        }
        catch(IOException ex){
            view.doError(Util.getStackTrace(ex));
        }
        
        method.releaseConnection();
        
        view.doEnd();
    }
}
