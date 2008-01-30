package org.wiztools.restclient.test;

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
    
    public RoReqEntityBean(final ReqEntityBean requestEntity){
        contentType = requestEntity.getContentType();
        charSet = requestEntity.getCharSet();
        body = requestEntity.getBody();
    }
}
