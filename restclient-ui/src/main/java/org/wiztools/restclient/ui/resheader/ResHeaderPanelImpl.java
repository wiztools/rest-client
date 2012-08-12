package org.wiztools.restclient.ui.resheader;

import java.awt.Component;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JPanel;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ResHeaderPanelImpl extends JPanel implements ResHeaderPanel {
    
    @Inject private RESTView view;
    
    @PostConstruct
    protected void init() {
        
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        // todo
    }
    
}
