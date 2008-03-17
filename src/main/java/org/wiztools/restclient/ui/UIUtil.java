/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author Subhash
 */
public class UIUtil {
    
    public static final Font FONT_DIALOG_12_PLAIN = new Font(Font.DIALOG, Font.PLAIN, 12);
    public static final Font FONT_DIALOG_12_BOLD = new Font(Font.DIALOG, Font.BOLD, 12);
    
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
    
    public static JPanel getFlowLayoutPanelLeftAligned(JPanel panel){
        return getFlowLayoutPanelLeftAligned(null, panel);
    }
    
    public static JPanel getFlowLayoutPanelLeftAligned(String title, JPanel panel){
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        if(title != null){
            panel.setBorder(BorderFactory.createTitledBorder(title));
        }
        jp.add(panel);
        return jp;
    }
}
