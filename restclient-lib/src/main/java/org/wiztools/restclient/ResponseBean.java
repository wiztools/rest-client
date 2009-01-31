package org.wiztools.restclient;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author schandran
 */
public final class ResponseBean implements Response{

    private int statusCode;
    private String statusLine;
    private Map<String, String> headers;
    private String responseBody;
    private TestResult testResult;
    private long executionTime;

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
    
    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }
    
    public ResponseBean(){
        headers = new LinkedHashMap<String, String>();
    }
    
    @Override
    public Object clone(){
        ResponseBean response = new ResponseBean();
        response.executionTime = executionTime;
        response.statusCode = statusCode;
        response.statusLine = statusLine;
        response.responseBody = responseBody;
        if(headers.size() != 0){
            for(String header: headers.keySet()){
                response.addHeader(header, headers.get(header));
            }
        }
        return response;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o instanceof ResponseBean){
            final ResponseBean bean = (ResponseBean)o;
            boolean isEqual = true;
            // Do not check executionTime: because when constructing ResponseBean
            // from the UI, it is not possible to get this value:
            // isEqual = isEqual && (this.executionTime == bean.executionTime);
            isEqual = isEqual && (this.statusCode == bean.statusCode);
            isEqual = isEqual && (this.statusLine == null? bean.statusLine == null: this.statusLine.equals(bean.statusLine));
            isEqual = isEqual && (this.headers == null? bean.headers == null: this.headers.equals(bean.headers));
            isEqual = isEqual && (this.responseBody == null? bean.responseBody == null: this.responseBody.equals(bean.responseBody));
            isEqual = isEqual && (this.testResult == null? bean.testResult == null: this.testResult.equals(bean.testResult));
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
