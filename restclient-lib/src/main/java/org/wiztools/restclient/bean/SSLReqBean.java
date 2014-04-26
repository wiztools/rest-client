package org.wiztools.restclient.bean;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author subwiz
 */
public class SSLReqBean implements SSLReq {
    
    private File trustStore;
    private KeyStoreType trustStoreType = KeyStoreType.JKS;
    private char[] trustStorePassword;
    
    private File keyStore;
    private KeyStoreType keyStoreType = KeyStoreType.JKS;
    private char[] keyStorePassword;
    
    // Default to strict!
    private SSLHostnameVerifier hostNameVerifier = SSLHostnameVerifier.STRICT;
    
    private boolean trustSelfSignedCert = false;

    public void setHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.hostNameVerifier = sslHostNameVerifier;
    }
    
    public void setTrustSelfSignedCert(boolean sslTrustSelfSignedCert) {
        this.trustSelfSignedCert = sslTrustSelfSignedCert;
    }

    public void setKeyStore(File sslKeyStore) {
        this.keyStore = sslKeyStore;
    }
    
    public void setKeyStoreType(KeyStoreType type) {
        keyStoreType = type;
    }

    public void setKeyStorePassword(char[] sslKeyStorePassword) {
        this.keyStorePassword = sslKeyStorePassword;
    }

    public void setTrustStore(File sslTrustStore) {
        this.trustStore = sslTrustStore;
    }
    
    public void setTrustStoreType(KeyStoreType type) {
        trustStoreType = type;
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
    public KeyStoreType getKeyStoreType() {
        return keyStoreType;
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
    public KeyStoreType getTrustStoreType() {
        return trustStoreType;
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
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.trustStore);
        hash = 37 * hash + Objects.hashCode(this.trustStoreType);
        hash = 37 * hash + Arrays.hashCode(this.trustStorePassword);
        hash = 37 * hash + Objects.hashCode(this.keyStore);
        hash = 37 * hash + Objects.hashCode(this.keyStoreType);
        hash = 37 * hash + Arrays.hashCode(this.keyStorePassword);
        hash = 37 * hash + Objects.hashCode(this.hostNameVerifier);
        hash = 37 * hash + (this.trustSelfSignedCert ? 1 : 0);
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
        if (this.trustStoreType != other.trustStoreType) {
            return false;
        }
        if (!Arrays.equals(this.trustStorePassword, other.trustStorePassword)) {
            return false;
        }
        if (!Objects.equals(this.keyStore, other.keyStore)) {
            return false;
        }
        if (this.keyStoreType != other.keyStoreType) {
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@SSL[");
        sb.append("trustSelfSignedCert=").append(trustSelfSignedCert).append(", ");
        sb.append("hostNameVerifier=").append(hostNameVerifier).append(", ");
        sb.append("trustStore=").append(trustStore).append(", ");
        sb.append("trustStoreType=").append(trustStoreType).append(", ");
        sb.append("trustStorePassword=").append(
                (trustStorePassword!=null? trustStorePassword.length: 0)).append(", ");
        sb.append("keyStore=").append(keyStore).append(", ");
        sb.append("keyStoreType=").append(keyStoreType).append(", ");
        sb.append("keyStorePassword=").append(
                (keyStorePassword!=null? keyStorePassword.length: 0));
        sb.append("]");
        return sb.toString();
    }
}
