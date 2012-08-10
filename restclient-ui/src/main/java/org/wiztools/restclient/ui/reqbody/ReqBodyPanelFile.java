package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.ReqEntity;
import org.wiztools.restclient.ReqEntityFile;
import org.wiztools.restclient.ReqEntityFileBean;
import org.wiztools.restclient.Util;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelFile extends JPanel implements ReqBodyOps {
    
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
        entity.setCharset(Charset.forName(Util.getCharsetFromContentType(
                jp_content_type_charset.getContentTypeCharsetString())));
        entity.setContentType(Util.getMimeFromContentType(
                jp_content_type_charset.getContentTypeCharsetString()));
        return entity;
    }

    @Override
    public void requestFocus() {
        jp_content_type_charset.requestFocus();
    }
}
