package org.wiztools.restclient.ui.component;

/**
 *
 * @author subhash
 */
public interface BodyPopupMenuListener {
    void onSuccess(String msg);
    void onFailure(String msg);
    void onMessage(String msg);
}
