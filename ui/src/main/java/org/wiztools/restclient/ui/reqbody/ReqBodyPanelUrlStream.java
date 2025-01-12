package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JLabel;
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
        setLayout(new FlowLayout(FlowLayout.LEFT));

        jtf_url.setToolTipText("Contents of this URL will be set as request body");

        JPanel jp = new JPanel(new BorderLayout());
        JPanel jp_west = new JPanel(new GridLayout(2, 1));
        jp_west.add(new JLabel(" Content type: "));
        jp_west.add(new JLabel(" URL: "));
        jp.add(jp_west, BorderLayout.WEST);

        JPanel jp_center = new JPanel(new GridLayout(2, 1));
        jp_center.add(jp_content_type_charset.getComponent());
        jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_url));
        jp.add(jp_center, BorderLayout.CENTER);

        add(jp);
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
            URL url = org.wiztools.restclient.util.Url.get(jtf_url.getText());
            return new ReqEntityUrlStreamBean(
                    jp_content_type_charset.getContentType(), url);
        }
        catch(MalformedURLException ex) {
            throw new IllegalStateException("Body Stream URL is malformed!", ex);
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
