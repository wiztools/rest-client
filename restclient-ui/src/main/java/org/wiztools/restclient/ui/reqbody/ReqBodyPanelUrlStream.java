package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.bean.ReqEntityUrlStream;
import org.wiztools.restclient.bean.ReqEntityUrlStreamBean;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelUrlStream extends JPanel implements ReqBodyPanel {
    
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    @Inject ContentTypeCharsetComponent jp_content_type_charset;
    
    private JTextField jtf_url = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout());
        
        jtf_url.setToolTipText("Contents of this URL will be set as request body");
        
        add(UIUtil.getFlowLayoutPanelLeftAligned(jp_content_type_charset.getComponent()), BorderLayout.NORTH);
        add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_url), BorderLayout.CENTER);
    }

    @Override
    public void enableBody() {
        jp_content_type_charset.enableComponent();
        jtf_url.setEnabled(true);
    }

    @Override
    public void disableBody() {
        jp_content_type_charset.disableComponent();
        jtf_url.setEnabled(false);
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityUrlStream) {
            ReqEntityUrlStream e = (ReqEntityUrlStream) entity;
            URL url = e.getUrl();
            jtf_url.setText(url.toString());
        }
    }

    @Override
    public ReqEntity getEntity() {
        try {
            URL url = new URL(jtf_url.getText());
            return new ReqEntityUrlStreamBean(
                    jp_content_type_charset.getContentType(), url);
        }
        catch(MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        jp_content_type_charset.clear();
        jtf_url.setText("");
    }

    @Override
    public void requestFocus() {
        jp_content_type_charset.requestFocus();
    }
}
