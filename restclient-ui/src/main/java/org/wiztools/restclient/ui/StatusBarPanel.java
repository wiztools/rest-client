package org.wiztools.restclient.ui;

import com.google.inject.ImplementedBy;
import java.awt.Component;

/**
 *
 * @author subwiz
 */
@ImplementedBy(StatusBarPanelImpl.class)
public interface StatusBarPanel {
    void setStatus(String status);
    
    void showProgressBar();
    void hideProgressBar();
    
    Component getComponent();
}
