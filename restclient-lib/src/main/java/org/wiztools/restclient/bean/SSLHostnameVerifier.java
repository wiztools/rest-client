package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum SSLHostnameVerifier {
    STRICT("Strict"), BROWSER_COMPATIBLE("Browser Compatible"), ALLOW_ALL("Allow All");

    private String displayName;
    SSLHostnameVerifier(String displayName){
        this.displayName = displayName;
    }

    @Override
    public String toString(){
        return displayName;
    }

    public static SSLHostnameVerifier[] getAll(){
        return new SSLHostnameVerifier[]{STRICT, BROWSER_COMPATIBLE, ALLOW_ALL};
    }
}
