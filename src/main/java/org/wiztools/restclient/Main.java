/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import javax.swing.JFrame;
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
        JFrame jf = new JFrame("WizTools.org RESTClient");
        jf.setContentPane(new RESTView(jf));
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }

}
