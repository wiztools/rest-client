/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

/**
 *
 * @author schandran
 */
public interface View {
    public void doStart(RequestBean request);
    public void doResponse(ResponseBean response);
    public void doEnd();
    public void doError(String error);
    public void doTestResult(String error);
}
