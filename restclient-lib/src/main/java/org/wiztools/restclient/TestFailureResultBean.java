package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
class TestFailureResultBean implements TestFailureResult {
    private int lineNumber;
    private String exceptionMessage;

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestFailureResult other = (TestFailureResult) obj;
        if (this.lineNumber != other.getLineNumber()) {
            return false;
        }
        if ((this.exceptionMessage == null) ? (other.getExceptionMessage() != null) : !this.exceptionMessage.equals(other.getExceptionMessage())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.lineNumber;
        hash = 97 * hash + (this.exceptionMessage != null ? this.exceptionMessage.hashCode() : 0);
        return hash;
    }
}
