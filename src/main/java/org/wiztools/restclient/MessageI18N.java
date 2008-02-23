/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

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
}
