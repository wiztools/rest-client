package org.wiztools.restclient.ui.reqssl;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqSSLPanelImpl.class)
public interface ReqSSLPanel extends ViewPanel {

    SSLReq getSslReq();
    void setSslReq(SSLReq sslReq);
    
}
