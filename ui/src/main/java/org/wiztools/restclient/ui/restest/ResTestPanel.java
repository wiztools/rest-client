package org.wiztools.restclient.ui.restest;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.bean.TestResult;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ResTestPanelImpl.class)
public interface ResTestPanel extends ViewPanel {
    TestResult getTestResult();
    void setTestResult(TestResult result);
}
