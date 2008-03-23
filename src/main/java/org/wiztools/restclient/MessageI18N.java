package org.wiztools.restclient;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 * @author Subhash
 */
public class MessageI18N {
    private static ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.restclient.messages");
    
    public static String getMessage(final String key){
        return rb.getString(key);
    }
    
    public static String getMessage(final String key, final String[] parameters){
        return MessageFormat.format(rb.getString(key), parameters);
    }
}
