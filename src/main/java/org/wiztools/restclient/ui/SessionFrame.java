/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JTable;

/**
 *
 * @author subwiz
 */
public class SessionFrame extends JFrame {
    
    private SessionTableModel stm = new SessionTableModel(new String[]{"Request", "Response"});
    private JTable jt = new JTable(stm);

    public SessionFrame(String title){
        super(title);
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        
        c.add(jt, BorderLayout.CENTER);
        
        pack();
    }
    
    public ISessionView getSessionView(){
        return stm;
    }
}
