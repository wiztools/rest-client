package org.wiztools.restclient.bean;

import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public class TestResultBean implements TestResult {

    private static final Logger LOG = Logger.getLogger(TestResultBean.class.getName());

    private int runCount;
    private int failureCount;
    private int errorCount;
    private List<TestExceptionResult> failures;
    private List<TestExceptionResult> errors;

    private String message;
    
    @Override
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

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    @Override
    public List<TestExceptionResult> getErrors() {
        return errors;
    }

    public void setErrors(List<TestExceptionResult> errors) {
        this.errors = errors;
    }

    @Override
    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    @Override
    public List<TestExceptionResult> getFailures() {
        return failures;
    }

    public void setFailures(List<TestExceptionResult> failures) {
        this.failures = failures;
    }

    @Override
    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestResult other = (TestResult) obj;
        if (this.runCount != other.getRunCount()) {
            return false;
        }
        if (this.failureCount != other.getFailureCount()) {
            return false;
        }
        if (this.errorCount != other.getErrorCount()) {
            return false;
        }
        if (this.failures != other.getFailures() && (this.failures == null || !this.failures.equals(other.getFailures()))) {
            return false;
        }
        if (this.errors != other.getErrors() && (this.errors == null || !this.errors.equals(other.getErrors()))) {
            return false;
        }
        if ((this.message == null) ? (other.getMessage() != null) : !this.message.equals(other.getMessage())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.runCount;
        hash = 59 * hash + this.failureCount;
        hash = 59 * hash + this.errorCount;
        hash = 59 * hash + (this.failures != null ? this.failures.hashCode() : 0);
        hash = 59 * hash + (this.errors != null ? this.errors.hashCode() : 0);
        hash = 59 * hash + (this.message != null ? this.message.hashCode() : 0);
        return hash;
    }
}
