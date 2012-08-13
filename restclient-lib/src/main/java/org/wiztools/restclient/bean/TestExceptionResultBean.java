package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class TestExceptionResultBean implements TestExceptionResult {
    private int lineNumber;
    private String exceptionMessage;

    @Override
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
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
        final TestExceptionResult other = (TestExceptionResult) obj;
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
