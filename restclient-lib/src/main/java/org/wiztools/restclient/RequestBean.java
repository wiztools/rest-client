package org.wiztools.restclient;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;

/**
 *
 * @author schandran
 */
public final class RequestBean implements Request{
    
    private URL url;
    private HTTPMethod method;
    private boolean authPreemptive;
    private List<HTTPAuthMethod> authMethods;
    private String authHost;
    private String authRealm;
    private String authUsername;
    private char[] authPassword;
    private MultiValueMap<String, String> headers;
    private ReqEntity body;
    private String testScript;
    private String sslTrustStore;
    private char[] sslTrustStorePassword;
    private SSLHostnameVerifier sslHostNameVerifier = SSLHostnameVerifier.STRICT; // Default to strict!
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

    public void setSslTrustStore(String sslKeyStore) {
        this.sslTrustStore = sslKeyStore;
    }

    @Override
    public char[] getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(char[] sslKeyStorePassword) {
        this.sslTrustStorePassword = sslKeyStorePassword;
    }

    @Override
    public SSLHostnameVerifier getSslHostNameVerifier() {
        return sslHostNameVerifier;
    }

    public void setSslHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.sslHostNameVerifier = sslHostNameVerifier;
    }
    
    @Override
    public String getTestScript() {
        return testScript;
    }

    public void setTestScript(String testScript) {
        this.testScript = testScript;
    }

    @Override
    public ReqEntity getBody() {
        return body;
    }
    
    public void setBody(final ReqEntity body){
        this.body = body;
    }
    
    @Override
    public boolean isAuthPreemptive() {
        return authPreemptive;
    }

    public void setAuthPreemptive(boolean authPreemptive) {
        this.authPreemptive = authPreemptive;
    }

    @Override
    public List<HTTPAuthMethod> getAuthMethods() {
        return Collections.unmodifiableList(authMethods);
    }

    public void addAuthMethod(final HTTPAuthMethod authMethod) {
        this.authMethods.add(authMethod);
    }

    @Override
    public String getAuthHost() {
        return authHost;
    }

    public void setAuthHost(String authHost) {
        this.authHost = authHost;
    }

    @Override
    public char[] getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(char[] authPassword) {
        this.authPassword = authPassword;
    }

    @Override
    public String getAuthRealm() {
        return authRealm;
    }

    public void setAuthRealm(String authRealm) {
        this.authRealm = authRealm;
    }

    @Override
    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return CollectionsUtil.unmodifiableMultiValueMap(headers);
    }

    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }

    @Override
    public HTTPMethod getMethod() {
        return method;
    }

    public void setMethod(final HTTPMethod method) {
        this.method = method;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public boolean isFollowRedirect() {
        return isFollowRedirect;
    }

    public void setFollwoRedirect(boolean isFollowRedirect) {
        this.isFollowRedirect = isFollowRedirect;
    }
    
    public RequestBean(){
        headers = new MultiValueMapArrayList<String, String>();
        authMethods = new ArrayList<HTTPAuthMethod>();
    }
    
    @Override
    public Object clone(){
        RequestBean cloned = new RequestBean();
        cloned.setAuthHost(authHost);
        cloned.setAuthPassword(authPassword);
        cloned.setAuthPreemptive(authPreemptive);
        cloned.setAuthRealm(authRealm);
        cloned.setAuthUsername(authUsername);
        cloned.setSslTrustStore(sslTrustStore);
        cloned.setSslTrustStorePassword(sslTrustStorePassword);
        cloned.setHttpVersion(httpVersion);
        if(body != null){
            cloned.setBody((ReqEntityBean)body.clone());
        }
        if(!headers.isEmpty()){
            for(String header: headers.keySet()){
                for(String value: headers.get(header)) {
                    cloned.addHeader(header, value);
                }
            }
        }
        cloned.setMethod(method);
        cloned.setTestScript(testScript);
        cloned.setUrl(url);
        cloned.setFollwoRedirect(isFollowRedirect);
        return cloned;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o instanceof Request){
            final Request bean = (Request)o;
            boolean isEqual = true;
            isEqual = isEqual && (this.method == null? bean.getMethod() == null: this.method.equals(bean.getMethod()));
            isEqual = isEqual && (this.headers == null? bean.getHeaders() == null: this.headers.equals(bean.getHeaders()));
            isEqual = isEqual && (this.body == null? bean.getBody() == null: this.body.equals(bean.getBody()));
            isEqual = isEqual && (this.authHost == null? bean.getAuthHost() == null: this.authHost.equals(bean.getAuthHost()));
            isEqual = isEqual && (this.authMethods == null? bean.getAuthMethods() == null: this.authMethods.equals(bean.getAuthMethods()));
            isEqual = isEqual && (this.authPassword == null? bean.getAuthPassword() == null: Arrays.equals(this.authPassword, bean.getAuthPassword()));
            isEqual = isEqual && (this.authPreemptive == bean.isAuthPreemptive());
            isEqual = isEqual && (this.authRealm == null? bean.getAuthRealm() == null: this.authRealm.equals(bean.getAuthRealm()));
            isEqual = isEqual && (this.authUsername == null? bean.getAuthUsername() == null: this.authUsername.equals(bean.getAuthUsername()));
            isEqual = isEqual && (this.sslTrustStore == null? bean.getSslTrustStore() == null: this.sslTrustStore.equals(bean.getSslTrustStore()));
            isEqual = isEqual && (this.sslTrustStorePassword == null? bean.getSslTrustStorePassword() == null: Arrays.equals(this.sslTrustStorePassword, bean.getSslTrustStorePassword()));
            isEqual = isEqual && (this.sslHostNameVerifier == null? bean.getSslHostNameVerifier() == null: this.sslHostNameVerifier == bean.getSslHostNameVerifier());
            isEqual = isEqual && (this.httpVersion == null? bean.getHttpVersion() == null: this.httpVersion == bean.getHttpVersion());
            isEqual = isEqual && (this.testScript == null? bean.getTestScript() == null: this.testScript.equals(bean.getTestScript()));
            isEqual = isEqual && (this.url == null? bean.getUrl() == null: this.url.equals(bean.getUrl()));
            isEqual = isEqual && (this.isFollowRedirect == bean.isFollowRedirect());
            return isEqual;
        }
        return false;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 61 * hash + (this.method != null ? this.method.hashCode() : 0);
        hash = 61 * hash + (this.authPreemptive ? 1 : 0);
        hash = 61 * hash + (this.authMethods != null ? this.authMethods.hashCode() : 0);
        hash = 61 * hash + (this.authHost != null ? this.authHost.hashCode() : 0);
        hash = 61 * hash + (this.authRealm != null ? this.authRealm.hashCode() : 0);
        hash = 61 * hash + (this.authUsername != null ? this.authUsername.hashCode() : 0);
        hash = 61 * hash + (this.authPassword != null ? this.authPassword.hashCode() : 0);
        hash = 61 * hash + (this.headers != null ? this.headers.hashCode() : 0);
        hash = 61 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 61 * hash + (this.testScript != null ? this.testScript.hashCode() : 0);
        hash = 61 * hash + (this.sslTrustStore != null ? this.sslTrustStore.hashCode() : 0);
        hash = 61 * hash + (this.sslTrustStorePassword != null ? this.sslTrustStorePassword.hashCode() : 0);
        hash = 61 * hash + (this.sslHostNameVerifier != null ? this.sslHostNameVerifier.hashCode() : 0);
        hash = 61 * hash + (this.httpVersion != null ? this.httpVersion.hashCode() : 0);
        hash = 61 * hash + (this.isFollowRedirect ? 1 : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@Request[");
        sb.append(url).append(", ");
        sb.append(method).append(", ");
        sb.append(headers.toString()).append(", ");
        sb.append(body).append(", ");
        sb.append(authMethods).append(", ");
        sb.append(authPreemptive).append(", ");
        sb.append(authHost).append(", ");
        sb.append(authRealm).append(", ");
        sb.append(authUsername).append(", ");
        sb.append(authPassword==null?"null": new String(authPassword).replaceAll(".", "X")).append(", ");
        sb.append(sslTrustStore).append(", ");
        sb.append(sslTrustStorePassword==null?"null": new String(sslTrustStorePassword).replaceAll(".", "X")).append(", ");
        sb.append(sslHostNameVerifier).append(", ");
        sb.append(httpVersion).append(", ");
        sb.append(isFollowRedirect).append(", ");
        sb.append(testScript);
        sb.append("]");
        return sb.toString();
    }
}
