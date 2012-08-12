package org.wiztools.restclient.ui.reqbody;

import java.awt.Component;
import javax.swing.JPanel;
import org.wiztools.restclient.ReqEntity;

/**
 *
 * @author subwiz
 */
class ReqBodyPanelNone extends JPanel implements ReqBodyPanel {

    @Override
    public void enableBody() {
        // do nothing!
    }

    @Override
    public void disableBody() {
        // do nothing!
    }

    @Override
    public void clear() {
        // do nothing!
    }

    @Override
    public void setEntity(ReqEntity entity) {
        // do nothing!
    }

    @Override
    public ReqEntity getEntity() {
        return null;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
}
