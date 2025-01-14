package org.wiztools.restclient.bean;

import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface SSLReq extends Serializable {

    SSLHostnameVerifier getHostNameVerifier();
    boolean isTrustAllCerts();

    SSLKeyStore getKeyStore();

    SSLKeyStore getTrustStore();
}
