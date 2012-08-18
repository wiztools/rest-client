package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface SSLReq {

    SSLHostnameVerifier getHostNameVerifier();

    String getKeyStore();

    char[] getKeyStorePassword();

    String getTrustStore();

    char[] getTrustStorePassword();

    boolean isTrustSelfSignedCert();
    
}
