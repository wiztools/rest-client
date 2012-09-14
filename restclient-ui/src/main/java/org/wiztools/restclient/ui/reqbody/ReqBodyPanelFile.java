package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.bean.ReqEntityFile;
import org.wiztools.restclient.bean.ReqEntityFileBean;
import org.wiztools.restclient.ui.FileChooserType;
import org.wiztools.restclient.ui.RCFileView;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelFile extends JPanel implements ReqBodyPanel {
    
    @Inject
    private RESTUserInterface rest_ui;
    
    @Inject
    private ContentTypeCharsetComponent jp_content_type_charset;
    
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JTextField jtf_file = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    
    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jp_content_type_charset.getComponent());
        
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
        ContentTypeSelectorOnFile.select(jp_content_type_charset, f, rest_ui.getFrame());
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
    public void clear() {
        jp_content_type_charset.clear();
        jtf_file.setText("");
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityFile) {
            ReqEntityFile e = (ReqEntityFile) entity;
            jp_content_type_charset.setContentTypeCharset(e.getContentType());
            File body = e.getBody();
            jtf_file.setText(body.getAbsolutePath());
        }
    }
    
    @Override
    public ReqEntity getEntity() {
        File file = new File(jtf_file.getText());
        
        ReqEntityFileBean entity = new ReqEntityFileBean(file,
                jp_content_type_charset.getContentType());
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
