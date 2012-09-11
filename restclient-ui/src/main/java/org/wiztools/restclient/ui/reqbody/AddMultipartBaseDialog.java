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

    public AddMultipartBaseDialog(RESTUserInterface rest_ui) {
        super(rest_ui.getFrame(), true);
    }

    @Override
    public void doEscape(AWTEvent event) {
        setVisible(false);
    }
    
    public void addMultipartPartListener(AddMultipartPartListener listener) {
        listeners.add(listener);
    }
}
