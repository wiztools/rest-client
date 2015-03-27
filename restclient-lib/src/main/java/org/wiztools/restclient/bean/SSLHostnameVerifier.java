package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum SSLHostnameVerifier {
    // Deprecation note: http://stackoverflow.com/questions/29207694/apache-httpclient-4-4-hostnameverifier-transition-from-4-3-x
    
    STRICT("Strict"),
    @Deprecated BROWSER_COMPATIBLE("Browser Compatible"),
    ALLOW_ALL("Allow All");

    private final String displayName;
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
