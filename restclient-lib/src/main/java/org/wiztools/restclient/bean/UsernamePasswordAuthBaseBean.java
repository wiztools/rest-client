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
    
}
