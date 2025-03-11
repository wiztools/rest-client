package org.wiztools.restclient.ui;

import java.util.List;
import org.wiztools.restclient.bean.HTTPAuthMethod;

/**
 *
 * @author subwiz
 */
public class AuthHelper {

    public static final String NONE = "None";
    public static final String BASIC = "BASIC";
    public static final String DIGEST = "DIGEST";
    public static final String OAUTH2_BEARER = "OAuth2 Bearer";

    private static final String[] ALL = new String[] {
        NONE,
        BASIC,
        DIGEST,
        OAUTH2_BEARER,
    };

    public static String[] getAll() {
        return ALL;
    }

    public static boolean isBasicOrDigest(List<HTTPAuthMethod> authMethods) {
        return (
            authMethods.contains(HTTPAuthMethod.BASIC) ||
            authMethods.contains(HTTPAuthMethod.DIGEST)
        );
    }

    public static boolean isBearer(List<HTTPAuthMethod> authMethods) {
        return authMethods.contains(HTTPAuthMethod.OAUTH_20_BEARER);
    }

    // String methods:
    public static boolean isNone(String input) {
        return NONE.equals(input);
    }

    public static boolean isBasicOrDigest(String input) {
        return isBasic(input) || isDigest(input);
    }

    public static boolean isBasic(String input) {
        return BASIC.equals(input);
    }

    public static boolean isDigest(String input) {
        return DIGEST.equals(input);
    }

    public static boolean isBearer(String input) {
        return OAUTH2_BEARER.equals(input);
    }
}
