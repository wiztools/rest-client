package org.wiztools.restclient;

import java.nio.charset.Charset;

/**
 *
 * @author schandran
 */
public final class ReqEntityStringBean implements ReqEntityString {
    
    private String contentType;
    private Charset charset;
    private String body;
    
    public ReqEntityStringBean(String body, String contentType, Charset charset){
        this.body = body;
        this.contentType = contentType;
        this.charset = charset;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String getContentTypeCharsetFormatted(){
        return HttpUtil.getFormattedContentType(contentType, charset);
    }
    
    @Override
    public Object clone(){
        ReqEntityStringBean cloned = new ReqEntityStringBean(body, contentType, charset);
        return cloned;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o instanceof ReqEntityStringBean){
            ReqEntityStringBean bean = (ReqEntityStringBean)o;
            boolean isEqual = true;
            isEqual = isEqual && (this.body == null? bean.body == null: this.body.equals(bean.body));
            isEqual = isEqual && (this.charset == null? bean.charset == null: this.charset.equals(bean.charset));
            isEqual = isEqual && (this.contentType == null? bean.contentType == null: this.contentType.equals(bean.contentType));
            return isEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 29 * hash + (this.charset != null ? this.charset.hashCode() : 0);
        hash = 29 * hash + (this.body != null ? this.body.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@RequestBody[");
        sb.append(contentType).append(", ");
        sb.append(charset).append(", ");
        sb.append(body);
        sb.append("]");
        return sb.toString();
    }
}
