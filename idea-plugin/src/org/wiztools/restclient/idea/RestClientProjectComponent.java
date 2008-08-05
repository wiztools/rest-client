package org.wiztools.restclient.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import org.wiztools.restclient.ui.RESTMain;
import org.wiztools.restclient.ui.ScriptEditor;

import javax.swing.*;
import java.awt.*;


/**
 * project component for REST Client
 *
 * @author linux_china@hotmail.com
 */
public class RestClientProjectComponent implements ProjectComponent {
    public static final String TOOL_WINDOW_ID = "REST Client";
    private Project project;
    private static final Icon icon = IconLoader.getIcon("/resources/icons/web_logo.png");
    private RESTMain restMain;

    /**
     * construct project component
     *
     * @param project project object
     */
    public RestClientProjectComponent(Project project) {
        this.project = project;
    }

    /**
     * get rest client project component
     *
     * @param project project
     * @return project component
     */
    public static RestClientProjectComponent getInstance(Project project) {
        return project.getComponent(RestClientProjectComponent.class);
    }

    /**
     * get RESTMain class
     *
     * @return RESTMain class
     */
    public RESTMain getRestMain() {
        return restMain;
    }

    /**
     * initialize component
     */
    public void initComponent() {

    }

    /**
     * disponse component
     */
    public void disposeComponent() {

    }

    /**
     * get project component name
     *
     * @return component name
     */
    @NotNull
    public String getComponentName() {
        return "RestClientProjectComponent";
    }

    /**
     * fired when project opened
     */
    public void projectOpened() {
        JFrame jFrame = WindowManager.getInstance().getFrame(project);
        ScriptEditor scriptEditor=new GroovyScriptEditor(project);
        ScriptEditor responseViewer=new ResponseViewerScriptEditor(project);
        restMain = new RESTMain(jFrame,scriptEditor,responseViewer);
        registerRestClientToolWindow(project);
    }

    /**
     * fired when project closed
     */
    public void projectClosed() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
    }

    /**
     * register REST Client tool window
     *
     * @param project project object
     */
    public void registerRestClientToolWindow(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM);
        ContentFactory contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(constructPanel(project, restMain.getView()), "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setIcon(icon);
    }

    /**
     * construct tool window panel
     *
     * @param project  project object
     * @param restView RESTView
     * @return restview
     */
    private JPanel constructPanel(Project project, JPanel restView) {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        toolPanel.add(ActionManager.getInstance().createActionToolbar("RESTClient Menu Bar", (ActionGroup) ActionManager.getInstance()
                .getAction("RESTClient.MenuToolbar"), true).getComponent(),
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints
                        .SIZEPOLICY_CAN_SHRINK | GridConstraints
                        .SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 12), null));
        toolPanel.add(restView, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null,
                null, null));
        return toolPanel;
    }
}
