package org.wiztools.restclient;

import org.wiztools.restclient.ReqEntityBean;

/**
 *
 * @author schandran
 */
public class RoReqEntityBean {
    
    private final String contentType;
    private final String charSet;
    private final String body;

    public String getBody() {
        return body;
    }

    public String getCharSet() {
        return charSet;
    }

    public String getContentType() {
        return contentType;
    }
    
    public RoReqEntityBean(final ReqEntity requestEntity){
        contentType = requestEntity.getContentType();
        charSet = requestEntity.getCharSet();
        body = requestEntity.getBody();
    }
}
