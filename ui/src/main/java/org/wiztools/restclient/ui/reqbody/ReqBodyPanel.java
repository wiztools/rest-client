package org.wiztools.restclient.ui.reqbody;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqBodyPanelImpl.class)
public interface ReqBodyPanel extends ViewPanel {
    void enableBody();
    void disableBody();

    void setEntity(ReqEntity entity);
    ReqEntity getEntity();
}
