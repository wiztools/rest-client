package org.wiztools.restclient.ui.reqbody;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.Util;
import org.wiztools.restclient.ui.RCFileView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ContentTypeCharsetComponent extends JPanel {
    @Inject private BodyContentTypeDialog jd_body_content_type;
    
    private static final String DEFAULT_CONTENT_CHARSET = "text/plain; charset=UTF-8";
    
    public static final int TEXT_FIELD_LENGTH = 26;
    
    private JTextField jtf_content_type_charset = new JTextField(DEFAULT_CONTENT_CHARSET, TEXT_FIELD_LENGTH);
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    
    
    @PostConstruct
    protected void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        jtf_content_type_charset.setEditable(false);
        add(jtf_content_type_charset);
        
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

    @Override
    public void requestFocus() {
        jb_body_content_type.requestFocus();
    }
    
    public void setContentTypeCharset(String contentType, Charset charset) {
        jd_body_content_type.setContentType(contentType);
        jd_body_content_type.setCharset(charset);
        jtf_content_type_charset.setText(
                Util.getFormattedContentType(contentType, charset));
    }
    
    public void setContentType(String contentType) {
        jd_body_content_type.setContentType(contentType);
        String charset = jd_body_content_type.getCharsetString();
        jtf_content_type_charset.setText(
                Util.getFormattedContentType(contentType, charset));
    }
    
    public String getContentTypeCharsetString() {
        return jtf_content_type_charset.getText();
    }
    
    public String getContentType() {
        return Util.getMimeFromContentType(jtf_content_type_charset.getText());
    }
    
    public void setCharset(Charset charset) {
        jd_body_content_type.setCharset(charset);
        jtf_content_type_charset.setText(
                Util.getFormattedContentType(
                    jd_body_content_type.getContentType(), charset));
    }
    
    public Charset getCharset() {
        return jd_body_content_type.getCharset();
    }
    
    public String getCharsetString() {
        return jd_body_content_type.getCharsetString();
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
