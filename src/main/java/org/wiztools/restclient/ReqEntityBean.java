package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public final class ReqEntityBean implements Cloneable{
    
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
    
    public String getContentTypeCharsetFormatted(){
        return Util.getFormattedContentType(contentType, charSet);
    }
    
    @Override
    public Object clone(){
        ReqEntityBean cloned = new ReqEntityBean(body, contentType, charSet);
        return cloned;
    }
    
    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof ReqEntityBean){
            ReqEntityBean bean = (ReqEntityBean)o;
            boolean isEqual = true;
            isEqual = isEqual && (this.body == null? bean.body == null: this.body.equals(bean.body));
            isEqual = isEqual && (this.charSet == null? bean.charSet == null: this.charSet.equals(bean.charSet));
            isEqual = isEqual && (this.contentType == null? bean.contentType == null: this.contentType.equals(bean.contentType));
            return isEqual;
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 29 * hash + (this.charSet != null ? this.charSet.hashCode() : 0);
        hash = 29 * hash + (this.body != null ? this.body.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@RequestBody[");
        sb.append(contentType).append(", ");
        sb.append(charSet).append(", ");
        sb.append(body);
        sb.append("]");
        return sb.toString();
    }
}
