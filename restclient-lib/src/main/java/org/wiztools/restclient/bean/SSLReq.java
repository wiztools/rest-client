package org.wiztools.restclient.bean;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface SSLReq extends Serializable {

    SSLHostnameVerifier getHostNameVerifier();

    File getKeyStore();

    char[] getKeyStorePassword();

    File getTrustStore();

    char[] getTrustStorePassword();

    boolean isTrustSelfSignedCert();
    
}
