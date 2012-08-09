package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.*;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
class ReqBodyPanelString extends JPanel implements ReqBodyOps {
    
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
        jb_body_file.setToolTipText("Load from file");
        jb_body_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                loadFile();
            }
        });
        jp_north.add(jb_body_file);
        jp_north.add(jb_body_params);
        
        add(jp_north, BorderLayout.NORTH);
    }
    
    private void loadFile() {
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_REQUEST_BODY);
        if(f == null){ // Pressed cancel?
            return;
        }
        if(!f.canRead()){
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    "File not readable: " + f.getAbsolutePath(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get text from file and set
        try{
            String body = FileUtil.getContentAsString(f, Charsets.UTF_8);
            se_req_body.setText(body);
            se_req_body.setCaretPosition(0);
        }
        catch(IOException ex){
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    "IO Error: " + ex.getMessage(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            // jd_body_content_type.setContentType(oldContentType);
            // jd_body_content_type.setCharSet(oldCharset);
        }
    }

    @Override
    public void enableBody() {
        jp_content_type_charset.enableComponent();
        jb_body_file.setEnabled(true);
        jb_body_params.setEnabled(true);
    }

    @Override
    public void disableBody() {
        jp_content_type_charset.disableComponent();
        jb_body_file.setEnabled(false);
        jb_body_params.setEnabled(false);
    }

    @Override
    public void clearBody() {
        jp_content_type_charset.clearComponent();
    }
    
    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityString) {
            // 
        }
    }

    @Override
    public ReqEntity getEntity() {
        String body = se_req_body.getText();
        String contentType = jp_content_type_charset.getContentType();
        Charset charset = jp_content_type_charset.getCharset();
        ReqEntityStringBean entity = new ReqEntityStringBean(
                body,
                contentType,
                charset);
        return entity;
    }
    
}
