package org.wiztools.restclient;

import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author schandran
 */
public class RoResponseBean implements Response {
    
    private final int statusCode;
    private final String statusLine;
    private final MultiValueMap<String, String> headers;
    private final String responseBody;
    private long executionTime;

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusLine() {
        return statusLine;
    }
    
    public RoResponseBean(final Response response){
        executionTime = response.getExecutionTime();
        statusCode = response.getStatusCode();
        statusLine = response.getStatusLine();
        headers = response.getHeaders();
        responseBody = response.getResponseBody();
    }

    public TestResult getTestResult() {
        return null;
    }

    @Override
    public Object clone(){
        return null;
    }
}
