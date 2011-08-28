package org.wiztools.restclient;

import java.util.List;

/**
 *
 * @author subwiz
 */
public interface TestResult {

    int getErrorCount();

    List<TestExceptionResult> getErrors();

    int getFailureCount();

    List<TestExceptionResult> getFailures();

    String getMessage();

    int getRunCount();

}
