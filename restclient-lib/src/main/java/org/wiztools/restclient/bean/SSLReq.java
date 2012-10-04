package org.wiztools.restclient.bean;

import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface SSLReq extends Serializable {

    SSLHostnameVerifier getHostNameVerifier();

    String getKeyStore();

    char[] getKeyStorePassword();

    String getTrustStore();

    char[] getTrustStorePassword();

    boolean isTrustSelfSignedCert();
    
}
