package org.wiztools.restclient.util;

import java.io.File;
import org.wiztools.restclient.IGlobalOptions;

/**
 *
 * @author subwiz
 */
public final class ConfigUtil {

    private ConfigUtil() {}
    
    public static File getConfigFile(String fileName) {
        return new File(IGlobalOptions.CONF_DIR, fileName);
    }
}
