package org.wiztools.restclient.ui.resbody;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ResBodyPanelImpl.class)
public interface ResBodyPanel extends ViewPanel {
    void setBody(byte[] data, ContentType type);
    byte[] getBody();
}
