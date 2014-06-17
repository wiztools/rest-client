package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
        setLayout(new BorderLayout());//new GridLayout(1, 2));
        jl_status.setFont(UIUtil.FONT_DIALOG_PLAIN);
        add(jl_status, BorderLayout.CENTER);
        
        // Progress bar
        Dimension d = jpb_status.getPreferredSize();
        d.height = d.height - 4;
        jpb_status.setPreferredSize(d);
        jpb_status.setIndeterminate(true);
        jpb_status.setVisible(false);
        add(jpb_status, BorderLayout.EAST);
    }

    @Override
    public void setStatus(String status) {
        jl_status.setText(" " + status);
        jl_status.setToolTipText(status);
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

    @Override
    public void clear() {
        setStatus("Cleared!");
    }
}
