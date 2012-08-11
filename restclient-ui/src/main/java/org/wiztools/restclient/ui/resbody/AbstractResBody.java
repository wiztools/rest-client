package org.wiztools.restclient.ui.resbody;

import javax.swing.JPanel;
import org.wiztools.restclient.ContentType;

/**
 *
 * @author subwiz
 */
public abstract class AbstractResBody extends JPanel implements ResBodyOps {
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
    public final void clearBody() {
        body = null;
    }
    
    public abstract void clearUI();
}
