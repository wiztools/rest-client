package org.wiztools.restclient.ui.reqbody;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.Util;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ContentTypeCharsetComponent extends JPanel {
    @Inject RESTUserInterface rest_ui;
    
    private static final String DEFAULT_CONTENT_CHARSET = "text/plain; charset=UTF-8";
    
    private JTextField jtf_content_type_charset = new JTextField(DEFAULT_CONTENT_CHARSET, 20);
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private BodyContentTypeDialog jd_body_content_type;
    
    @PostConstruct
    protected void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        jtf_content_type_charset.setEditable(false);
        add(jtf_content_type_charset);
        
        jd_body_content_type = new BodyContentTypeDialog(rest_ui.getFrame());
        jd_body_content_type.addContentTypeCharSetChangeListener(new ContentTypeCharsetChangeListener() {
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
        add(jb_body_content_type);
    }
    
    public String getContentTypeCharsetString() {
        return jtf_content_type_charset.getText();
    }
    
    public void enableComponent() {
        jtf_content_type_charset.setEnabled(true);
        jb_body_content_type.setEnabled(true);
    }
    
    public void disableComponent() {
        jtf_content_type_charset.setEnabled(false);
        jb_body_content_type.setEnabled(false);
    }
    
    public void clearComponent() {
        jtf_content_type_charset.setText(DEFAULT_CONTENT_CHARSET);
    }
}
