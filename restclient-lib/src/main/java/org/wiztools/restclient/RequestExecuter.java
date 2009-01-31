package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public interface RequestExecuter {
    void execute(Request request, View ... views);
    void abortExecution();
}
