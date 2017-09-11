package org.wiztools.restclient.ui.customrest;

/**
 * created by 10192065 on 2017/9/8
 * User: 10192065(yzg)
 * Date: 2017/9/8
 */
public class xmlFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(java.io.File f) {
        if (f.isDirectory()) return true;
        return f.getName().endsWith(".xml");
    }

    public String getDescription() {
        return ".xml";
    }
}