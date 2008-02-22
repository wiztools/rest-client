/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

/**
 *
 * @author rsubramanian
 */
public final class ReqResBean {
    
    private RequestBean requestBean;
    private ResponseBean responseBean;

    public RequestBean getRequestBean() {
        return requestBean;
    }

    public void setRequestBean(RequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ResponseBean getResponseBean() {
        return responseBean;
    }

    public void setResponseBean(ResponseBean responseBean) {
        this.responseBean = responseBean;
    }
    
}
