package org.wiztools.restclient.ui.reqgo;

import com.google.inject.ImplementedBy;
import java.awt.event.ActionListener;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqUrlGoPanelImpl.class)
public interface ReqUrlGoPanel extends ViewPanel {
    enum ACTION_TYPE{GO, CANCEL};
    
    void setAsRunning();
    void setAsIdle();
    
    boolean isIdle();
    boolean isRunning();
    
    String getUrlString();
    void setUrlString(String url);
    
    void addActionListener(ActionListener listener);
}
