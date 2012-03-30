package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
public class OS {
    private OS() {}
    
    public static boolean isMac() {
        final String os = System.getProperty("os.name").toLowerCase();
        if(os.indexOf("mac") != -1) {
            return true;
        }
        return false;
    }
}
