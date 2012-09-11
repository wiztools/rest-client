package org.wiztools.restclient.ui.reqbody;

import java.awt.AWTEvent;
import java.util.ArrayList;
import java.util.List;
import org.wiztools.restclient.ui.EscapableDialog;
import org.wiztools.restclient.ui.RESTUserInterface;

/**
 *
 * @author subwiz
 */
public abstract class AddMultipartBaseDialog extends EscapableDialog {
    
    protected final List<AddMultipartPartListener> listeners = new ArrayList<AddMultipartPartListener>();
    protected RESTUserInterface rest_ui;

    public AddMultipartBaseDialog(RESTUserInterface rest_ui) {
        super(rest_ui.getFrame(), true);
        
        this.rest_ui = rest_ui;
    }

    @Override
    public void doEscape(AWTEvent event) {
        clear();
        setVisible(false);
    }
    
    public void addMultipartPartListener(AddMultipartPartListener listener) {
        listeners.add(listener);
    }
    
    public abstract void clear();
}
