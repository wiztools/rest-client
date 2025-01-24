package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum HostnameVerifier {

    STRICT("Strict"),
    ALLOW_ALL("Allow All");

    private final String displayName;
    HostnameVerifier(String displayName){
        this.displayName = displayName;
    }

    @Override
    public String toString(){
        return displayName;
    }

    public static HostnameVerifier[] getAll(){
        return new HostnameVerifier[]{STRICT, ALLOW_ALL};
    }
}
