/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.Component;
import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author subwiz
 */
public interface RESTUserInterface {

    public JFrame getFrame();

    public File getOpenFile(final FileChooserType type);

    public File getOpenFile(final FileChooserType type, final Component parent);

    public File getSaveFile(final FileChooserType type);

    public RESTView getView();

}
