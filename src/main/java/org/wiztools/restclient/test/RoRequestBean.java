/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.test;

import java.net.URL;
import java.util.List;
import java.util.Map;
import org.wiztools.restclient.RequestBean;

/**
 *
 * @author schandran
 */
public class RoRequestBean {
    
    private final URL url;
    private final String method;
    private final Map<String, String> headers;
    private final RoReqEntityBean body;
    private final List<String> authMethods;
    private final boolean authPreemptive;
    private final String authHost;
    private final String authRealm;
    private final String authUsername;
    private final char[] authPassword;

    public String getAuthHost() {
        return authHost;
    }

    public List<String> getAuthMethods() {
        return authMethods;
    }

    public char[] getAuthPassword() {
        return authPassword;
    }

    public boolean isAuthPreemptive() {
        return authPreemptive;
    }

    public String getAuthRealm() {
        return authRealm;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public RoReqEntityBean getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }
    
    public RoRequestBean(final RequestBean request){
        url = request.getUrl();
        method = request.getMethod();
        headers = request.getHeaders();
        if(request.getBody() != null){
            body = new RoReqEntityBean(request.getBody());
        }
        else{
            body = null;
        }
        authMethods = request.getAuthMethods();
        authPreemptive = request.isAuthPreemptive();
        authHost = request.getAuthHost();
        authRealm = request.getAuthRealm();
        authUsername = request.getAuthUsername();
        authPassword = request.getAuthPassword();
    }
}
