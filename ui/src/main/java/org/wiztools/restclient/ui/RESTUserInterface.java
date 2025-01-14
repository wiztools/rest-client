package org.wiztools.restclient.ui;

import org.wiztools.restclient.ImplementedBy;
import java.awt.Component;
import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author subwiz
 */
@ImplementedBy(RESTMain.class)
public interface RESTUserInterface {

    public JFrame getFrame();

    public File getOpenFile(final FileChooserType type);

    public File getOpenFile(final FileChooserType type, final Component parent);

    public File getSaveFile(final FileChooserType type);

    public RESTView getView();

}
