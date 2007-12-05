/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author Subhash
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame jf = new JFrame("WizTools.org RESTClient");
                ImageIcon icon = 
                        new ImageIcon(Main.class.getClassLoader()
                        .getResource("org/wiztools/restclient/WizLogo.jpg"));
                jf.setIconImage(icon.getImage());
                jf.setContentPane(new RESTView(jf));
                jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                jf.pack();
                jf.setResizable(false);
                jf.setVisible(true);
            }
        });
    }

}
