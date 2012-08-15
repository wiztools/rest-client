package org.wiztools.restclient.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.RCConstants;

/**
 *
 * @author subwiz
 */
public class StatusBarPanelImpl extends JPanel implements StatusBarPanel {
    
    @Inject private RESTView view;
    
    private JLabel jl_status = new JLabel(" " + RCConstants.TITLE);
    private JProgressBar jpb_status = new JProgressBar();
    
    private Calendar statusLastUpdated;
    
    @PostConstruct
    protected void init() {
        // Status clear timer:
        // Start status clear timer:
        statusLastUpdated = Calendar.getInstance();
        new Timer(5*1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar c = (Calendar)statusLastUpdated.clone();
                c.add(Calendar.SECOND, 20);
                if(Calendar.getInstance().after(c)){
                    setStatus(RCConstants.TITLE);
                }
            }
        }).start();
        
        // Label
        setBorder(BorderFactory.createBevelBorder(1));
        setLayout(new GridLayout(1, 2));
        jl_status.setFont(UIUtil.FONT_DIALOG_12_PLAIN);
        add(jl_status);
        
        // Progress bar
        JPanel jp_progressBar = new JPanel();
        jp_progressBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        Dimension d = jpb_status.getPreferredSize();
        d.height = d.height - 2;
        jpb_status.setPreferredSize(d);
        jpb_status.setIndeterminate(true);
        jpb_status.setVisible(false);
        jp_progressBar.add(jpb_status);
        add(jp_progressBar);
    }

    @Override
    public void setStatus(String status) {
        jl_status.setText(" " + status);
        statusLastUpdated = Calendar.getInstance();
    }

    @Override
    public void showProgressBar() {
        jpb_status.setVisible(true);
    }

    @Override
    public void hideProgressBar() {
        jpb_status.setVisible(false);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
