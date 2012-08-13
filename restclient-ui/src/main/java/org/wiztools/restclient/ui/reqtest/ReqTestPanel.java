package org.wiztools.restclient.ui.reqtest;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqTestPanelImpl.class)
public interface ReqTestPanel extends ViewPanel {
    String getTestScript();
    void setTestScript(String script);
    void runClonedRequestTest(Request request, Response response);
}
