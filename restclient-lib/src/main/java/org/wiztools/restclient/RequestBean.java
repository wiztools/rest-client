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
    private String authDomain;
    private String authWorkstation;
    private String authToken;
    private MultiValueMap<String, String> headers;
    private ReqEntity body;
    private String testScript;
    private String sslTrustStore;
    private char[] sslTrustStorePassword;
    private String sslKeyStore;
    private char[] sslKeyStorePassword;
    private SSLHostnameVerifier sslHostNameVerifier = SSLHostnameVerifier.STRICT; // Default to strict!
    private boolean sslTrustSelfSignedCert = false;
    private HTTPVersion httpVersion = HTTPVersion.getDefault(); // Initialize to the default version
    private boolean isFollowRedirect;
    private boolean isIgnoreResponseBody = false;

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
    public String getSslKeyStore() {
        return sslKeyStore;
    }

    public void setSslKeyStore(String sslKeyStore) {
        this.sslKeyStore = sslKeyStore;
    }

    @Override
    public char[] getSslKeyStorePassword() {
        return sslKeyStorePassword;
    }

    public void setSslKeyStorePassword(char[] sslKeyStorePassword) {
        this.sslKeyStorePassword = sslKeyStorePassword;
    }

    @Override
    public SSLHostnameVerifier getSslHostNameVerifier() {
        return sslHostNameVerifier;
    }

    public void setSslHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.sslHostNameVerifier = sslHostNameVerifier;
    }
    
    public void setSslTrustSelfSignedCert(boolean sslTrustSelfSignedCert) {
        this.sslTrustSelfSignedCert = sslTrustSelfSignedCert;
    }
    
    @Override
    public boolean isSslTrustSelfSignedCert() {
        return this.sslTrustSelfSignedCert;
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
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
    
    public void setAuthWorkstation(String authWorkstation) {
        this.authWorkstation = authWorkstation;
    }
    
    @Override
    public String getAuthWorkstation() {
        return authWorkstation;
    }
    
    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }

    @Override
    public String getAuthDomain() {
        return authDomain;
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
    
    public void setIgnoreResponseBody(boolean isIgnoreResponseBody) {
        this.isIgnoreResponseBody = isIgnoreResponseBody;
    }
    
    @Override
    public boolean isIgnoreResponseBody() {
        return isIgnoreResponseBody;
    }
    
    public RequestBean(){
        headers = new MultiValueMapArrayList<String, String>();
        authMethods = new ArrayList<HTTPAuthMethod>();
    }
    
    @Override
    public Object clone(){
        RequestBean cloned = new RequestBean();
        cloned.setAuthHost(authHost);
        cloned.setAuthPassword(Arrays.copyOf(authPassword, authPassword.length));
        cloned.setAuthPreemptive(authPreemptive);
        cloned.setAuthRealm(authRealm);
        cloned.setAuthUsername(authUsername);
        cloned.setAuthDomain(authDomain);
        cloned.setAuthWorkstation(authWorkstation);
        cloned.setSslTrustStore(sslTrustStore);
        cloned.setSslTrustStorePassword(
                Arrays.copyOf(sslTrustStorePassword, sslTrustStorePassword.length));
        cloned.setSslKeyStore(sslKeyStore);
        cloned.setSslKeyStorePassword(
                Arrays.copyOf(sslKeyStorePassword, sslKeyStorePassword.length));
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
        cloned.setIgnoreResponseBody(isIgnoreResponseBody);
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestBean other = (RequestBean) obj;
        if (this.url != other.url && (this.url == null || !this.url.equals(other.url))) {
            return false;
        }
        if (this.method != other.method) {
            return false;
        }
        if (this.authPreemptive != other.authPreemptive) {
            return false;
        }
        if (this.authMethods != other.authMethods && (this.authMethods == null || !this.authMethods.equals(other.authMethods))) {
            return false;
        }
        if ((this.authHost == null) ? (other.authHost != null) : !this.authHost.equals(other.authHost)) {
            return false;
        }
        if ((this.authRealm == null) ? (other.authRealm != null) : !this.authRealm.equals(other.authRealm)) {
            return false;
        }
        if ((this.authUsername == null) ? (other.authUsername != null) : !this.authUsername.equals(other.authUsername)) {
            return false;
        }
        if (!Arrays.equals(this.authPassword, other.authPassword)) {
            return false;
        }
        if ((this.authDomain == null) ? (other.authDomain != null) : !this.authDomain.equals(other.authDomain)) {
            return false;
        }
        if ((this.authWorkstation == null) ? (other.authWorkstation != null) : !this.authWorkstation.equals(other.authWorkstation)) {
            return false;
        }
        if ((this.authToken == null) ? (other.authToken != null) : !this.authToken.equals(other.authToken)) {
            return false;
        }
        if (this.headers != other.headers && (this.headers == null || !this.headers.equals(other.headers))) {
            return false;
        }
        if (this.body != other.body && (this.body == null || !this.body.equals(other.body))) {
            return false;
        }
        if ((this.testScript == null) ? (other.testScript != null) : !this.testScript.equals(other.testScript)) {
            return false;
        }
        if ((this.sslTrustStore == null) ? (other.sslTrustStore != null) : !this.sslTrustStore.equals(other.sslTrustStore)) {
            return false;
        }
        if (!Arrays.equals(this.sslTrustStorePassword, other.sslTrustStorePassword)) {
            return false;
        }
        if ((this.sslKeyStore == null) ? (other.sslKeyStore != null) : !this.sslKeyStore.equals(other.sslKeyStore)) {
            return false;
        }
        if (!Arrays.equals(this.sslKeyStorePassword, other.sslKeyStorePassword)) {
            return false;
        }
        if (this.sslHostNameVerifier != other.sslHostNameVerifier) {
            return false;
        }
        if (this.sslTrustSelfSignedCert != other.sslTrustSelfSignedCert) {
            return false;
        }
        if (this.httpVersion != other.httpVersion) {
            return false;
        }
        if (this.isFollowRedirect != other.isFollowRedirect) {
            return false;
        }
        if (this.isIgnoreResponseBody != other.isIgnoreResponseBody) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 59 * hash + (this.method != null ? this.method.hashCode() : 0);
        hash = 59 * hash + (this.authPreemptive ? 1 : 0);
        hash = 59 * hash + (this.authMethods != null ? this.authMethods.hashCode() : 0);
        hash = 59 * hash + (this.authHost != null ? this.authHost.hashCode() : 0);
        hash = 59 * hash + (this.authRealm != null ? this.authRealm.hashCode() : 0);
        hash = 59 * hash + (this.authUsername != null ? this.authUsername.hashCode() : 0);
        hash = 59 * hash + Arrays.hashCode(this.authPassword);
        hash = 59 * hash + (this.authDomain != null ? this.authDomain.hashCode() : 0);
        hash = 59 * hash + (this.authWorkstation != null ? this.authWorkstation.hashCode() : 0);
        hash = 59 * hash + (this.authToken != null ? this.authToken.hashCode() : 0);
        hash = 59 * hash + (this.headers != null ? this.headers.hashCode() : 0);
        hash = 59 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 59 * hash + (this.testScript != null ? this.testScript.hashCode() : 0);
        hash = 59 * hash + (this.sslTrustStore != null ? this.sslTrustStore.hashCode() : 0);
        hash = 59 * hash + Arrays.hashCode(this.sslTrustStorePassword);
        hash = 59 * hash + (this.sslKeyStore != null ? this.sslKeyStore.hashCode() : 0);
        hash = 59 * hash + Arrays.hashCode(this.sslKeyStorePassword);
        hash = 59 * hash + (this.sslHostNameVerifier != null ? this.sslHostNameVerifier.hashCode() : 0);
        hash = 59 * hash + (this.sslTrustSelfSignedCert ? 1 : 0);
        hash = 59 * hash + (this.httpVersion != null ? this.httpVersion.hashCode() : 0);
        hash = 59 * hash + (this.isFollowRedirect ? 1 : 0);
        hash = 59 * hash + (this.isIgnoreResponseBody ? 1 : 0);
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
        sb.append(sslKeyStore).append(", ");
        sb.append(sslKeyStorePassword==null?"null": new String(sslKeyStorePassword).replaceAll(".", "X")).append(", ");
        sb.append(sslHostNameVerifier).append(", ");
        sb.append(sslTrustSelfSignedCert).append(", ");
        sb.append(httpVersion).append(", ");
        sb.append(isFollowRedirect).append(", ");
        sb.append(isIgnoreResponseBody).append(", ");
        sb.append(testScript);
        sb.append("]");
        return sb.toString();
    }
}
