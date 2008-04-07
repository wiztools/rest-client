package org.wiztools.restclient.ui;

import java.util.List;

/**
 *
 * @author Subhash
 */
public interface IOptionsPanel {
    /**
     * Method for loading last saved option values from persistent storage.
     */
    public void initOptions();
    
    /**
     * Method for writing options to persistent storage.
     */
    public void shutdownOptions();
    
    /**
     * When Ok is pressed in the Options dialog, this method is called first.
     * @return List of errors, or an empty List, or null
     */
    public List<String> validateInput();
    
    /**
     * After validation step, this method is called.
     * @return true on success, false on failure to set the options
     */
    public boolean saveOptions();
    
    /**
     * When Cancel button is pressed, this method is called--basically
     * used to revert the Option UI values to the last selected value.
     * @return
     */
    public boolean revertOptions(); // When cancel is pressed
}
