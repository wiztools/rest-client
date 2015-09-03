package org.wiztools.restclient;

import com.google.inject.ImplementedBy;
import java.io.File;
import org.wiztools.commons.SystemProperty;

/**
 *
 * @author subwiz
 */
@ImplementedBy(GlobalOptions.class)
public interface IGlobalOptions {
    
    File CONF_DIR = new File(
            SystemProperty.userHome + SystemProperty.fileSeparator
                    + ".rest-client");

    OptionsLock acquire();

    String getProperty(String key);
    
    boolean isPropertyTrue(String key);

    void removeProperty(String key);

    void setProperty(String key, String value);

    void writeProperties();

}
