package org.wiztools.restclient.ui.reqbody;

import org.wiztools.restclient.ui.reqbody.BodyContentTypeDialog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ReqEntity;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.ui.RCFileView;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.ScriptEditor;
import org.wiztools.restclient.ui.ScriptEditorFactory;
import org.wiztools.restclient.ui.UIUtil;
import org.wiztools.restclient.ui.reqbody.ContentTypeCharsetComponent;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelString extends JPanel implements ReqBodyOps {
    
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    @Inject private ContentTypeCharsetComponent jp_content_type_charset;
    
    private ParameterDialog jd_req_paramDialog;
    
    private ScriptEditor se_req_body;
    {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        final boolean enableSyntaxColoring = Boolean.valueOf(
                options.getProperty("request.body.syntax.color")==null?
                    "true": options.getProperty("request.body.syntax.color"));
        if(enableSyntaxColoring) {
            se_req_body = ScriptEditorFactory.getXMLScriptEditor();
        }
        else {
            se_req_body = ScriptEditorFactory.getTextAreaScriptEditor();
        }
    }
    
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jp_content_type_charset);
        jp_north.add(jb_body_file);
        jp_north.add(jb_body_params);
        
        add(jp_north, BorderLayout.NORTH);
    }

    @Override
    public void enableBody() {
        jp_content_type_charset.enableComponent();
    }

    @Override
    public void disableBody() {
        jp_content_type_charset.disableComponent();
    }

    @Override
    public void clearBody() {
        jp_content_type_charset.clearComponent();
    }

    @Override
    public ReqEntity getEntity() {
        return null;
    }
    
}
