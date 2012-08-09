package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.ReqEntity;
import org.wiztools.restclient.Util;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelFile extends JPanel implements ReqBodyOps {
    
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    private JTextField jtf_content_type_charset = new JTextField(20);
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private BodyContentTypeDialog jd_body_content_type;
    
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JTextField jtf_file = new JTextField(20);
    
    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        
        jtf_content_type_charset.setEditable(false);
        
        jd_body_content_type = new BodyContentTypeDialog(rest_ui.getFrame());
        jd_body_content_type.addContentTypeCharSetChangeListener(new ContentTypeCharSetChangeListener() {
            @Override
            public void changed(String contentType, String charSet) {
                final String formatted = Util.getFormattedContentType(contentType, charSet);
                jtf_content_type_charset.setText(formatted);
            }
        });
        
        jb_body_content_type.setToolTipText("Edit Content-type & Charset");
        jb_body_content_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                jd_body_content_type.setVisible(true);
            }
        });
        
        // North
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jtf_content_type_charset);
        jp_north.add(jb_body_content_type);
        
        add(jp_north, BorderLayout.NORTH);
        
        // Center
        JPanel jp_center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp_center.add(jtf_file);
        jp_center.add(jb_body_file);
        
        add(jp_center, BorderLayout.CENTER);
    }
    
    @Override
    public void enableBody() {
        jtf_content_type_charset.setEnabled(true);
        jtf_file.setEnabled(true);
        
        jb_body_content_type.setEnabled(true);
        jb_body_file.setEnabled(true);
    }
    
    @Override
    public void disableBody() {
        jtf_content_type_charset.setEnabled(false);
        jtf_file.setEnabled(false);
        
        jb_body_content_type.setEnabled(false);
        jb_body_file.setEnabled(false);
    }
    
    @Override
    public void clearBody() {
        jtf_content_type_charset.setText("");
        jtf_file.setText("");
    }
    
    @Override
    public ReqEntity getEntity() {
        return null;
    }
}
