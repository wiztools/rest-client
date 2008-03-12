package org.wiztools.restclient;

import org.wiztools.restclient.ui.RESTFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Subhash
 */
public class Main {
    
    /*private static void globalUISettings(){
        Font f = new Font(Font.DIALOG, Font.PLAIN, 12);
        //UIManager.put("Label.font", f);
        //UIManager.put("Button.font", f);
        //UIManager.put("RadioButton.font", f);
        ArrayList excludes = new ArrayList();
        excludes.add("TitledBorder.font");
        excludes.add("MenuBar.font");
        excludes.add("MenuItem.font");
        excludes.add("MenuItem.acceleratorFont");
        excludes.add("Menu.font");
        excludes.add("TabbedPane.font");
        excludes.add("");
        
        Enumeration itr = UIManager.getDefaults().keys();
        while(itr.hasMoreElements()){
            Object key = itr.nextElement();
            Object value = UIManager.get (key);
            if ((value instanceof javax.swing.plaf.FontUIResource)
                    && (!excludes.contains(key))){
                System.out.println(key);
                UIManager.put (key, f);
            }
        }
    }*/
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Do the global settings:
        //globalUISettings();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RESTFrame(RCConstants.TITLE + RCConstants.VERSION);
            }
        });
        
    }

}
