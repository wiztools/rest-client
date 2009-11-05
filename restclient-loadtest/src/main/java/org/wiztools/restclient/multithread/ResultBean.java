package org.wiztools.restclient.multithread;

import java.io.File;
import org.wiztools.restclient.Response;

/**
 *
 * @author subwiz
 */
class ResultBean {
    private File file;
    private Response response;
    private String threadName;

    File getFile() {
        return file;
    }

    void setFile(File file) {
        this.file = file;
    }

    Response getResponse() {
        return response;
    }

    void setResponse(Response response) {
        this.response = response;
    }

    String getThreadName() {
        return threadName;
    }

    void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName()).append("|").append(response.getExecutionTime())
                .append(" seconds")
                .append("|")
                .append(threadName);
        return sb.toString();
    }

    
}
