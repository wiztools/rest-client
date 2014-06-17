package org.wiztools.restclient.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.wiztools.commons.StringUtil;

/**
 *
 * @author subwiz
 */
public final class RCUIConstants {
    
    private static final Logger LOG = Logger.getLogger(RCUIConstants.class.getName());
    
    private RCUIConstants() {}
    
    public static final String SYS_PROPERTY_FONT_SIZE = "rc:ui-font-size";
    public static final int DEFAULT_UI_FONT_SIZE = 12;
    
    public static int getUIFontSize() {
        final String t = System.getProperty(RCUIConstants.SYS_PROPERTY_FONT_SIZE);
        if(StringUtil.isNotEmpty(t)) {
            try {
                final int fontSize = Integer.parseInt(t);
                if(fontSize < RCUIConstants.DEFAULT_UI_FONT_SIZE) {
                    throw new NumberFormatException("Font size value cannot be less than "
                        + RCUIConstants.DEFAULT_UI_FONT_SIZE + ".");
                }
                return fontSize;
            }
            catch(NumberFormatException ex) {
                LOG.log(Level.WARNING, "Illegal font size specified: {0}", t);
            }
        }
        return -1;
    }
    
    public static int getUIFontSizeDefault() {
        final int size = getUIFontSize();
        return size==-1? DEFAULT_UI_FONT_SIZE: size;
    }
}
