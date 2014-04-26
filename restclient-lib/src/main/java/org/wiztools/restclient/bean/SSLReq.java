package org.wiztools.restclient.bean;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface SSLReq extends Serializable {

    SSLHostnameVerifier getHostNameVerifier();
    boolean isTrustSelfSignedCert();

    File getKeyStore();
    KeyStoreType getKeyStoreType();
    char[] getKeyStorePassword();

    File getTrustStore();
    KeyStoreType getTrustStoreType();
    char[] getTrustStorePassword();
}
