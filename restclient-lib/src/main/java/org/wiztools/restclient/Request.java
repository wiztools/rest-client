package org.wiztools.restclient;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 *
 * @author subwiz
 */
public interface Request extends Cloneable {

    String getAuthHost();

    List<String> getAuthMethods();

    char[] getAuthPassword();

    String getAuthRealm();

    String getAuthUsername();

    ReqEntity getBody();

    Map<String, String> getHeaders();

    HTTPVersion getHttpVersion();

    HTTPMethod getMethod();

    SSLHostnameVerifier getSslHostNameVerifier();

    String getSslTrustStore();

    char[] getSslTrustStorePassword();

    String getTestScript();

    URL getUrl();

    boolean isAuthPreemptive();

    Object clone();
}
