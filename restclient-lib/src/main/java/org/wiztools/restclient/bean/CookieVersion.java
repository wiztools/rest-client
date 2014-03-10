package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum CookieVersion {
    V_0, V_1;
    
    public int getInt() {
        if(this.equals(V_0)) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
