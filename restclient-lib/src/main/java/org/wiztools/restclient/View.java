package org.wiztools.restclient;

import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.Request;

/**
 * This is the callback interface for RequestExecuter. An implementation
 * of this interface needs to be passed to RequestExecuter (actually, a number
 * of View implementation could be passed to RequestExecuter--it is an vararg
 * parameter). The callback methods will be called during various stages in
 * request processing.
 * @author schandran
 */
public interface View {
    /**
     * This is called just before starting the request processing.
     * @param request The same request object passed to RequestExecuter.
     */
    public void doStart(Request request);

    /**
     * When the request processing is completed, the response object is
     * received by this method.
     * @param response
     */
    public void doResponse(Response response);

    /**
     * This callback is called when the request is aborted during its progress
     * by the call of RequestExecuter.abortExecution().
     */
    public void doCancelled();

    /**
     * When the request has completed, this method is called.
     */
    public void doEnd();

    /**
     * Whenever an error is encountered, this is called. This usually is the
     * error trace.
     * @param error
     */
    public void doError(String error);
}
