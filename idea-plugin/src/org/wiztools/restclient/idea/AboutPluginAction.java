package org.wiztools.restclient.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;

/**
 * about plugin action
 *
 * @author libing.chen@gmail.com
 */
public class AboutPluginAction extends AnAction {
    /**
     * display about dialog
     *
     * @param event action event
     */
    public void actionPerformed(AnActionEvent event) {
        RestClientProjectComponent clientProjectComponent = RestClientProjectComponent.getInstance(event.getData(DataKeys.PROJECT));
        clientProjectComponent.getRestMain().showAboutDialog();
    }
}
