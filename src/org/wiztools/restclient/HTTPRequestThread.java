/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 *
 * @author schandran
 */
public class HTTPRequestThread extends Thread {
    
    private RequestBean request;
    private RESTClientView view;
    
    public HTTPRequestThread(final RequestBean request,
            final RESTClientView view){
        this.request = request;
        this.view = view;
    }
    
    @Override
    public void run(){
        view.freeze();
        
        boolean authEnabled = request.isIsAuthEnabled();
        
        HttpClient client = new HttpClient();
        
        if(authEnabled){
            // Set to default preemptive mode
            client.getParams().setAuthenticationPreemptive(true);
            
            // Type of authentication
            List authPrefs = new ArrayList(1);
            String authMethod = request.getAuthMethod();
            if("BASIC".equals(authMethod)){
                authPrefs.add(AuthPolicy.BASIC);
            }
            else if("DIGEST".equals(authMethod)){
                authPrefs.add(AuthPolicy.DIGEST);
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
            client.getState().setCredentials(new AuthScope(host, 80, realm), creds);
        }
        
        HttpMethod method = null;
        String url = (String)request.getUrl();
        String httpMethod = request.getMethod();
        if("GET".equals(httpMethod)){
            method = new GetMethod(url);
        }
        else if("HEAD".equals(httpMethod)){
            method = new HeadMethod(url);
        }
        else if("POST".equals(httpMethod)){
            method = new PostMethod(url);
        }
        else if("PUT".equals(httpMethod)){
            method = new PutMethod(url);
        }
        else if("DELETE".equals(httpMethod)){
            method = new DeleteMethod(url);
        }
        else if("OPTIONS".equals(httpMethod)){
            method = new OptionsMethod(url);
        }
        else if("TRACE".equals(httpMethod)){
            method = new TraceMethod(url);
        }
        
        // Get request headers
        Map<String, String> data = request.getHeaders();
        for(String key: data.keySet()){
            String value = data.get(key);
            Header header = new Header(key, value);
            method.addRequestHeader(header);
        }
        
        //method.
        
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
                    new DefaultHttpMethodRetryHandler());
        
        try{
            int statusCode = client.executeMethod(method);
            ResponseBean response = new ResponseBean();
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
            
            view.ui_update_response(response);
        }
        catch(HttpException ex){
            view.showErrorDialog(Util.getStackTrace(ex));
        }
        catch(IOException ex){
            view.showErrorDialog(Util.getStackTrace(ex));
        }
        
        method.releaseConnection();
        
        view.unfreeze();
    }
}
