package org.wiztools.restclient;

import org.wiztools.restclient.persistence.XMLException;
import java.util.Arrays;

/**
 *
 * @author subwiz
 */
public final class Versions {
    public static final String CURRENT = "3.5";
    
    private static final String[] VERSIONS = new String[]{
        "3.0", "3.1", "3.2", "3.2.1", "3.2.2", "3.2.3", "3.3", "3.3.1", "3.4",
        "3.4.1", "3.4.2", CURRENT
    };
    
    static {
        // Sort the version array for binary search
        Arrays.sort(VERSIONS);
    }
    
    public static void checkIfVersionValid(final String restVersion)
            throws XMLException {
        if (restVersion == null) {
            throw new XMLException("Attribute `version' not available for root element");
        }
        int res = Arrays.binarySearch(VERSIONS, restVersion);
        if (res == -1) {
            throw new XMLException("Version not supported");
        }
    }
}
