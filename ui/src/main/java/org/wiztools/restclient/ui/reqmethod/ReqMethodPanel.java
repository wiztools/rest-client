package org.wiztools.restclient.ui.reqmethod;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.HTTPMethod;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqMethodPanelImpl.class)
public interface ReqMethodPanel extends ViewPanel {

    boolean doesSelectedMethodSupportEntityBody();

    HTTPMethod getSelectedMethod();

    void setSelectedMethod(HTTPMethod method);

}
