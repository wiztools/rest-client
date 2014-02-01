package org.wiztools.restclient.bean;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author subwiz
 */
public class SSLReqBean implements SSLReq {
    
    private File trustStore;
    private char[] trustStorePassword;
    private File keyStore;
    private char[] keyStorePassword;
    private SSLHostnameVerifier hostNameVerifier = SSLHostnameVerifier.STRICT; // Default to strict!
    private boolean trustSelfSignedCert = false;

    public void setHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.hostNameVerifier = sslHostNameVerifier;
    }

    public void setKeyStore(File sslKeyStore) {
        this.keyStore = sslKeyStore;
    }

    public void setKeyStorePassword(char[] sslKeyStorePassword) {
        this.keyStorePassword = sslKeyStorePassword;
    }

    public void setTrustSelfSignedCert(boolean sslTrustSelfSignedCert) {
        this.trustSelfSignedCert = sslTrustSelfSignedCert;
    }

    public void setTrustStore(File sslTrustStore) {
        this.trustStore = sslTrustStore;
    }

    public void setTrustStorePassword(char[] sslTrustStorePassword) {
        this.trustStorePassword = sslTrustStorePassword;
    }

    @Override
    public SSLHostnameVerifier getHostNameVerifier() {
        return hostNameVerifier;
    }

    @Override
    public File getKeyStore() {
        return keyStore;
    }

    @Override
    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    @Override
    public File getTrustStore() {
        return trustStore;
    }

    @Override
    public char[] getTrustStorePassword() {
        return trustStorePassword;
    }

    @Override
    public boolean isTrustSelfSignedCert() {
        return trustSelfSignedCert;
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
        if ((this.trustStore == null) ? (other.trustStore != null) : !this.trustStore.equals(other.trustStore)) {
            return false;
        }
        if (!Arrays.equals(this.trustStorePassword, other.trustStorePassword)) {
            return false;
        }
        if ((this.keyStore == null) ? (other.keyStore != null) : !this.keyStore.equals(other.keyStore)) {
            return false;
        }
        if (!Arrays.equals(this.keyStorePassword, other.keyStorePassword)) {
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
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.trustStore != null ? this.trustStore.hashCode() : 0);
        hash = 53 * hash + Arrays.hashCode(this.trustStorePassword);
        hash = 53 * hash + (this.keyStore != null ? this.keyStore.hashCode() : 0);
        hash = 53 * hash + Arrays.hashCode(this.keyStorePassword);
        hash = 53 * hash + (this.hostNameVerifier != null ? this.hostNameVerifier.hashCode() : 0);
        hash = 53 * hash + (this.trustSelfSignedCert ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@SSL[");
        sb.append("trustSelfSignedCert=").append(trustSelfSignedCert).append(", ");
        sb.append("hostNameVerifier=").append(hostNameVerifier).append(", ");
        sb.append("trustStore=").append(trustStore).append(", ");
        sb.append("trustStorePassword=").append(
                (trustStorePassword!=null? trustStorePassword.length: 0)).append(", ");
        sb.append("keyStore=").append(keyStore).append(", ");
        sb.append("keyStorePassword=").append(
                (keyStorePassword!=null? keyStorePassword.length: 0));
        sb.append("]");
        return sb.toString();
    }
}
