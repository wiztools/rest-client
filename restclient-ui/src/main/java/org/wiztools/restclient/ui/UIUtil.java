package org.wiztools.restclient.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.wiztools.filechooser.FileChooser;
import org.wiztools.filechooser.JFCFileChooser;

/**
 *
 * @author Subhash
 */
public final class UIUtil {
    
    private UIUtil(){}
    
    public static final Font FONT_DIALOG_PLAIN = new Font(Font.DIALOG, Font.PLAIN,
            RCUIConstants.getUIFontSizeDefault());
    public static final Font FONT_DIALOG_BOLD = new Font(Font.DIALOG, Font.BOLD,
            RCUIConstants.getUIFontSizeDefault());
    public static final Font FONT_MONO_PLAIN = new Font(Font.MONOSPACED, Font.PLAIN,
            RCUIConstants.getUIFontSizeDefault());
    public static final Font FONT_BIG = new Font(Font.DIALOG, Font.PLAIN, 18);
    
    public static final String LAST_CWD_REQ = "filesystem.lastdir.request";
    public static final String LAST_CWD_RES = "filesystem.lastdir.response";
    public static final String LAST_CWD_ARC = "filesystem.lastdir.archive";
    public static final String LAST_CWD_HIS = "filesystem.lastdir.history";
    
    private static final RCFileView fileView = new RCFileView();
    public static final FileChooser getNewFileChooser(){
        JFCFileChooser jfc = new JFCFileChooser();
        jfc.setFileView(fileView);
        return jfc;
        
        // return new FDFileChooser();
    }
    
    public static ImageIcon getIconFromClasspath(String path){
        URL url = UIUtil.class.getClassLoader().getResource(path);
        return new ImageIcon(url);
    }
    
    public static JPanel getFlowLayoutLeftAlignedMulti(Component ... components) {
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        for(Component c: components) {
            jp.add(c);
        }
        
        return jp;
    }
    
    public static JPanel getFlowLayoutPanelLeftAligned(Component component){
        return getFlowLayoutPanelLeftAligned(null, component);
    }
    
    public static JPanel getFlowLayoutPanelLeftAligned(String title, Component component){
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        if(title != null){
            if(component instanceof JPanel){
                JPanel p = (JPanel)component;
                p.setBorder(BorderFactory.createTitledBorder(title));
            }
        }
        jp.add(component);
        return jp;
    }
    
    public static Component getJScrollPaneWrapped(Component component) {
        return new JScrollPane(component);
    }
    
    public static void clipboardCopy(String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str), null);
    }
    
    public static boolean hasRetinaDisplay() {
        Object obj = Toolkit.getDefaultToolkit()
                .getDesktopProperty("apple.awt.contentScaleFactor");
        if (obj instanceof Float) {
            Float f = (Float) obj;
            int scale = f.intValue();
            return (scale == 2);
        }
        return false;
    }
}
