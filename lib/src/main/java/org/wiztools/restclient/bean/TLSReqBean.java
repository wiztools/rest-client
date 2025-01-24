package org.wiztools.restclient.bean;

import java.util.Objects;

/**
 *
 * @author subwiz
 */
public class TLSReqBean implements TLSReq {

    private KeyStore trustStore;
    private KeyStore keyStore;

    // Default to strict!
    private HostnameVerifier hostNameVerifier = HostnameVerifier.STRICT;

    private boolean trustAllCerts = false;

    public void setHostNameVerifier(HostnameVerifier hnv) {
        this.hostNameVerifier = hnv;
    }

    public void setTrustAllCerts(boolean trust) {
        this.trustAllCerts = trust;
    }

    public void setKeyStore(KeyStore ks) {
        this.keyStore = ks;
    }

    public void setTrustStore(KeyStore ts) {
        this.trustStore = ts;
    }

    @Override
    public HostnameVerifier getHostNameVerifier() {
        return hostNameVerifier;
    }

    @Override
    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public KeyStore getTrustStore() {
        return trustStore;
    }

    @Override
    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.trustStore);
        hash = 29 * hash + Objects.hashCode(this.keyStore);
        hash = 29 * hash + Objects.hashCode(this.hostNameVerifier);
        hash = 29 * hash + (this.trustAllCerts ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TLSReqBean other = (TLSReqBean) obj;
        if (!Objects.equals(this.trustStore, other.trustStore)) {
            return false;
        }
        if (!Objects.equals(this.keyStore, other.keyStore)) {
            return false;
        }
        if (this.hostNameVerifier != other.hostNameVerifier) {
            return false;
        }
        if (this.trustAllCerts != other.trustAllCerts) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@SSL[");
        sb.append("trustSelfSignedCert=").append(trustAllCerts).append(", ");
        sb.append("hostNameVerifier=").append(hostNameVerifier).append(", ");
        sb.append("trustStore=").append(trustStore).append(", ");
        sb.append("keyStore=").append(keyStore).append(", ");
        sb.append("]");
        return sb.toString();
    }
}
