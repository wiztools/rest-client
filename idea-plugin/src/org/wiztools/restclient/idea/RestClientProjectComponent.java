package org.wiztools.restclient.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import org.jetbrains.annotations.NotNull;
import org.wiztools.restclient.ui.RESTMain;



/**
 * project component for REST Client
 *
 * @author linux_china@hotmail.com
 */
public class RestClientProjectComponent implements ProjectComponent {
    public static final String TOOL_WINDOW_ID = "REST Client";
    private Project project;
    private static final Icon icon = IconLoader.getIcon("/resources/icons/web_logo.png");
    
    /**
     * construct project component
     *
     * @param project project object
     */
    public RestClientProjectComponent(Project project) {
        this.project = project;
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
        JFrame jFrame = WindowManager.getInstance().getFrame(project);
        RESTMain restMain = new RESTMain(jFrame);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM);
        ContentFactory contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(restMain.getView(), "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setIcon(icon);
    }
}
