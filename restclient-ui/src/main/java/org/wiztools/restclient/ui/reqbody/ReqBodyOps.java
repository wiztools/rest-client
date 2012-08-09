package org.wiztools.restclient.ui.reqbody;

import org.wiztools.restclient.ReqEntity;

/**
 *
 * @author subwiz
 */
public interface ReqBodyOps {
    void enableBody();
    void disableBody();
    void clearBody();
    
    void setEntity(ReqEntity entity);
    ReqEntity getEntity();
}
