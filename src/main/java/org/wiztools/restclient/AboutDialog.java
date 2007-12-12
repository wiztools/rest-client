/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class AboutDialog extends EscapableDialog {

    private Frame frame;
    private AboutDialog me;
    
    public AboutDialog(Frame f){
        super(f, true);
        frame = f;
        me = this;
        init();
    }
    
    private void init(){
        // Title
        setTitle("About");
        
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH));
        jp.setLayout(new BorderLayout());
        
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_title = new JLabel(
                "<html><h2>" +
                Main.TITLE + Main.VERSION +
                "</h2></html>");
        jp_north.add(jl_title);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new FlowLayout());
        JLabel jl_text = new JLabel();
        String strText = "<html>RESTClient is a Java platform client application <br>" +
                "to test RESTful webservices. It can be used <br>" +
                "to test variety of HTTP communications. <br><br>" +
                "<b>http://rest-client.googlecode.com/</b></html>";
        jl_text.setText(strText);
        jp_center.add(jl_text);
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe();
            }
        });
        jp_south.add(jb_ok);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        setContentPane(jp);
        
        pack();
    }
    
    @Override
    public void setVisible(final boolean visible){
        me.setLocationRelativeTo(frame);
        super.setVisible(visible);
    }
    
    @Override
    public void doEscape(KeyEvent event) {
        hideMe();
    }
    
    public void hideMe(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                me.setVisible(false);
            }
        });
    }

}
