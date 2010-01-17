package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author subwiz
 */
public interface Response extends Cloneable {

    long getExecutionTime();

    MultiValueMap<String, String> getHeaders();

    String getResponseBody() throws UnsupportedEncodingException;

    byte[] getResponseBodyBytes();

    int getStatusCode();

    String getStatusLine();

    TestResult getTestResult();

    Object clone();
}
