package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * @author subwiz
 */
public interface Response extends Cloneable {

    long getExecutionTime();

    Map<String, String> getHeaders();

    String getResponseBody() throws UnsupportedEncodingException;

    byte[] getResponseBodyBytes();

    int getStatusCode();

    String getStatusLine();

    TestResult getTestResult();

    Object clone();
}
