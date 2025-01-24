package org.wiztools.restclient.ui.reqssl;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqTLSPanelImpl.class)
public interface ReqTLSPanel extends ViewPanel {

    SSLReq getSslReq();
    void setSslReq(SSLReq sslReq);

}
