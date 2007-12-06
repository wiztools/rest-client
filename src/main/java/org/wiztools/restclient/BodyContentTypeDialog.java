/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class BodyContentTypeDialog extends JDialog {
    
    private static final String[] contentTypeArr = 
            new String[]{"text/plain",
            "application/xml",
            "application/json",
            "application/x-www-form-urlencoded"};
    
    private static final String[] charSetArr =
            new String[]{"UTF-8"};
    
    private JComboBox jcb_content_type = new JComboBox(contentTypeArr);
    private JComboBox jcb_char_set = new JComboBox(charSetArr);
    
    private final BodyContentTypeDialog me;
    
    private String contentType;
    private String charSet;
    
    
    BodyContentTypeDialog(Frame f){
        // true means Modal:
        super(f, true);
        me = this;
        setTitle("Body Content-type");
        init();
        contentType = (String)jcb_content_type.getSelectedItem();
        charSet = (String)jcb_char_set.getSelectedItem();
    }
    
    private void init(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout());
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(2, 1, 5, 5));
        JLabel jl_content_type = new JLabel("Content-type: ");
        jl_content_type.setLabelFor(jcb_content_type);
        JLabel jl_char_set = new JLabel("Char-set: ");
        jl_char_set.setLabelFor(jcb_char_set);
        jp_center_west.add(jl_content_type);
        jp_center_west.add(jl_char_set);
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(2, 1, 5, 5));
        jp_center_center.add(jcb_content_type);
        jp_center_center.add(jcb_char_set);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe(true);
            }
        });
        JButton jb_cancel = new JButton("Cancel");
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe(false);
            }
        });
        jp_south.add(jb_ok);
        jp_south.add(jb_cancel);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        JPanel jp_encp = new JPanel();
        jp_encp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jp_encp.add(jp);
        
        this.setContentPane(jp_encp);
        this.pack();
    }
    
    void showMe(){
        this.setLocationRelativeTo(this.getParent());
        this.setVisible(true);
    }
    
    void hideMe(final boolean isOk){   
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(isOk){
                    contentType = (String)jcb_content_type.getSelectedItem();
                    charSet = (String)jcb_char_set.getSelectedItem();
                }
                else{
                    jcb_content_type.setSelectedItem(me.contentType);
                    jcb_char_set.setSelectedItem(me.charSet);
                }
                me.setVisible(false);
            }
        });
    }
    
    String getContentType(){
        return this.contentType;
    }
    
    String getCharSet(){
        return this.charSet;
    }
}
