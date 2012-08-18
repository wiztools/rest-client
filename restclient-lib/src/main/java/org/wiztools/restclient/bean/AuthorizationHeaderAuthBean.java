package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class AuthorizationHeaderAuthBean implements AuthorizationHeaderAuth {

    protected String authorizationHeaderValue;
    
    public void setAuthorizationHeaderValue(String value) {
        authorizationHeaderValue = value;
    }
    
    @Override
    public String getAuthorizationHeaderValue() {
        return authorizationHeaderValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuthorizationHeaderAuthBean other = (AuthorizationHeaderAuthBean) obj;
        if ((this.authorizationHeaderValue == null) ? (other.authorizationHeaderValue != null) : !this.authorizationHeaderValue.equals(other.authorizationHeaderValue)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.authorizationHeaderValue != null ? this.authorizationHeaderValue.hashCode() : 0);
        return hash;
    }
    
}
