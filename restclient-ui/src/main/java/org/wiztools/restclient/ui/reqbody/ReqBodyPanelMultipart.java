package org.wiztools.restclient.ui.reqbody;

import java.awt.Component;
import javax.swing.JPanel;
import org.wiztools.restclient.ReqEntity;
import org.wiztools.restclient.ReqEntityMultipart;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelMultipart extends JPanel implements ReqBodyPanel {
    @Override
    public void enableBody() {
        // enable control
    }
    
    @Override
    public void disableBody() {
        // disable control
    }
    
    @Override
    public void clearBody() {
        // enable control
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityMultipart) {
            ReqEntityMultipart e = (ReqEntityMultipart) entity;
        }
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
