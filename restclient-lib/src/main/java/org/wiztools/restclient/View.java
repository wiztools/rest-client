package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public interface View {
    public void doStart(Request request);
    public void doResponse(Response response);
    public void doCancelled();
    public void doEnd();
    public void doError(String error);
}
