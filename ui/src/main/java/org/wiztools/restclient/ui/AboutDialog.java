package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.MessageI18N;
import org.wiztools.restclient.RCConstants;
import org.wiztools.restclient.Versions;

/**
 *
 * @author schandran
 */
class AboutDialog extends EscapableDialog {

    private AboutDialog me = this;
    
    @Inject
    public AboutDialog(RESTUserInterface ui){
        super(ui.getFrame(), true);
    }
    
    @Inject
    protected void init(){
        // Title
        setTitle("About");
        
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTViewImpl.BORDER_WIDTH,
                RESTViewImpl.BORDER_WIDTH,
                RESTViewImpl.BORDER_WIDTH,
                RESTViewImpl.BORDER_WIDTH));
        jp.setLayout(new BorderLayout());
        
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_title = new JLabel(
                "<html><h2>" +
                RCConstants.TITLE + Versions.CURRENT +
                "</h2></html>");
        jp_north.add(jl_title);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new GridLayout(1, 1));
        JTextPane jtp = new JTextPane();
        jtp.setEditable(false);
        jtp.setContentType("text/html");
        jtp.setText(MessageI18N.getMessage("menu.help.about"));
        jp_center.add(new JScrollPane(jtp));
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                hideMe();
            }
        });
        getRootPane().setDefaultButton(jb_ok);
        jp_south.add(jb_ok);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        setContentPane(jp);
        
        pack();
        
        jb_ok.requestFocus();
    }
    
    @Override
    public void doEscape(AWTEvent event) {
        hideMe();
    }
    
    public void hideMe(){
        me.setVisible(false);
    }

}
