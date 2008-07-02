/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import org.wiztools.restclient.RequestBean;
import org.wiztools.restclient.ResponseBean;

/**
 *
 * @author subwiz
 */
public interface ISessionView {
    public void add(RequestBean request, ResponseBean response);
    public void clear();
}
