package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.bean.ReqEntityString;
import org.wiztools.restclient.bean.ReqEntityStringBean;
import org.wiztools.restclient.ui.*;
import org.wiztools.restclient.ui.component.BodyPopupMenu;
import org.wiztools.restclient.ui.component.BodyPopupMenuListener;
import org.wiztools.restclient.ui.dnd.FileDropTargetListener;

/**
 *
 * @author subwiz
 */
class ReqBodyPanelString extends JPanel implements ReqBodyPanel, FontableEditor {
    
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    @Inject private ContentTypeCharsetComponent jp_content_type_charset;
    @Inject private ParameterDialog jd_req_paramDialog;
    
    private final ScriptEditor se_req_body = ScriptEditorFactory.getXMLScriptEditor();
    
    private final JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private final JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    
    @PostConstruct
    protected void init() {
        // Parameter dialog initialization
        jd_req_paramDialog.addParameterGenerationListener((String params) -> {
            se_req_body.setText(params);
        });
        
        // Layout
        setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jp_content_type_charset.getComponent());
        jb_body_file.setToolTipText("Load from file");
        jb_body_file.addActionListener((ActionEvent ae) -> {
            loadFile();
        });
        jp_north.add(jb_body_file);
        
        jb_body_params.setToolTipText("Insert parameters");
        jb_body_params.addActionListener((ActionEvent ae) -> {
            if(canSetReqBodyText()) {
                checkAndSetParameterContentType();
                jd_req_paramDialog.setLocationRelativeTo(rest_ui.getFrame());
                jd_req_paramDialog.setVisible(true);
            }
        });
        jp_north.add(jb_body_params);
        
        add(jp_north, BorderLayout.NORTH);
        
        // Center
        // Popup menu for body content tab
        BodyPopupMenuListener listener = new BodyPopupMenuListener() {

            @Override
            public void onSuccess(String msg) {
                view.setStatusMessage(msg);
            }

            @Override
            public void onFailure(String msg) {
                view.setStatusMessage(msg);
            }

            @Override
            public void onMessage(String msg) {
                view.setStatusMessage(msg);
            }
        };
        final BodyPopupMenu bpm = new BodyPopupMenu(se_req_body, listener, false);
        se_req_body.setPopupMenu(bpm);
        
        /*
         * Following code is written becuase of what seems to be a bug in
         * RSyntaxTextArea module: what happens is every time the popup
         * is displayed, the control is disabled by default. The following
         * code enables it.
         */
        se_req_body.getEditorComponent().addMouseListener(new MouseAdapter() {
            private void eEnable() {
                if(se_req_body.getEditorComponent().isEnabled()) {
                    Component[] components = bpm.getComponents();
                    for(Component c: components) {
                        if(!c.isEnabled()) {
                            c.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                eEnable();
            }
        });
        
        add(se_req_body.getEditorView(), BorderLayout.CENTER);
        
        // DnD:
        FileDropTargetListener l = new FileDropTargetListener();
        l.addDndAction((List<File> files) -> {
            loadFile(files.get(0));
        });
        new DropTarget(jb_body_file, l);
        new DropTarget(se_req_body.getEditorView(), l);
    }
    
    private void loadFile() {
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_REQUEST_BODY);
        loadFile(f);
    }
    
    private void loadFile(File f) {
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
        if(!BodyContentTypeDialog.PARAM_CONTENT_TYPE.equals(jp_content_type_charset.getContentType())){
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
                            BodyContentTypeDialog.PARAM_CONTENT_TYPE);
            }
        }
    }

    @Override
    public void enableBody() {
        jp_content_type_charset.enableComponent();
        jb_body_file.setEnabled(true);
        jb_body_params.setEnabled(true);
        se_req_body.setEnabled(true);
    }

    @Override
    public void disableBody() {
        jp_content_type_charset.disableComponent();
        jb_body_file.setEnabled(false);
        jb_body_params.setEnabled(false);
        se_req_body.setEnabled(false);
    }

    @Override
    public void clear() {
        jp_content_type_charset.clear();
    }
    
    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityString) {
            ReqEntityString bean = (ReqEntityString) entity;
            jp_content_type_charset.setContentTypeCharset(bean.getContentType());
            se_req_body.setText(bean.getBody());
        }
    }

    @Override
    public ReqEntity getEntity() {
        String body = se_req_body.getText();
        ReqEntityStringBean entity = new ReqEntityStringBean(
                body,
                jp_content_type_charset.getContentType());
        return entity;
    }

    @Override
    public void requestFocus() {
        se_req_body.getEditorComponent().requestFocus();
    }
    
    @Override
    public void setEditorFont(Font font) {
        se_req_body.getEditorComponent().setFont(font);
    }

    @Override
    public Font getEditorFont() {
        return se_req_body.getEditorComponent().getFont();
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
}
