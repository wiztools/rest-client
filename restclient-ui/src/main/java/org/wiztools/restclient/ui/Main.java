package org.wiztools.restclient.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import org.wiztools.restclient.RCConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Subhash
 */
public class Main {
    
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    
    private static void globalUISettings(){
        Font f = new Font(Font.DIALOG, Font.PLAIN, 12);
        //UIManager.put("Label.font", f);
        //UIManager.put("Button.font", f);
        //UIManager.put("RadioButton.font", f);
        ArrayList excludes = new ArrayList();
        //excludes.add("TitledBorder.font");
        //excludes.add("MenuBar.font");
        //excludes.add("MenuItem.font");
        //excludes.add("MenuItem.acceleratorFont");
        //excludes.add("Menu.font");
        //excludes.add("TabbedPane.font");
        excludes.add("");
        
        Enumeration itr = UIManager.getDefaults().keys();
        while(itr.hasMoreElements()){
            Object key = itr.nextElement();
            Object value = UIManager.get (key);
            if ((value instanceof javax.swing.plaf.FontUIResource)
                    && (!excludes.contains(key))){
                LOG.fine(key.toString());
                UIManager.put (key, f);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Do the global settings:
        globalUISettings();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RESTMain(RCConstants.TITLE + RCConstants.VERSION);
            }
        });
        
    }

}
