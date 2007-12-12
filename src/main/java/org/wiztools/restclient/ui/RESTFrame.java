/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class RESTFrame extends JFrame {
    
    private RESTView view;
    private AboutDialog aboutDialog;
    
    public RESTFrame(final String title){
        super(title);
        init();
    }
    
    private void createMenu(){
        JMenuBar jmb = new JMenuBar();
        
        // File menu
        JMenu jm_file = new JMenu("File");
        jm_file.setMnemonic('f');
        
        JMenuItem jmi_open_req = new JMenuItem("Open Request");
        jmi_open_req.setMnemonic('o');
        jm_file.add(jmi_open_req);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_save_req = new JMenuItem("Save Request");
        jmi_save_req.setMnemonic('q');
        jm_file.add(jmi_save_req);
        
        JMenuItem jmi_save_res = new JMenuItem("Save Response");
        jmi_save_res.setMnemonic('s');
        jm_file.add(jmi_save_res);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_exit = new JMenuItem("Exit");
        jmi_exit.setMnemonic('x');
        jmi_exit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        jmi_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                shutdownCall();
            }
        });
        jm_file.add(jmi_exit);
        
        // Help menu
        JMenu jm_help = new JMenu("Help");
        
        JMenuItem jmi_about = new JMenuItem("About");
        jmi_about.setMnemonic('a');
        jmi_about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        aboutDialog.setVisible(true);
                    }
                });
            }
        });
        jm_help.add(jmi_about);
        
        // Add menus to menu-bar
        jmb.add(jm_file);
        jmb.add(jm_help);
        
        this.setJMenuBar(jmb);
    }
    
    private void init(){
        // Create AboutDialog
        aboutDialog = new AboutDialog(this);
        
        createMenu();
        
        ImageIcon icon = 
                new ImageIcon(this.getClass().getClassLoader()
                .getResource("org/wiztools/restclient/WizLogo.png"));
        setIconImage(icon.getImage());
        view = new RESTView(this);
        setContentPane(view);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                shutdownCall();
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void shutdownCall(){
        System.out.println("Exiting...");
        System.exit(0);
    }
}
