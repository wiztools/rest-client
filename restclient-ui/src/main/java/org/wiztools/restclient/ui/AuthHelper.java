package org.wiztools.restclient.ui;

import java.util.List;
import org.wiztools.restclient.HTTPAuthMethod;

/**
 *
 * @author subwiz
 */
class AuthHelper {
    static final String NONE = "None";
    static final String BASIC = "BASIC";
    static final String DIGEST = "DIGEST";
    static final String NTLM = "NTLM";
    static final String OAUTH2_BEARER = "OAuth2 Bearer";
    
    private static final String[] ALL = new String[]{NONE, BASIC, DIGEST, NTLM, OAUTH2_BEARER};
    
    static String[] getAll() {
        return ALL;
    }
    
    static boolean isBasicOrDigest(List<HTTPAuthMethod> authMethods) {
        return authMethods.contains(HTTPAuthMethod.BASIC)
                || authMethods.contains(HTTPAuthMethod.DIGEST);
    }
    
    static boolean isNtlm(List<HTTPAuthMethod> authMethods) {
        return authMethods.contains(HTTPAuthMethod.NTLM);
    }
    
    static boolean isBearer(List<HTTPAuthMethod> authMethods) {
        return authMethods.contains(HTTPAuthMethod.OAUTH_20_BEARER);
    }
    
    // String methods:
    static boolean isNone(String input) {
        return NONE.equals(input);
    }
    
    static boolean isBasicOrDigest(String input) {
        return isBasic(input) || isDigest(input);
    }
    
    static boolean isBasic(String input) {
        return BASIC.equals(input);
    }
    
    static boolean isDigest(String input) {
        return DIGEST.equals(input);
    }
    
    static boolean isNtlm(String input) {
        return NTLM.equals(input);
    }
    
    static boolean isBearer(String input) {
        return OAUTH2_BEARER.equals(input);
    }
}
