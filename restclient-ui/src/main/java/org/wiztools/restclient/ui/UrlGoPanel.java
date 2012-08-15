package org.wiztools.restclient.ui;

import com.google.inject.ImplementedBy;
import java.awt.event.ActionListener;

/**
 *
 * @author subwiz
 */
@ImplementedBy(UrlGoPanelImpl.class)
public interface UrlGoPanel extends ViewPanel {
    enum ACTION_TYPE{GO, CANCEL};
    
    void setAsRunning();
    void setAsIdle();
    
    boolean isIdle();
    boolean isRunning();
    
    String getUrlString();
    void setUrlString(String url);
    
    void addActionListener(ActionListener listener);
}
