package org.wiztools.restclient.ui.reqssl;

import org.wiztools.restclient.bean.SSLKeyStore;

/**
 *
 * @author subwiz
 */
public interface KeyStoreListener {
    /**
     * Triggered when Ok button is pressed.
     * @param store Will always be NOT null.
     */
    void onOk(SSLKeyStore store);
    
    /**
     * Triggered when Cancel button is clicked.
     */
    void onCancel();
}
