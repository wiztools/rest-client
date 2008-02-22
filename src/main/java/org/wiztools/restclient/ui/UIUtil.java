/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

/**
 *
 * @author Subhash
 */
public class UIUtil {
    private static final RCFileView fileView = new RCFileView();
    public static final JFileChooser getNewJFileChooser(){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileView(fileView);
        return jfc;
    }
    
    public static ImageIcon getIconFromClasspath(String path){
        URL url = UIUtil.class.getClassLoader().getResource(path);
        return new ImageIcon(url);
    }
}
