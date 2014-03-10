package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum CookieVersion {
    V_1("Cookie v1", 1), V_0("Cookie v0", 0);
    
    private final String description;
    private final int version;

    private CookieVersion(String description, int version) {
        this.description = description;
        this.version = version;
    }
    
    public int getIntValue() {
        return version;
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

    @Override
    public String toString() {
        return description;
    }
}
