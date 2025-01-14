package org.wiztools.restclient.ui.reqbody;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ContentTypeBean;
import org.wiztools.restclient.ui.RCFileView;
import org.wiztools.restclient.ui.UIUtil;
import org.wiztools.restclient.util.HttpUtil;

/**
 *
 * @author subwiz
 */
public class ContentTypeCharsetComponentImpl extends JPanel implements ContentTypeCharsetComponent {
    @Inject private BodyContentTypeDialog jd;
    
    private static final String DEFAULT_CONTENT_CHARSET = HttpUtil.getFormattedContentType(
            BodyContentTypeDialog.DEFAULT_CONTENT_TYPE, BodyContentTypeDialog.DEFAULT_CHARSET);
    
    private final JTextField jtf_content_type_charset = new JTextField(DEFAULT_CONTENT_CHARSET, TEXT_FIELD_LENGTH);
    private final JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    
    
    @PostConstruct
    protected void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        jtf_content_type_charset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    jd.setVisible(true);
                }
            }
        });
        
        jtf_content_type_charset.setEditable(false);
        add(jtf_content_type_charset);
        
        jd.addContentTypeCharSetChangeListener(new ContentTypeCharsetChangeListener() {
            @Override
            public void changed(String contentType, String charset) {
                final String formatted = HttpUtil.getFormattedContentType(contentType, charset);
                jtf_content_type_charset.setText(formatted);
            }
        });
        
        jb_body_content_type.setToolTipText("Edit Content-type & Charset");
        jb_body_content_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                jd.setVisible(true);
            }
        });
        add(jb_body_content_type);
    }

    @Override
    public void requestFocus() {
        jb_body_content_type.requestFocus();
    }
    
    @Override
    public void setContentTypeCharset(ContentType contentType) {
        if(contentType != null) {
            setContentTypeCharset(contentType.getContentType(), contentType.getCharset());
        }
    }
    
    @Override
    public ContentType getContentType() {
        if(StringUtil.isNotEmpty(jd.getContentType())) {
            return new ContentTypeBean(jd.getContentType(), jd.getCharset());
        }
        else {
            return null;
        }
    }
    
    @Override
    public void setContentTypeCharset(String contentType, Charset charset) {
        jd.setContentType(contentType);
        if(charset != null) {
            jd.setCharset(charset);
        }
        jtf_content_type_charset.setText(
                HttpUtil.getFormattedContentType(contentType, charset));
    }
    
    public void setContentType(ContentType contentType) {
        setContentTypeCharset(contentType.getContentType(), contentType.getCharset());
    }
    
    @Override
    public void setContentType(String contentType) {
        jd.setContentType(contentType);
        String charset = jd.getCharsetString();
        jtf_content_type_charset.setText(
                HttpUtil.getFormattedContentType(contentType, charset));
    }
    
    @Override
    public String getContentTypeCharsetString() {
        return jtf_content_type_charset.getText();
    }
    
    @Override
    public String getContentTypeString() {
        return HttpUtil.getMimeFromContentType(jtf_content_type_charset.getText());
    }
    
    @Override
    public void setCharset(Charset charset) {
        jd.setCharset(charset);
        jtf_content_type_charset.setText(
                HttpUtil.getFormattedContentType(
                    jd.getContentType(), charset));
    }
    
    @Override
    public Charset getCharset() {
        return jd.getCharset();
    }
    
    @Override
    public String getCharsetString() {
        return jd.getCharsetString();
    }
    
    @Override
    public void enableComponent() {
        jtf_content_type_charset.setEnabled(true);
        jb_body_content_type.setEnabled(true);
    }
    
    @Override
    public void disableComponent() {
        jtf_content_type_charset.setEnabled(false);
        jb_body_content_type.setEnabled(false);
    }
    
    @Override
    public void clear() {
        jtf_content_type_charset.setText(DEFAULT_CONTENT_CHARSET);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
