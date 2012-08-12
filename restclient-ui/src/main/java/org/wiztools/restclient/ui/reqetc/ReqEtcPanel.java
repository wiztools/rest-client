package org.wiztools.restclient.ui.reqetc;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.HTTPVersion;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqEtcPanelImpl.class)
public interface ReqEtcPanel extends ViewPanel {
    HTTPVersion getHttpVersion();
    void setHttpVersion(HTTPVersion version);
    
    boolean isFollowRedirects();
    void setFollowRedirects(boolean b);
    
    boolean isIgnoreResponseBody();
    void setIgnoreResponseBody(boolean b);
}
