package org.wiztools.restclient.ui.resbody;

import java.awt.GridLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JTextArea;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.util.HexDump;

/**
 *
 * @author subwiz
 */
public class ResBodyBinaryPanel extends AbstractResBody {
    @Inject private RESTView view;
    
    private JTextArea jta = new JTextArea();
    
    @PostConstruct
    protected void init() {
        jta.setEditable(false);
        setLayout(new GridLayout());
        add(jta);
    }

    @Override
    public void setBody(byte[] body, ContentType type) {
        // Call the super method
        super.setBody(body, type);
        
        // Set the hex display value
        jta.setText(HexDump.getHexDataDumpAsString(body));
        jta.setCaretPosition(0);
    }

    @Override
    public void clearUI() {
        jta.setText("");
    }
}
