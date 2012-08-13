package org.wiztools.restclient.bean;

/**
 *
 * @author rsubramanian
 */
public final class ReqResBean implements Cloneable{
    
    private Request requestBean;
    private Response responseBean;

    public Request getRequestBean() {
        return requestBean;
    }

    public void setRequestBean(Request requestBean) {
        this.requestBean = requestBean;
    }

    public Response getResponseBean() {
        return responseBean;
    }

    public void setResponseBean(Response responseBean) {
        this.responseBean = responseBean;
    }
    
    @Override
    public Object clone(){
        ReqResBean cloned = new ReqResBean();
        if(requestBean != null){
            Request clonedRequestBean = (Request)requestBean.clone();
            cloned.requestBean = clonedRequestBean;
        }
        if(responseBean != null){
            //Response clonedResponseBean = (Response)responseBean.clone();
            //cloned.responseBean = clonedResponseBean;
        }
        return cloned;
    }
}
