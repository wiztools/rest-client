package org.wiztools.restclient.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author subwiz
 */
public class OAuth2BearerAuthBean extends AuthorizationHeaderAuthBean implements OAuth2BearerAuth {
    private final static Pattern p = Pattern.compile("Bearer\\s(.*)");
    
    public void setOAuth2BearerToken(String token) {
        setAuthorizationHeaderValue("Bearer " + token);
    }
    
    @Override
    public String getOAuth2BearerToken() {
        String fullHeader = getAuthorizationHeaderValue();
        Matcher m = p.matcher(fullHeader);
        if(m.matches()) {
            return m.group(1);
        }
        throw new IllegalStateException("OAuth2 Header does not match pattern: " + p);
    }
}
