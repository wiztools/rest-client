package org.wiztools.restclient.ui.reqssl;

import org.wiztools.restclient.bean.SSLKeyStore;

/**
 *
 * @author subwiz
 */
public interface KeyStoreListener {
    void onOk(SSLKeyStore store);
    void onCancel();
}
