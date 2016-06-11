package org.wiztools.restclient.ui.reqetc;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.annotation.PostConstruct;
import javax.swing.*;
import org.wiztools.restclient.bean.CookieVersion;
import org.wiztools.restclient.bean.HTTPVersion;

/**
 *
 * @author subwiz
 */
public class ReqEtcPanelImpl extends JPanel implements ReqEtcPanel {

    // HTTP Version Combo box
    private final JComboBox<HTTPVersion> jcb_http_version =
            new JComboBox<>(HTTPVersion.values());

    // Cookie versions
    private final JComboBox<CookieVersion> jcb_cookie_version =
            new JComboBox<>(CookieVersion.values());

    // Follow redirect
    private final JCheckBox jcb_followRedirects = new JCheckBox("Follow HTTP Redirects? ");

    // Ignore body
    private final JCheckBox jcb_ignoreResponseBody = new JCheckBox("Ignore Response Body? ");

    @PostConstruct
    protected void init() {
        JPanel jp_etc = new JPanel(new GridLayout(3, 1));
        { // Http Version
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(new JLabel("Version: "));

            jp.add(jcb_http_version);
            jp.add(jcb_cookie_version);

            jp_etc.add(jp);
        }

        { // Follow Redirect
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            // Previous version of RESTClient had follow redirects as true.
            // To maintain backward compatibility in default behavior:
            jcb_followRedirects.setSelected(true);
            jcb_followRedirects.setBorder(null);
            jp.add(jcb_followRedirects);
            jp_etc.add(jp);
        }

        { // Ignore response body
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jcb_ignoreResponseBody.setBorder(null);
            jp.add(jcb_ignoreResponseBody);
            jp_etc.add(jp);
        }

        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(jp_etc);
    }

    @Override
    public void clear() {
        jcb_http_version.setSelectedItem(HTTPVersion.HTTP_1_1);
        jcb_cookie_version.setSelectedItem(CookieVersion.V_1);
        jcb_followRedirects.setSelected(true);
        jcb_ignoreResponseBody.setSelected(false);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CookieVersion getCookieVersion() {
        return (CookieVersion) jcb_cookie_version.getSelectedItem();
    }

    @Override
    public void setCookieVersion(CookieVersion version) {
        if(version != null) {
            jcb_cookie_version.setSelectedItem(version);
        }
    }

    @Override
    public HTTPVersion getHttpVersion() {
        return (HTTPVersion) jcb_http_version.getSelectedItem();
    }

    @Override
    public void setHttpVersion(HTTPVersion version) {
        jcb_http_version.setSelectedItem(version);
    }

    @Override
    public boolean isFollowRedirects() {
        return jcb_followRedirects.isSelected();
    }

    @Override
    public void setFollowRedirects(boolean b) {
        jcb_followRedirects.setSelected(b);
    }

    @Override
    public boolean isIgnoreResponseBody() {
        return jcb_ignoreResponseBody.isSelected();
    }

    @Override
    public void setIgnoreResponseBody(boolean b) {
        jcb_ignoreResponseBody.setSelected(b);
    }

}
