package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author schandran
 */
public class RoResponseBean implements Response {
    
    private final int statusCode;
    private final String statusLine;
    private final MultiValueMap<String, String> headers;
    private final byte[] responseBodyBytes;
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

    public String getResponseBody() throws UnsupportedEncodingException {
        return new String(responseBodyBytes, Util.getCharsetFromHeader(headers));
    }

    public byte[] getResponseBodyBytes(){
        return responseBodyBytes;
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
        responseBodyBytes = response.getResponseBodyBytes();
    }

    public TestResult getTestResult() {
        return null;
    }

    @Override
    public Object clone(){
        return null;
    }
}
