/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

/**
 *
 * @author subwiz
 */
public class SessionFrame extends JFrame {
    
    private final SessionFrame me;
    
    private final SessionTableModel stm = new SessionTableModel(new String[]{"Request", "Response"});
    private final JTable jt = new JTable(stm);

    public SessionFrame(String title){
        super(title);
        me = this;
        
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        
        jt.setPreferredSize(new Dimension(200, 300));
        c.add(new JScrollPane(jt), BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                int confirmValue = JOptionPane.showConfirmDialog(me, "You will loose any unsaved session data if you\n" +
                        " close the Session Window. Do you want to close?", "Close Session Window?", JOptionPane.YES_NO_OPTION);
                if(confirmValue == JOptionPane.YES_OPTION){
                    stm.clear();
                    me.setVisible(false);
                }
            }
        });
        
        pack();
    }
    
    public ISessionView getSessionView(){
        return stm;
    }
}
