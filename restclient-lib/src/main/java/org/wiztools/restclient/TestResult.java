/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.util.List;

/**
 *
 * @author subwiz
 */
public interface TestResult {

    int getErrorCount();

    List<TestFailureResult> getErrors();

    int getFailureCount();

    List<TestFailureResult> getFailures();

    String getMessage();

    int getRunCount();

}
