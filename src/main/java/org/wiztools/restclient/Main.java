/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import javax.swing.SwingUtilities;

/**
 *
 * @author Subhash
 */
public class Main {
    
    public static final String TITLE = "WizTools.org RESTClient ";
    public static final String VERSION = "2.0";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RESTFrame(TITLE + VERSION);
            }
        });
    }

}
