package org.wiztools.restclient;

import java.util.Arrays;

/**
 *
 * @author subwiz
 */
public final class Versions {
    public static final String CURRENT = "3.6";
    
    private static final String[] VERSIONS = new String[]{
        "3.0", "3.1", "3.2", "3.2.1", "3.2.2", "3.2.3", "3.3", "3.3.1", "3.4",
        "3.4.1", "3.4.2", "3.5", CURRENT
    };
    
    static {
        // Sort the version array for binary search
        Arrays.sort(VERSIONS);
    }
    
    public static void versionValidCheck(final String restVersion)
            throws VersionValidationException {
        if (restVersion == null) {
            throw new VersionValidationException("Attribute `version' not available for root element");
        }
        int res = Arrays.binarySearch(VERSIONS, restVersion);
        if (res == -1) {
            throw new VersionValidationException("Version not supported");
        }
    }
    
    public static class VersionValidationException extends Exception {

        public VersionValidationException(String message) {
            super(message);
        }
        
    }
}
