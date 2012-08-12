package org.wiztools.restclient.ui.reqbody;

import org.wiztools.restclient.util.HttpUtil;
import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTextField;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.*;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelFile extends JPanel implements ReqBodyPanel {
    
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    @Inject ContentTypeCharsetComponent jp_content_type_charset;
    
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JTextField jtf_file = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    
    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jp_content_type_charset);
        
        add(jp_north, BorderLayout.NORTH);
        
        // Center
        jb_body_file.setToolTipText("Select file");
        jb_body_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selectFile();
            }
        });
        JPanel jp_center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_center.add(jtf_file);
        jp_center.add(jb_body_file);
        
        add(UIUtil.getFlowLayoutPanelLeftAligned(jp_center), BorderLayout.CENTER);
    }
    
    private void selectFile() {
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
        final String mime = FileUtil.getMimeType(f);
        if(!mime.equals("content/unknown")) {
            final String origContentType = jp_content_type_charset.getContentType();
            if(!mime.equals(origContentType)) {
                final int result = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                        "The content-type selected (" + origContentType + ") does NOT match\n"
                        + "the computed file mime type (" + mime + ")\n"
                        + "Do you want to update the content-type to `" + mime + "'?",
                        "Mime-type mismatch correction",
                        JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION) {
                    // Set content type
                    jp_content_type_charset.setContentType(mime);
                    
                    // Check if XML content type:
                    if(XMLUtil.XML_MIME.equals(mime)){
                        try{
                            String charset = XMLUtil.getDocumentCharset(f);
                            if(charset != null && !(charset.equals(jp_content_type_charset.getCharsetString()))) {
                                final int charsetYesNo = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                                        "Change charset to `" + charset + "'?",
                                        "Change charset?",
                                        JOptionPane.YES_NO_OPTION);
                                if(charsetYesNo == JOptionPane.YES_OPTION) {
                                    jp_content_type_charset.setCharset(Charset.forName(charset));
                                }
                            }
                        }
                        catch(IOException ex){
                            // Do nothing!
                        }
                        catch(XMLException ex){
                            // Do nothing!
                        }
                    }
                }
            }
        }
        jtf_file.setText(f.getAbsolutePath());
    }
    
    @Override
    public void enableBody() {
        jp_content_type_charset.enableComponent();
        jtf_file.setEnabled(true);
        jb_body_file.setEnabled(true);
    }
    
    @Override
    public void disableBody() {
        jp_content_type_charset.disableComponent();
        jtf_file.setEnabled(false);
        jb_body_file.setEnabled(false);
    }
    
    @Override
    public void clearBody() {
        jp_content_type_charset.clearComponent();
        jtf_file.setText("");
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityFile) {
            ReqEntityFile e = (ReqEntityFile) entity;
            jp_content_type_charset.setContentTypeCharset(
                    e.getContentType(), e.getCharset());
            File body = e.getBody();
            jtf_file.setText(body.getAbsolutePath());
        }
    }
    
    @Override
    public ReqEntity getEntity() {
        ReqEntityFileBean entity = new ReqEntityFileBean();
        entity.setBody(new File(jtf_file.getText()));
        entity.setCharset(Charset.forName(HttpUtil.getCharsetFromContentType(
                jp_content_type_charset.getContentTypeCharsetString())));
        entity.setContentType(HttpUtil.getMimeFromContentType(
                jp_content_type_charset.getContentTypeCharsetString()));
        return entity;
    }

    @Override
    public void requestFocus() {
        jp_content_type_charset.requestFocus();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
