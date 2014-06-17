package org.wiztools.restclient.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.ServiceLocator;

/**
 *
 * @author Subhash
 */
public class Main {
    
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    
    private static void setGlobalUIFontSize(final int fontSize){
        Font f = new Font(Font.DIALOG, Font.PLAIN, fontSize);
        //UIManager.put("Label.font", f);
        //UIManager.put("Button.font", f);
        //UIManager.put("RadioButton.font", f);
        ArrayList<String> excludes = new ArrayList<String>();
        //excludes.add("TitledBorder.font");
        //excludes.add("MenuBar.font");
        //excludes.add("MenuItem.font");
        //excludes.add("MenuItem.acceleratorFont");
        //excludes.add("Menu.font");
        //excludes.add("TabbedPane.font");
        excludes.add("");
        
        Enumeration itr = UIManager.getDefaults().keys();
        while(itr.hasMoreElements()){
            Object o = itr.nextElement();
            if(o instanceof String) {
                String key = (String) o;
                Object value = UIManager.get (key);
                if ((value instanceof javax.swing.plaf.FontUIResource)
                        && (!excludes.contains(key))){
                    LOG.fine(key);
                    UIManager.put (key, f);
                }
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        // Set the font:
        final String t = System.getProperty(RCUIConstants.SYS_PROPERTY_FONT_SIZE);
        if(StringUtil.isNotEmpty(t)) {
            try {
                final int fontSize = Integer.parseInt(t);
                if(fontSize < RCUIConstants.MIN_UI_FONT_SIZE) {
                    throw new NumberFormatException("Font size value cannot be less than "
                        + RCUIConstants.MIN_UI_FONT_SIZE + ".");
                }
                setGlobalUIFontSize(fontSize);
            }
            catch(NumberFormatException ex) {
                LOG.log(Level.WARNING, "Illegal font size specified: {0}", t);
            }
        }
        
        // Work on the UI:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RESTUserInterface ui = ServiceLocator.getInstance(
                        RESTUserInterface.class);
            }
        });
        
    }

}
