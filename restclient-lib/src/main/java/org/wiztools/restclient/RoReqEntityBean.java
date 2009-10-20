package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author schandran
 */
public class RoReqEntityBean implements ReqEntity {
    
    private final String contentType;
    private final String charSet;
    private final byte[] bodyBytes;

    public String getBody() throws UnsupportedEncodingException {
        return new String(bodyBytes, charSet);
    }

    public byte[] getBodyBytes(){
        return this.bodyBytes;
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
        bodyBytes = requestEntity.getBodyBytes();
    }

    public String getContentTypeCharsetFormatted() {
        return Util.getFormattedContentType(contentType, charSet);
    }

    @Override
    public Object clone(){
        return null;
    }
}
