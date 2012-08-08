package org.wiztools.restclient.ui;

import org.wiztools.restclient.ReqEntity;

/**
 *
 * @author subwiz
 */
public interface ReqBodyOps {
    void enable();
    void disable();
    
    ReqEntity getEntity();
}
