package org.wiztools.restclient.ui;

import org.wiztools.restclient.Request;
import org.wiztools.restclient.Response;

/**
 *
 * @author subwiz
 */
public interface ISessionView {
    public void add(Request request, Response response);
    public void clear();
}
