package org.wiztools.restclient.util;

import java.io.File;
import java.io.FileFilter;
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
    
    public static File[] getTestDependencies() {
        File libDir = new File(IGlobalOptions.CONF_DIR, "lib");
        if(libDir.exists() && libDir.canRead() && libDir.isDirectory()) {
            return libDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(pathname.getName().toLowerCase().endsWith(".jar")
                            && pathname.canRead()
                            && pathname.isFile()) {
                        return true;
                    }
                    return false;
                }
            });
        }
        else {
            return new File[]{};
        }
    }
}
