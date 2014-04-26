package org.wiztools.restclient.bean;

import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface SSLReq extends Serializable {

    SSLHostnameVerifier getHostNameVerifier();
    boolean isTrustSelfSignedCert();

    SSLKeyStore getKeyStore();

    SSLKeyStore getTrustStore();
}
