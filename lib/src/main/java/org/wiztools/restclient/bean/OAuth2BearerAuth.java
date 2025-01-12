package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface OAuth2BearerAuth extends AuthorizationHeaderAuth {

    String getOAuth2BearerToken();
    
}
