package org.wiztools.restclient.ui;

import java.util.List;

/**
 *
 * @author Subhash
 */
public interface IOptionsPanel {
    public List<String> validateInput();
    public boolean saveOptions();
    public boolean revertOptions(); // When cancel is pressed
}
