package org.wiztools.restclient.bean;

import java.util.Arrays;

/**
 *
 * @author subwiz
 */
public class UsernamePasswordAuthBaseBean implements UsernamePasswordAuth {
    
    protected String username;
    protected char[] password;

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public final String getUsername() {
        return username;
    }

    @Override
    public final char[] getPassword() {
        return Arrays.copyOf(password, password.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UsernamePasswordAuthBaseBean other = (UsernamePasswordAuthBaseBean) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        if (!Arrays.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 67 * hash + Arrays.hashCode(this.password);
        return hash;
    }
    
}
