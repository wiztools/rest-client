package org.wiztools.restclient.bean;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author subwiz
 */
public interface TestResult extends Serializable {

    int getErrorCount();

    List<TestExceptionResult> getErrors();

    int getFailureCount();

    List<TestExceptionResult> getFailures();

    String getMessage();

    int getRunCount();

}
