package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public class RoReqEntityStringBean implements ReqEntityString {
    
    private final String contentType;
    private final String charSet;
    private final String body;

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String getCharset() {
        return charSet;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
    
    public RoReqEntityStringBean(final ReqEntityString requestEntity){
        contentType = requestEntity.getContentType();
        charSet = requestEntity.getCharset();
        body = requestEntity.getBody();
    }

    @Override
    public String getContentTypeCharsetFormatted() {
        return Util.getFormattedContentType(contentType, charSet);
    }

    @Override
    public Object clone(){
        return null;
    }
}
