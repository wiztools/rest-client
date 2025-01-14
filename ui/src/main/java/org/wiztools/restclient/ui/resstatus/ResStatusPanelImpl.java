package org.wiztools.restclient.ui.resstatus;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ResStatusPanelImpl extends JPanel implements ResStatusPanel {
    
    private JTextField jtf = new JTextField();
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        JLabel jl_res_statusLine = new JLabel("Status: ");
        add(jl_res_statusLine, BorderLayout.WEST);
        jtf.setColumns(35);
        jtf.setEditable(false);
        add(jtf, BorderLayout.CENTER);
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public String getStatus() {
        return jtf.getText();
    }

    @Override
    public void setStatus(String status) {
        jtf.setText(status);
    }

    @Override
    public void clear() {
        jtf.setText("");
    }
}
