/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.test;

import java.util.Map;
import org.wiztools.restclient.ResponseBean;

/**
 *
 * @author schandran
 */
public class RoResponseBean {
    
    private final int statusCode;
    private final String statusLine;
    private final Map<String, String> headers;
    private final String responseBody;

    public Map<String, String> getHeaders() {
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
    
    public RoResponseBean(final ResponseBean response){
        statusCode = response.getStatusCode();
        statusLine = response.getStatusLine();
        headers = response.getHeaders();
        responseBody = response.getResponseBody();
    }
}
