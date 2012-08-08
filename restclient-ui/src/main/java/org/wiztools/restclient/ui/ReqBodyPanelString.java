package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.*;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelString extends JPanel implements ReqBodyOps {
    
    @Inject RESTUserInterface rest_ui;
    @Inject RESTView view;
    
    private ParameterDialog jd_req_paramDialog;
    
    private JTextField jtf_body_content_type = new JTextField();
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
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    private BodyContentTypeDialog jd_body_content_type;
    
    private JPanel jp_body = this;

    public ReqBodyPanelString() {
        // Initialize parameter dialog
        ParameterView pv = new ParameterView(){
            @Override
            public void setParameter(final String params) {
                se_req_body.setText(params);
            }
            
        };
        jd_req_paramDialog = new ParameterDialog(rest_ui, pv);
        
        
        JPanel jp_body_north = new JPanel();
        jp_body_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        jtf_body_content_type.setEditable(false);
        jtf_body_content_type.setColumns(24);
        jtf_body_content_type.setToolTipText("Selected Content-type & Charset");
        jtf_body_content_type.setText(Util.getFormattedContentType(
                jd_body_content_type.getContentType(),
                jd_body_content_type.getCharSet()));
        jp_body_north.add(jtf_body_content_type);
    
        jb_body_content_type.setToolTipText("Edit Content-type & Charset");
        jb_body_content_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jd_body_content_type.setVisible(true);
            }
        });
        jp_body_north.add(jb_body_content_type);
        
        JSeparator js = new JSeparator(SwingConstants.VERTICAL);
        jp_body_north.add(js);
        
        jb_body_file.setToolTipText("Load from file");
        jb_body_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_body_fileActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_file);
        
        jb_body_params.setToolTipText("Insert parameters");
        jb_body_params.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_body_paramActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_params);
        
        // Popup menu for body content tab
        final JPopupMenu jpm_req_body = new JPopupMenu();
        
        JMenu jm_syntax = new JMenu("Syntax Color");
        JMenuItem jmi_syntax_none = new JMenuItem("None");
        jmi_syntax_none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        JMenuItem jmi_syntax_xml = new JMenuItem("XML");
        jmi_syntax_xml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        
        jpm_req_body.add(jm_syntax);
        
        if (se_req_body.getEditorComponent() instanceof JEditorPane) {
            se_req_body.getEditorComponent().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                private void showPopup(final MouseEvent e) {
                    if(!se_req_body.getEditorComponent().isEnabled()){
                        // do not show popup menu when component is disabled:
                        return;
                    }
                    if (e.isPopupTrigger()) {
                        jpm_req_body.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        
        jp_body.add(jp_body_north, BorderLayout.NORTH);
        JPanel jp_body_center = new JPanel();
        jp_body_center.setLayout(new GridLayout(1, 1));
        jp_body_center.add(se_req_body.getEditorView());
        jp_body.add(jp_body_center, BorderLayout.CENTER);
    }
    
    private void actionTextEditorSyntaxChange(final ScriptEditor editor, final TextEditorSyntax syntax){
        ((JSyntaxPaneScriptEditor)editor).setSyntax(syntax);
    }
    
    private void jb_body_fileActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
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
        // Determine the MIME type and set parameter
        String contentType = FileUtil.getMimeType(f);
        String charset = null;
        if(XMLUtil.XML_MIME.equals(contentType)){
            try{
                charset = XMLUtil.getDocumentCharset(f);
            }
            catch(IOException ex){
                // Do nothing!
            }
            catch(XMLException ex){
                // Do nothing!
            } 
        }
        String oldContentType = jd_body_content_type.getContentType();
        String oldCharset = jd_body_content_type.getCharSet();
        if(!oldContentType.equals(contentType)){
            int contentTypeYesNo = JOptionPane.showConfirmDialog(view,
                    "Change ContentType To: " + contentType + "?",
                    "Change ContentType?", JOptionPane.YES_NO_OPTION);
            if(contentTypeYesNo == JOptionPane.YES_OPTION){
                jd_body_content_type.setContentType(contentType);
                if(charset != null){ // is XML file
                    jd_body_content_type.setCharSet(charset);
                }
            }
        }
        // Only the charset has changed:
        else if((charset != null) && (!oldCharset.equals(charset))){
            int charsetYesNo = JOptionPane.showConfirmDialog(view,
                    "Change Charset To: " + charset + "?",
                    "Change Charset?", JOptionPane.YES_NO_OPTION);
            if(charsetYesNo == JOptionPane.YES_OPTION){
                jd_body_content_type.setCharSet(charset);
            }
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
            jd_body_content_type.setContentType(oldContentType);
            jd_body_content_type.setCharSet(oldCharset);
        }
    }
    
    private void jb_body_paramActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        checkAndSetParameterContentType();
        jd_req_paramDialog.setLocationRelativeTo(rest_ui.getFrame());
        jd_req_paramDialog.setVisible(true);
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
        if(!jd_body_content_type.getContentType().equals(BodyContentTypeDialog.PARAM_CONTENT_TYPE)
                || !jd_body_content_type.getCharSet().equals(BodyContentTypeDialog.PARAM_CHARSET)){
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
                jd_body_content_type.setContentType(BodyContentTypeDialog.PARAM_CONTENT_TYPE);
                jd_body_content_type.setCharSet(BodyContentTypeDialog.PARAM_CHARSET);
            }
        }
    }
    
    private void setUIReqBodyEnabled(final boolean boo){
        se_req_body.getEditorComponent().setEnabled(boo);
        jb_body_content_type.setEnabled(boo);
        jb_body_file.setEnabled(boo);
        jb_body_params.setEnabled(boo);
    }
    
    @Override
    public void enable() {
        setUIReqBodyEnabled(true);
    }
    
    @Override
    public void disable() {
        setUIReqBodyEnabled(false);
    }
    
    @Override
    public ReqEntity getEntity() {
        return null;
    }
}
