package org.wiztools.restclient.bean;

import java.net.HttpCookie;
import java.net.URL;
import java.util.List;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author subwiz
 */
public interface Request extends Cloneable {

    String getAuthHost();

    List<HTTPAuthMethod> getAuthMethods();

    char[] getAuthPassword();

    String getAuthRealm();

    String getAuthUsername();
    
    String getAuthBearerToken();
    
    String getAuthWorkstation();
    
    String getAuthDomain();

    ReqEntity getBody();

    MultiValueMap<String, String> getHeaders();
    
    List<HttpCookie> getCookies();

    HTTPVersion getHttpVersion();

    HTTPMethod getMethod();

    SSLHostnameVerifier getSslHostNameVerifier();

    String getSslTrustStore();

    char[] getSslTrustStorePassword();
    
    String getSslKeyStore();
    
    char[] getSslKeyStorePassword();
    
    boolean isSslTrustSelfSignedCert();

    String getTestScript();

    URL getUrl();

    boolean isAuthPreemptive();

    boolean isFollowRedirect();
    
    boolean isIgnoreResponseBody();

    Object clone();
}
