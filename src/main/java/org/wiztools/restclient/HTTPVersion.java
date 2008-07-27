package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public enum HTTPVersion {
    HTTP_1_1("HTTP 1.1"),
    HTTP_1_0("HTTP 1.0");
    
    private String msg;
    
    HTTPVersion(String desc){
        this.msg = desc;
    }
    
    @Override
    public String toString(){
        return msg;
    }
}
