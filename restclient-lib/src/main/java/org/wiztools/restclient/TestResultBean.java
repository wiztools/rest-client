package org.wiztools.restclient;

import java.util.List;

/**
 *
 * @author NEWUSER
 */
class TestResultBean implements TestResult {
    private int runCount;
    private int failureCount;
    private int errorCount;
    private List<TestFailureResult> failures;
    private List<TestFailureResult> errors;
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

    public List<TestFailureResult> getErrors() {
        return errors;
    }

    public void setErrors(List<TestFailureResult> errors) {
        this.errors = errors;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<TestFailureResult> getFailures() {
        return failures;
    }

    public void setFailures(List<TestFailureResult> failures) {
        this.failures = failures;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }
}
