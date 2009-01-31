/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.net.URL;
import java.util.List;
import java.util.Map;
import org.wiztools.restclient.HTTPVersion;
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
    private String sslTrustStore;
    private char[] sslTrustStorePassword;
    private HTTPVersion httpVersion = HTTPVersion.getDefault(); // Initialize to the default version

    public HTTPVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HTTPVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getSslTrustStore() {
        return sslTrustStore;
    }

    public void setSslTrustStore(String sslTrustStore) {
        this.sslTrustStore = sslTrustStore;
    }

    public char[] getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(char[] sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }
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
    
    public RoRequestBean(final Request request){
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
        sslTrustStore = request.getSslTrustStore();
        sslTrustStorePassword = request.getSslTrustStorePassword();
        httpVersion = request.getHttpVersion();
    }
}
