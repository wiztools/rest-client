package org.wiztools.restclient.ui.reqtls;

import org.wiztools.restclient.bean.KeyStore;

/**
 *
 * @author subwiz
 */
public interface KeyStoreListener {
    /**
     * Triggered when Ok button is pressed.
     * @param store Will always be NOT null.
     */
    void onOk(KeyStore store);

    /**
     * Triggered when Cancel button is clicked.
     */
    void onCancel();
}
