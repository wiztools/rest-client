package org.wiztools.restclient.bean;

import java.net.HttpCookie;
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
 * @author subwiz
 */
public final class RequestBean implements Request{
    
    private URL url;
    private HTTPMethod method;
    private Auth auth;
    private final MultiValueMap<String, String> headers = new MultiValueMapArrayList<String, String>();
    private final List<HttpCookie> cookies = new ArrayList<HttpCookie>();
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
    
    public void setAuth(Auth auth) {
        this.auth = auth;
    }
    
    @Override
    public Auth getAuth() {
        return auth;
    }

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
    public MultiValueMap<String, String> getHeaders() {
        return CollectionsUtil.unmodifiableMultiValueMap(headers);
    }

    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }
    
    public void addCookie(HttpCookie cookie) {
        this.cookies.add(cookie);
    }
    
    @Override
    public List<HttpCookie> getCookies() {
        return Collections.unmodifiableList(this.cookies);
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

    public void setFollowRedirect(boolean isFollowRedirect) {
        this.isFollowRedirect = isFollowRedirect;
    }
    
    public void setIgnoreResponseBody(boolean isIgnoreResponseBody) {
        this.isIgnoreResponseBody = isIgnoreResponseBody;
    }
    
    @Override
    public boolean isIgnoreResponseBody() {
        return isIgnoreResponseBody;
    }
    
    @Override
    public Object clone(){
        RequestBean cloned = new RequestBean();
        cloned.setSslTrustStore(sslTrustStore);
        cloned.setSslTrustStorePassword(
                Arrays.copyOf(sslTrustStorePassword, sslTrustStorePassword.length));
        cloned.setSslKeyStore(sslKeyStore);
        cloned.setSslKeyStorePassword(
                Arrays.copyOf(sslKeyStorePassword, sslKeyStorePassword.length));
        cloned.setHttpVersion(httpVersion);
        if(body != null){
            cloned.setBody((ReqEntityStringBean)body.clone());
        }
        if(!headers.isEmpty()){
            for(String header: headers.keySet()){
                for(String value: headers.get(header)) {
                    cloned.addHeader(header, value);
                }
            }
        }
        if(!cookies.isEmpty()) {
            for(HttpCookie cookie: cookies) {
                cloned.addCookie(cookie);
            }
        }
        cloned.setMethod(method);
        cloned.setTestScript(testScript);
        cloned.setUrl(url);
        cloned.setFollowRedirect(isFollowRedirect);
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
        if (this.headers != other.headers && (this.headers == null || !this.headers.equals(other.headers))) {
            return false;
        }
        if (this.cookies != other.cookies && (this.cookies == null || !this.cookies.equals(other.cookies))) {
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
        hash = 59 * hash + (this.headers != null ? this.headers.hashCode() : 0);
        hash = 59 * hash + (this.cookies != null ? this.cookies.hashCode() : 0);
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
        sb.append(cookies.toString()).append(", ");
        sb.append(body).append(", ");
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
