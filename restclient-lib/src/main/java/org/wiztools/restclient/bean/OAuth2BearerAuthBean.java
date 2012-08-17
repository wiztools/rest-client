package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class OAuth2BearerAuthBean extends AuthorizationHeaderAuthBean {
    void setOAuth2BearerToken(String token) {
        setAuthorizationHeaderValue("Bearer " + token);
    }
}
