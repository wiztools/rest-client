package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public enum HTTPVersion {
    HTTP_1_1("HTTP 1.1", "1.1"),
    HTTP_1_0("HTTP 1.0", "1.0");
    
    private String msg;
    private String versionNumber;
    
    HTTPVersion(String desc, String versionNumber){
        this.msg = desc;
        this.versionNumber = versionNumber;
    }
    
    public String versionNumber(){
        return versionNumber;
    }
    
    @Override
    public String toString(){
        return msg;
    }
    
    public static HTTPVersion getDefault(){
        return HTTP_1_1;
    }
}
