package org.wiztools.restclient.ui.restest;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.TestResult;
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
