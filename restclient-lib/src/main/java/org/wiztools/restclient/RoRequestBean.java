package org.wiztools.restclient;

import java.net.URL;
import java.util.List;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author schandran
 */
public class RoRequestBean implements Request {
    
    private final URL url;
    private final HTTPMethod method;
    private final MultiValueMap<String, String> headers;
    private final RoReqEntityBean body;
    private final List<HTTPAuthMethod> authMethods;
    private final boolean authPreemptive;
    private final String authHost;
    private final String authRealm;
    private final String authUsername;
    private final char[] authPassword;
    private String sslTrustStore;
    private char[] sslTrustStorePassword;
    SSLHostnameVerifier sslHostNameVerifier;
    private HTTPVersion httpVersion = HTTPVersion.getDefault(); // Initialize to the default version
    private boolean isFollowRedirect;

    @Override
    public HTTPVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HTTPVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public String getSslTrustStore() {
        return sslTrustStore;
    }

    public void setSslTrustStore(String sslTrustStore) {
        this.sslTrustStore = sslTrustStore;
    }

    @Override
    public char[] getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(char[] sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }
    @Override
    public String getAuthHost() {
        return authHost;
    }

    @Override
    public List<HTTPAuthMethod> getAuthMethods() {
        return authMethods;
    }

    @Override
    public char[] getAuthPassword() {
        return authPassword;
    }

    @Override
    public boolean isAuthPreemptive() {
        return authPreemptive;
    }

    @Override
    public String getAuthRealm() {
        return authRealm;
    }

    @Override
    public String getAuthUsername() {
        return authUsername;
    }

    @Override
    public RoReqEntityBean getBody() {
        return body;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public HTTPMethod getMethod() {
        return method;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isFollowRedirect() {
        return this.isFollowRedirect;
    }

    public void setFollowRedirect(boolean isFollowRedirect) {
        this.isFollowRedirect = isFollowRedirect;
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
        sslHostNameVerifier = request.getSslHostNameVerifier();
    }

    public SSLHostnameVerifier getSslHostNameVerifier() {
        return sslHostNameVerifier;
    }

    public String getTestScript() {
        return null;
    }

    @Override
    public Object clone(){
        return null;
    }
}
