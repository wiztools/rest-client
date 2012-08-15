package org.wiztools.restclient.ui;

import com.google.inject.ImplementedBy;

/**
 *
 * @author subwiz
 */
@ImplementedBy(StatusBarPanelImpl.class)
public interface StatusBarPanel extends ViewPanel {
    void setStatus(String status);
    
    void showProgressBar();
    void hideProgressBar();
}
