package org.wiztools.restclient;

import java.util.Arrays;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;

/**
 *
 * @author schandran
 */
public final class ResponseBean implements Response{

    private int statusCode;
    private String statusLine;
    private MultiValueMap<String, String> headers;
    private byte[] responseBody;
    private TestResult testResult;
    private long executionTime;

    @Override
    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    @Override
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return CollectionsUtil.unmodifiableMultiValueMap(headers);
    }

    /*public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }*/
    
    public void addHeader(final String key, final String value){
        this.headers.put(key, value);
    }

    @Override
    public ContentType getContentType() {
        if(headers != null)
            return HttpUtil.getContentType(headers);
        return null;
    }

    @Override
    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }
    
    @Override
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
        response.responseBody = responseBody;
        if(!headers.isEmpty()){
            for(String header: headers.keySet()){
                for(String value: headers.get(header)) {
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
            isEqual = isEqual && (this.responseBody == null? bean.getResponseBody() == null: Arrays.equals(this.responseBody, bean.getResponseBody()));
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
        hash = 53 * hash + (this.responseBody != null ? this.responseBody.hashCode() : 0);
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
