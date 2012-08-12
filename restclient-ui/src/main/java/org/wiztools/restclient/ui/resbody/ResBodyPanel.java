package org.wiztools.restclient.ui.resbody;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.ContentType;
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
