package org.wiztools.restclient;

/**
 *
 * @author rsubramanian
 */
public final class ReqResBean implements Cloneable{
    
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
    
    @Override
    public Object clone(){
        ReqResBean cloned = new ReqResBean();
        if(requestBean != null){
            RequestBean clonedRequestBean = (RequestBean)requestBean.clone();
            cloned.requestBean = clonedRequestBean;
        }
        if(responseBean != null){
            ResponseBean clonedResponseBean = (ResponseBean)responseBean.clone();
            cloned.responseBean = clonedResponseBean;
        }
        return cloned;
    }
}
