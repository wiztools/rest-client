package org.wiztools.restclient.test;

import java.util.List;

/**
 *
 * @author NEWUSER
 */
public class TestResultBean {
    private int runCount;
    private int failureCount;
    private int errorCount;
    private List<TestFailureResultBean> failures;
    private List<TestFailureResultBean> errors;
    private String message;
    
    public String getMessage(){
        return message;
    }
    
    public void setMessage(String message){
        this.message = message;
    }
    
    @Override
    public String toString(){
        return message;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<TestFailureResultBean> getErrors() {
        return errors;
    }

    public void setErrors(List<TestFailureResultBean> errors) {
        this.errors = errors;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<TestFailureResultBean> getFailures() {
        return failures;
    }

    public void setFailures(List<TestFailureResultBean> failures) {
        this.failures = failures;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }
}
