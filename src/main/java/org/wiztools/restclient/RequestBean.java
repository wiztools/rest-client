/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schandran
 */
public final class RequestBean {
    
    private String url;
    private String method;
    private List<String> authMethods;
    private String authHost;
    private String authRealm;
    private String authUsername;
    private char[] authPassword;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private Map<String, String> body;

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(body);
    }
    
    public void addBody(final String key, final String value){
        body.put(key, value);
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public void addParameter(final String key, final String value){
        parameters.put(key, value);
    }
    
    public List<String> getAuthMethods() {
        return Collections.unmodifiableList(authMethods);
    }

    public void addAuthMethod(final String authMethod) {
        this.authMethods.add(authMethod);
    }

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

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /*public void setHeaders(Map headers) {
        this.headers = headers;
    }*/
    
    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
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
        parameters = new LinkedHashMap();
        body = new LinkedHashMap();
        authMethods = new ArrayList<String>();
    }
}
