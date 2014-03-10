package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum CookieVersion {
    V_0, V_1;
    
    public int getIntValue() {
        if(this.equals(V_0)) {
            return 0;
        }
        else {
            return 1;
        }
    }
    
    public static CookieVersion getValue(int ver) throws IllegalArgumentException {
        if(ver == 0) {
            return V_0;
        }
        else if(ver == 1) {
            return V_1;
        }
        else {
            throw new IllegalArgumentException("Accepted parameters: 0/1");
        }
    }
}
