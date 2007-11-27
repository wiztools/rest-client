/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author schandran
 */
public class RequestBean {
    private String url;
    private String method;
    private boolean isAuthEnabled;
    private String authHost;
    private String authRealm;
    private String authUsername;
    private char[] authPassword;

    public String getAuthHost() {
        return authHost;
    }

    public void setAuthHost(String authHost) {
        this.authHost = authHost;
    }

    public char[] getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(char[] authPassword) {
        this.authPassword = authPassword;
    }

    public String getAuthRealm() {
        return authRealm;
    }

    public void setAuthRealm(String authRealm) {
        this.authRealm = authRealm;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }
    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /*public void setHeaders(Map headers) {
        this.headers = headers;
    }*/
    
    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }

    public boolean isIsAuthEnabled() {
        return isAuthEnabled;
    }

    public void setIsAuthEnabled(boolean isAuthEnabled) {
        this.isAuthEnabled = isAuthEnabled;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public RequestBean(){
        headers = new LinkedHashMap();
    }
}
