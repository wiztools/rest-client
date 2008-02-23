package org.wiztools.restclient;

import javax.swing.ImageIcon;
import org.wiztools.restclient.ui.RESTFrame;
import javax.swing.SwingUtilities;
import org.wiztools.restclient.ui.SplashScreen;

/**
 *
 * @author Subhash
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // SplashScreen display
        ImageIcon ii = new ImageIcon(Main.class.getClassLoader().getResource("org/wiztools/restclient/Splash.png"));
        final SplashScreen ss = new SplashScreen(ii);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ss.setVisible(true);
            }
        });
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RESTFrame(RCConstants.TITLE + RCConstants.VERSION);
            }
        });
        
        // SplashScreen dispose
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ss.setVisible(false);
                ss.dispose();
            }
        });
    }

}
