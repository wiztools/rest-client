package org.wiztools.restclient.ui.reqtls;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.TLSReq;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqTLSPanelImpl.class)
public interface ReqTLSPanel extends ViewPanel {

    TLSReq getSslReq();
    void setSslReq(TLSReq sslReq);

}
