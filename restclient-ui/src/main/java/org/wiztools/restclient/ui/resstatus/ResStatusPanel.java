package org.wiztools.restclient.ui.resstatus;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ResStatusPanelImpl.class)
public interface ResStatusPanel extends ViewPanel {
    String getStatus();
    void setStatus(String status);
}
