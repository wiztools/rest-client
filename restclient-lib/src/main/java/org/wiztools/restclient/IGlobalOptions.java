package org.wiztools.restclient;

import com.google.inject.ImplementedBy;
import java.io.File;
import org.wiztools.commons.SystemConstants;

/**
 *
 * @author subwiz
 */
@ImplementedBy(GlobalOptions.class)
public interface IGlobalOptions {
    
    File CONF_DIR = new File(
            SystemConstants.userHome + SystemConstants.fileSeparator
                    + ".rest-client");

    OptionsLock acquire();

    String getProperty(String key);
    
    boolean isPropertyTrue(String key);

    void removeProperty(String key);

    void setProperty(String key, String value);

    void writeProperties();

}
