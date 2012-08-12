package org.wiztools.restclient.ui.reqauth;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.SSLHostnameVerifier;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqSSLPanelImpl.class)
public interface ReqSSLPanel extends ViewPanel {

    void clear();

    SSLHostnameVerifier getHostnameVerifier();

    String getKeyStoreFile();

    char[] getKeyStorePassword();

    String getTrustStoreFile();

    char[] getTrustStorePassword();

    boolean isTrustSelfSignedCert();

    void setHostnameVerifier(SSLHostnameVerifier v);

    void setKeyStoreFile(String file);

    void setKeyStorePassword(String password);

    void setTrustSelfSignedCert(boolean b);

    void setTrustStoreFile(String file);

    void setTrustStorePassword(String password);
    
}
