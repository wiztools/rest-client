package org.wiztools.restclient;

import org.wiztools.appupdate.Version;
import org.wiztools.appupdate.VersionImpl;

/**
 *
 * @author subwiz
 */
public final class Versions {
    public static final String CURRENT = "3.6.2";
    
    private static final Version MIN = new VersionImpl("3.0");
    private static final Version MAX = new VersionImpl(CURRENT);
    
    public static void versionValidCheck(final String restVersion)
            throws VersionValidationException {
        if (restVersion == null) {
            throw new VersionValidationException("Attribute `version' not available for root element");
        }
        Version stored = new VersionImpl(restVersion);
        if(!stored.isWithin(MIN, MAX)) {
            throw new VersionValidationException("Version not supported");
        }
    }
    
    public static class VersionValidationException extends Exception {
        public VersionValidationException(String message) {
            super(message);
        }
    }
}
