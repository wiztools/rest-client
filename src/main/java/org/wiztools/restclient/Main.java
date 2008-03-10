package org.wiztools.restclient;

import org.wiztools.restclient.ui.RESTFrame;
import javax.swing.SwingUtilities;

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
                new RESTFrame(RCConstants.TITLE + RCConstants.VERSION);
            }
        });
        
    }

}
