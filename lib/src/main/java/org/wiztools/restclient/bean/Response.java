package org.wiztools.restclient.bean;

import java.io.Serializable;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author subwiz
 */
public interface Response extends Cloneable, Serializable {

    long getExecutionTime();

    MultiValueMap<String, String> getHeaders();
    
    ContentType getContentType();

    byte[] getResponseBody();

    int getStatusCode();

    String getStatusLine();

    TestResult getTestResult();

    Object clone();
}
