package org.wiztools.restclient.ui;

import com.google.inject.ImplementedBy;
import java.awt.Component;
import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author subwiz
 */
@ImplementedBy(RESTMain.class)
interface RESTUserInterface {

    public JFrame getFrame();

    public File getOpenFile(final FileChooserType type);

    public File getOpenFile(final FileChooserType type, final Component parent);

    public File getSaveFile(final FileChooserType type);

    public RESTView getView();
    
    public void show();

}
