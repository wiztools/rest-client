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
    private final String authToken;
    private final String sslTrustStore;
    private final char[] sslTrustStorePassword;
    private final String sslKeyStore;
    private final char[] sslKeyStorePassword;
    private final SSLHostnameVerifier sslHostNameVerifier;
    private final HTTPVersion httpVersion;
    private final boolean isFollowRedirect;

    @Override
    public HTTPVersion getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String getSslTrustStore() {
        return sslTrustStore;
    }

    @Override
    public char[] getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }
    
    @Override
    public String getSslKeyStore() {
        return sslKeyStore;
    }

    @Override
    public char[] getSslKeyStorePassword() {
        return sslKeyStorePassword;
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
    public String getAuthToken() {
        return authToken;
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
        authToken = request.getAuthToken();
        sslTrustStore = request.getSslTrustStore();
        sslTrustStorePassword = request.getSslTrustStorePassword();
        sslKeyStore = request.getSslKeyStore();
        sslKeyStorePassword = request.getSslKeyStorePassword();
        httpVersion = request.getHttpVersion();
        sslHostNameVerifier = request.getSslHostNameVerifier();
        isFollowRedirect = request.isFollowRedirect();
    }

    @Override
    public SSLHostnameVerifier getSslHostNameVerifier() {
        return sslHostNameVerifier;
    }

    @Override
    public String getTestScript() {
        return null;
    }

    @Override
    public Object clone(){
        return null;
    }
}
