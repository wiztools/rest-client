package org.wiztools.restclient.bean;

import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface TLSReq extends Serializable {

    HostnameVerifier getHostNameVerifier();
    boolean isTrustAllCerts();

    KeyStore getKeyStore();

    KeyStore getTrustStore();
}
