/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public class ReqEntityBean {
    
    private String contentType;
    private String charSet;
    private String body;
    
    public ReqEntityBean(String body, String contentType, String charSet){
        this.body = body;
        this.contentType = contentType;
        this.charSet = charSet;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
