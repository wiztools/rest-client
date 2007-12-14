/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class OptionsDialog extends EscapableDialog {
    
    private final Frame frame;
    
    private JCheckBox jcb_enable = new JCheckBox("Enable");
    private JCheckBox jcb_auth_enable = new JCheckBox("Authentication");
    
    private final int jtf_size = 25;
    private JTextField jtf_host = new JTextField(jtf_size);
    private JTextField jtf_port = new JTextField(jtf_size);
    private JTextField jtf_username = new JTextField(jtf_size);
    private JPasswordField jpf_password = new JPasswordField(jtf_size);

    public OptionsDialog(Frame f){
        super(f, true);
        frame = f;
        init();
    }
    
    private void init(){
        this.setTitle("Options");
        
        toggleEnable(false);
        
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp.setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jcb_enable.setMnemonic('e');
        jcb_enable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jcb_enable.isSelected()){
                    toggleEnable(true);
                }
                else{
                    toggleEnable(false);
                }
            }
        });
        jp_north.add(jcb_enable);
        jcb_auth_enable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jcb_auth_enable.isSelected()){
                    toggleAuthEnable(true);
                }
                else{
                    toggleAuthEnable(false);
                }
            }
        });
        jp_north.add(jcb_auth_enable);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_center_west.add(new JLabel("Host: "));
        jp_center_west.add(new JLabel("Port: "));
        jp_center_west.add(new JLabel("Username: "));
        jp_center_west.add(new JLabel("Password: "));
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_center_center.add(jtf_host);
        jp_center_center.add(jtf_port);
        jp_center_center.add(jtf_username);
        jp_center_center.add(jpf_password);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);

        // Tabbed pane
        JTabbedPane jtp = new JTabbedPane();
        jtp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jtp.addTab("Proxy", jp);
        
        // Encapsulating
        JPanel jp_encp = new JPanel();
        jp_encp.setLayout(new BorderLayout());
        jp_encp.add(jtp, BorderLayout.CENTER);
        
        // South
        JPanel jp_encp_south = new JPanel();
        jp_encp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionClose();
            }
        });
        JButton jb_cancel = new JButton("Cancel");
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionClose();
            }
        });
        jp_encp_south.add(jb_ok);
        jp_encp_south.add(jb_cancel);
        
        jp_encp.add(jp_encp_south, BorderLayout.SOUTH);
        
        this.setContentPane(jp_encp);
        
        pack();
    }
    
    private void toggleEnable(final boolean boo){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jtf_host.setEnabled(boo);
                jtf_port.setEnabled(boo);
                jcb_auth_enable.setEnabled(boo);
                if(!boo){ // if boo is false
                    toggleAuthEnable(false);
                }
                else if(boo && jcb_auth_enable.isSelected()){
                    toggleAuthEnable(true);
                }
            }
        });
    }
    
    private void toggleAuthEnable(final boolean boo){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jtf_username.setEnabled(boo);
                jpf_password.setEnabled(boo);
            }
        });
    }
    
    @Override
    public void setVisible(boolean boo){
        this.setLocationRelativeTo(frame);
        super.setVisible(boo);
    }
    
    @Override
    public void doEscape(KeyEvent event) {
        actionClose();
    }
    
    private void actionClose(){
        this.setVisible(false);
    }
}
