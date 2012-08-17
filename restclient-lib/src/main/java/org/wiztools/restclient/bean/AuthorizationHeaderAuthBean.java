package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class AuthorizationHeaderAuthBean implements AuthorizationHeaderAuth {

    private String authorizationHeaderValue;
    
    public void setAuthorizationHeaderValue(String value) {
        authorizationHeaderValue = value;
    }
    
    @Override
    public String getAuthorizationHeaderValue() {
        return authorizationHeaderValue;
    }
    
}
