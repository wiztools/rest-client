package org.wiztools.restclient.ui.reqbody;

import javax.swing.JPanel;
import org.wiztools.restclient.ReqEntity;

/**
 *
 * @author subwiz
 */
class ReqBodyPanelNone extends JPanel implements ReqBodyOps {

    @Override
    public void enableBody() {
        // do nothing!
    }

    @Override
    public void disableBody() {
        // do nothing!
    }

    @Override
    public void clearBody() {
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
    
}
