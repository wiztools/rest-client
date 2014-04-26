package org.wiztools.restclient.bean;

import java.util.Objects;

/**
 *
 * @author subwiz
 */
public class SSLReqBean implements SSLReq {
    
    private SSLKeyStore trustStore;
    private SSLKeyStore keyStore;
    
    // Default to strict!
    private SSLHostnameVerifier hostNameVerifier = SSLHostnameVerifier.STRICT;
    
    private boolean trustSelfSignedCert = false;

    public void setHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.hostNameVerifier = sslHostNameVerifier;
    }
    
    public void setTrustSelfSignedCert(boolean sslTrustSelfSignedCert) {
        this.trustSelfSignedCert = sslTrustSelfSignedCert;
    }

    public void setKeyStore(SSLKeyStore sslKeyStore) {
        this.keyStore = sslKeyStore;
    }

    public void setTrustStore(SSLKeyStore sslTrustStore) {
        this.trustStore = sslTrustStore;
    }

    @Override
    public SSLHostnameVerifier getHostNameVerifier() {
        return hostNameVerifier;
    }

    @Override
    public SSLKeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public SSLKeyStore getTrustStore() {
        return trustStore;
    }

    @Override
    public boolean isTrustSelfSignedCert() {
        return trustSelfSignedCert;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.trustStore);
        hash = 29 * hash + Objects.hashCode(this.keyStore);
        hash = 29 * hash + Objects.hashCode(this.hostNameVerifier);
        hash = 29 * hash + (this.trustSelfSignedCert ? 1 : 0);
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
        final SSLReqBean other = (SSLReqBean) obj;
        if (!Objects.equals(this.trustStore, other.trustStore)) {
            return false;
        }
        if (!Objects.equals(this.keyStore, other.keyStore)) {
            return false;
        }
        if (this.hostNameVerifier != other.hostNameVerifier) {
            return false;
        }
        if (this.trustSelfSignedCert != other.trustSelfSignedCert) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@SSL[");
        sb.append("trustSelfSignedCert=").append(trustSelfSignedCert).append(", ");
        sb.append("hostNameVerifier=").append(hostNameVerifier).append(", ");
        sb.append("trustStore=").append(trustStore).append(", ");
        sb.append("keyStore=").append(keyStore).append(", ");
        sb.append("]");
        return sb.toString();
    }
}
