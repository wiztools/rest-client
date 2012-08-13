package org.wiztools.restclient.bean;

import java.nio.charset.Charset;

/**
 *
 * @author schandran
 */
public final class ReqEntityStringBean extends AbstractReqEntitySimpleBean implements ReqEntityString {
    
    private String body;
    
    public ReqEntityStringBean(String body, ContentType contentType){
        super(contentType);
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    @Override
    public Object clone(){
        ReqEntityStringBean cloned = new ReqEntityStringBean(body, contentType);
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
            isEqual = isEqual && (this.contentType == null? bean.contentType == null: this.contentType.equals(bean.contentType));
            return isEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 29 * hash + (this.body != null ? this.body.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@RequestBody[");
        sb.append(contentType).append(", ");
        sb.append(body);
        sb.append("]");
        return sb.toString();
    }
}
