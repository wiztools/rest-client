package org.wiztools.restclient.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
        ArrayList<String> excludes = new ArrayList<>();
        //excludes.add("TitledBorder.font");
        //excludes.add("MenuBar.font");
        //excludes.add("MenuItem.font");
        //excludes.add("MenuItem.acceleratorFont");
        //excludes.add("Menu.font");
        //excludes.add("TabbedPane.font");
        excludes.add("");

        Enumeration<Object> itr = UIManager.getDefaults().keys();
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
        final int fontSize = RCUIConstants.getUIFontSize();
        if(fontSize != -1) {
            setGlobalUIFontSize(fontSize);
        }

        // Work on the UI:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ServiceLocator.getInstance(RESTUserInterface.class);
            }
        });
    }

}
