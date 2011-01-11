package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public class RoReqEntityBean implements ReqEntity {
    
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

    public String getContentTypeCharsetFormatted() {
        return Util.getFormattedContentType(contentType, charSet);
    }

    @Override
    public Object clone(){
        return null;
    }
}
