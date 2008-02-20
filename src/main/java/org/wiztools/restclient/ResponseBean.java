package org.wiztools.restclient;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author schandran
 */
public final class ResponseBean {

    private int statusCode;
    private String statusLine;
    private Map<String, String> headers;
    private String responseBody;
    
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /*public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }*/
    
    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }
    
    public ResponseBean(){
        headers = new LinkedHashMap<String, String>();
    }
    
    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof ResponseBean){
            final ResponseBean bean = (ResponseBean)o;
            boolean isEqual = true;
            isEqual = isEqual && (this.statusCode == bean.statusCode);
            isEqual = isEqual && (this.statusLine == null? bean.statusLine == null: this.statusLine.equals(bean.statusLine));
            isEqual = isEqual && (this.headers == null? bean.headers == null: this.headers.equals(bean.headers));
            isEqual = isEqual && (this.responseBody == null? bean.responseBody == null: this.responseBody.equals(bean.responseBody));
            return isEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.statusCode;
        hash = 29 * hash + (this.statusLine != null ? this.statusLine.hashCode() : 0);
        hash = 29 * hash + (this.headers != null ? this.headers.hashCode() : 0);
        hash = 29 * hash + (this.responseBody != null ? this.responseBody.hashCode() : 0);
        return hash;
    }
}
