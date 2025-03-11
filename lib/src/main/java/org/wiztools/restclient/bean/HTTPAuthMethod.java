package org.wiztools.restclient.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public enum HTTPAuthMethod {
    BASIC,
    DIGEST,
    OAUTH_20_BEARER;

    private static final Logger LOG = Logger.getLogger(
        HTTPAuthMethod.class.getName()
    );

    public static HTTPAuthMethod get(final String name) {
        if ("BASIC".equals(name)) {
            return BASIC;
        } else if ("DIGEST".equals(name)) {
            return DIGEST;
        } else if ("OAUTH_20_BEARER".equals(name)) {
            return OAUTH_20_BEARER;
        } else {
            LOG.log(
                Level.WARNING,
                "HTTPAuthMethod string unrecognized: {0}",
                name
            );
            LOG.log(Level.WARNING, "Sending default method: {0}", BASIC);
            return BASIC;
        }
    }
}
