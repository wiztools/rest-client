package org.wiztools.restclient;

import java.util.Map;

/**
 *
 * @author subwiz
 */
public interface Response extends Cloneable {

    long getExecutionTime();

    Map<String, String> getHeaders();

    String getResponseBody();

    int getStatusCode();

    String getStatusLine();

    TestResult getTestResult();

    Object clone();
}
