package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class SSLReqBean implements SSLReq {
    
    private String trustStore;
    private char[] trustStorePassword;
    private String keyStore;
    private char[] keyStorePassword;
    private SSLHostnameVerifier hostNameVerifier = SSLHostnameVerifier.STRICT; // Default to strict!
    private boolean trustSelfSignedCert = false;

    public void setHostNameVerifier(SSLHostnameVerifier sslHostNameVerifier) {
        this.hostNameVerifier = sslHostNameVerifier;
    }

    public void setKeyStore(String sslKeyStore) {
        this.keyStore = sslKeyStore;
    }

    public void setKeyStorePassword(char[] sslKeyStorePassword) {
        this.keyStorePassword = sslKeyStorePassword;
    }

    public void setTrustSelfSignedCert(boolean sslTrustSelfSignedCert) {
        this.trustSelfSignedCert = sslTrustSelfSignedCert;
    }

    public void setTrustStore(String sslTrustStore) {
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
    public String getKeyStore() {
        return keyStore;
    }

    @Override
    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    @Override
    public String getTrustStore() {
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
    
}
