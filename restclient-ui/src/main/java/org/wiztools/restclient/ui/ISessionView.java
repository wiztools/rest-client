package org.wiztools.restclient.ui;

import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;

/**
 *
 * @author subwiz
 */
interface ISessionView {
    public void add(Request request, Response response);
    public void clear();
}
