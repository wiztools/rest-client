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
import org.wiztools.commons.StringUtil;
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
    @Inject private ParameterDialog jd_req_paramDialog;
    
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
        // Parameter dialog initialization
        jd_req_paramDialog.addParameterGenerationListener(new ParameterGenerationListener() {
            @Override
            public void onParameterGeneration(String params) {
                se_req_body.setText(params);
            }
        });
        
        // Layout
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
        
        jb_body_params.setToolTipText("Insert parameters");
        jb_body_params.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(canSetReqBodyText()) {
                    checkAndSetParameterContentType();
                    jd_req_paramDialog.setLocationRelativeTo(rest_ui.getFrame());
                    jd_req_paramDialog.setVisible(true);
                }
            }
        });
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
    
    private boolean canSetReqBodyText(){
        if(StringUtil.isEmpty(se_req_body.getText())){
            return true;
        }
        else{
            int response = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                    "Body text exists. Erase?",
                    "Erase?",
                    JOptionPane.YES_NO_OPTION);
            if(response == JOptionPane.YES_OPTION){
                return true;
            }
        }
        return false;
    }
    
    private void checkAndSetParameterContentType(){
        if(!jp_content_type_charset.getContentType().equals(BodyContentTypeDialog.PARAM_CONTENT_TYPE)
                || !jp_content_type_charset.getCharset().equals(BodyContentTypeDialog.PARAM_CHARSET)){
            int status = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                    "<html>For parameter the Content-type and Charset needs <br>" +
                    "to be `" + BodyContentTypeDialog.PARAM_CONTENT_TYPE +
                    "' and `"+
                    BodyContentTypeDialog.PARAM_CHARSET +
                    "' respectively.<br>"+
                    "Do you want to set this option?</html>",
                    "Parameter Content-type and Charset",
                    JOptionPane.YES_NO_OPTION);
            if(status == JOptionPane.YES_OPTION){
                jp_content_type_charset.setContentTypeCharset(                        
                            BodyContentTypeDialog.PARAM_CONTENT_TYPE,
                            BodyContentTypeDialog.PARAM_CHARSET);
            }
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
