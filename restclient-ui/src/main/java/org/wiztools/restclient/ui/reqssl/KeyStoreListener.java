package org.wiztools.restclient.ui.reqssl;

import org.wiztools.restclient.bean.SSLKeyStore;

/**
 *
 * @author subwiz
 */
public interface KeyStoreListener {
    void ok(SSLKeyStore store);
    void cancel();
}
