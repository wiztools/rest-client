package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 *
 * @author schandran
 */
public final class ReqEntityBean implements ReqEntity{
    
    private String contentType;
    private String charSet;
    private byte[] bodyBytes;
    
    public ReqEntityBean(byte[] body, String contentType, String charSet){
        this.bodyBytes = body;
        this.contentType = contentType;
        this.charSet = charSet;
    }

    public String getBody() throws UnsupportedEncodingException {
        return new String(bodyBytes, charSet);
    }

    public byte[] getBodyBytes(){
        return bodyBytes;
    }

    public void setBodyBytes(byte[] body) {
        this.bodyBytes = body;
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
    
    public String getContentTypeCharsetFormatted(){
        return Util.getFormattedContentType(contentType, charSet);
    }
    
    @Override
    public Object clone(){
        ReqEntityBean cloned = new ReqEntityBean(bodyBytes, contentType, charSet);
        return cloned;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o instanceof ReqEntityBean){
            ReqEntityBean bean = (ReqEntityBean)o;
            boolean isEqual = true;
            isEqual = isEqual && (this.bodyBytes == null? bean.bodyBytes == null: Arrays.equals(this.bodyBytes, bean.bodyBytes));
            isEqual = isEqual && (this.charSet == null? bean.charSet == null: this.charSet.equals(bean.charSet));
            isEqual = isEqual && (this.contentType == null? bean.contentType == null: this.contentType.equals(bean.contentType));
            return isEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 29 * hash + (this.charSet != null ? this.charSet.hashCode() : 0);
        hash = 29 * hash + (this.bodyBytes != null ? this.bodyBytes.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@RequestBody[");
        sb.append(contentType).append(", ");
        sb.append(charSet).append(", ");
        sb.append(org.apache.commons.codec.binary.Base64.encodeBase64String(bodyBytes));
        sb.append("]");
        return sb.toString();
    }
}
