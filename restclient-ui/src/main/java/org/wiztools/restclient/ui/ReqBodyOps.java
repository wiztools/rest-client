package org.wiztools.restclient.ui;

import org.wiztools.restclient.ReqEntity;

/**
 *
 * @author subwiz
 */
public interface ReqBodyOps {
    void enableBody();
    void disableBody();
    void clear();
    
    ReqEntity getEntity();
}
