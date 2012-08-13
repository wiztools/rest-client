package org.wiztools.restclient.ui.resbody;

import java.awt.Component;
import javax.swing.JPanel;
import org.wiztools.restclient.bean.ContentType;

/**
 *
 * @author subwiz
 */
public abstract class AbstractResBody extends JPanel implements ResBodyPanel {
    protected byte[] body;
    protected ContentType type;

    @Override
    public void setBody(byte[] body, ContentType type) {
        this.body = body;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public final void clear() {
        body = null;
        
        clearUI();
    }
    
    public abstract void clearUI();
}
