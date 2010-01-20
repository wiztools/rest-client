package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;

/**
 *
 * @author subwiz
 */
public final class ResponseBean implements Response{

    private int statusCode;
    private String statusLine;
    private MultiValueMap<String, String> headers;
    private byte[] responseBodyBytes;
    private TestResult testResult;
    private long executionTime;

    private String getCharset(){
        return Util.getCharsetFromHeader(headers);
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public MultiValueMap<String, String> getHeaders() {
        return CollectionsUtil.unmodifiableMultiValueMap(headers);
    }

    /*public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }*/
    
    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }

    public String getResponseBody() throws UnsupportedEncodingException{
        String charset = getCharset();
        if(charset == null){ // when charset is not set, use UTF-8
            charset = Charsets.UTF_8.name();
        }
        return new String(responseBodyBytes, charset);
    }

    public byte[] getResponseBodyBytes() {
        return responseBodyBytes;
    }

    public void setResponseBodyBytes(byte[] responseBody) {
        this.responseBodyBytes = responseBody;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }
    
    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }
    
    public ResponseBean(){
        headers = new MultiValueMapArrayList<String, String>();
    }
    
    @Override
    public Object clone(){
        ResponseBean response = new ResponseBean();
        response.executionTime = executionTime;
        response.statusCode = statusCode;
        response.statusLine = statusLine;
        response.responseBodyBytes = responseBodyBytes;
        if(headers.size() != 0){
            for(String header: headers.keySet()){
                for(String value: headers.get(header)){
                    response.addHeader(header, value);
                }
            }
        }
        return response;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o instanceof Response){
            final Response bean = (Response)o;
            boolean isEqual = true;
            // Do not check executionTime: because when constructing ResponseBean
            // from the UI, it is not possible to get this value:
            // isEqual = isEqual && (this.executionTime == bean.executionTime);
            isEqual = isEqual && (this.statusCode == bean.getStatusCode());
            isEqual = isEqual && (this.statusLine == null? bean.getStatusLine() == null: this.statusLine.equals(bean.getStatusLine()));
            isEqual = isEqual && (this.headers == null? bean.getHeaders() == null: this.headers.equals(bean.getHeaders()));
            isEqual = isEqual && (this.responseBodyBytes == null? bean.getResponseBodyBytes() == null: Arrays.equals(this.responseBodyBytes, bean.getResponseBodyBytes()));
            isEqual = isEqual && (this.testResult == null? bean.getTestResult() == null: this.testResult.equals(bean.getTestResult()));
            return isEqual;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        // hash = 53 * hash + (int)this.executionTime;
        hash = 53 * hash + this.statusCode;
        hash = 53 * hash + (this.statusLine != null ? this.statusLine.hashCode() : 0);
        hash = 53 * hash + (this.headers != null ? this.headers.hashCode() : 0);
        hash = 53 * hash + (this.responseBodyBytes != null ? this.responseBodyBytes.hashCode() : 0);
        hash = 53 * hash + (this.testResult != null ? this.testResult.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("@Response[");
        sb.append(statusLine);
        sb.append("]");
        return sb.toString();
    }
}
