package org.wiztools.restclient.ui.reqetc;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.CookieVersion;
import org.wiztools.restclient.bean.HTTPVersion;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqEtcPanelImpl.class)
public interface ReqEtcPanel extends ViewPanel {
    HTTPVersion getHttpVersion();
    void setHttpVersion(HTTPVersion version);

    CookieVersion getCookieVersion();
    void setCookieVersion(CookieVersion version);

    boolean isFollowRedirects();
    void setFollowRedirects(boolean b);

    boolean isIgnoreResponseBody();
    void setIgnoreResponseBody(boolean b);
}
