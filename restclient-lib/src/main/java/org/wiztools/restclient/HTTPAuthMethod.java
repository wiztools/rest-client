package org.wiztools.restclient;

import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public enum HTTPAuthMethod {
    BASIC, DIGEST, OAUTH_20;

    private static final Logger LOG = Logger.getLogger(HTTPAuthMethod.class.getName());

    public static HTTPAuthMethod get(final String name){
        if("BASIC".equals(name)){
            return BASIC;
        }
        else if("DIGEST".equals(name)){
            return DIGEST;
        }
        else if("OAUTH_20".equals(name)) {
            return OAUTH_20;
        }
        else{
            LOG.warning("HTTPAuthMethod string unrecognized: " + name);
            LOG.warning("Sending default method: " + BASIC);
            return BASIC;
        }
    }
}
