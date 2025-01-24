package org.wiztools.restclient.ui.reqssl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;

import com.github.rjeschke.txtmark.Processor;
import org.wiztools.commons.StreamUtil;
import org.wiztools.restclient.bean.SSLHostnameVerifier;
import org.wiztools.restclient.bean.SSLKeyStore;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.bean.SSLReqBean;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ReqTLSPanelImpl extends JPanel implements ReqTLSPanel {
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;

    // SSL - general
    private final JCheckBox jcb_disable_hostname_verifier = new JCheckBox("Disable hostname verification? ");
    private final JCheckBox jcb_ssl_trust_self_signed_cert = new JCheckBox("Ignore cert errors (self-signed, expired, etc.)? ");

    // SSL - trust store
    @Inject private KeyStorePanel jp_truststore;

    // SSL - key store
    @Inject private KeyStorePanel jp_keystore;

    private SSLHostnameVerifier getSelectedHostnameVerifier() {
        return jcb_disable_hostname_verifier.isSelected()?
                SSLHostnameVerifier.ALLOW_ALL: SSLHostnameVerifier.STRICT;
    }

    @Override
    public SSLReq getSslReq() {
        SSLReqBean out = new SSLReqBean();

        out.setHostNameVerifier(getSelectedHostnameVerifier());
        out.setTrustAllCerts(jcb_ssl_trust_self_signed_cert.isSelected());

        SSLKeyStore trustStore = jp_truststore.getKeyStore();
        out.setTrustStore(trustStore);

        SSLKeyStore keyStore = jp_keystore.getKeyStore();
        out.setKeyStore(keyStore);

        return out;
    }

    @Override
    public void setSslReq(SSLReq sslReq) {
        // general tab:
        switch(sslReq.getHostNameVerifier()) {
            case ALLOW_ALL:
                jcb_disable_hostname_verifier.setSelected(true);
                break;
            default:
                jcb_disable_hostname_verifier.setSelected(false);
                break;
        }
        jcb_ssl_trust_self_signed_cert.setSelected(sslReq.isTrustAllCerts());

        // truststore / keystore tab:
        jp_truststore.setKeyStore(sslReq.getTrustStore());
        jp_keystore.setKeyStore(sslReq.getKeyStore());
    }

    @Override
    public void clear() {
        jcb_disable_hostname_verifier.setSelected(false);
        jcb_ssl_trust_self_signed_cert.setSelected(false);

        jp_truststore.clear();
        jp_keystore.clear();
    }

    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout(RESTView.BORDER_WIDTH, 2));

        JTabbedPane jtp_ssl = new JTabbedPane();

        { // SSL General:
            JPanel jpGrid = new JPanel(new GridLayout(2, 1));
            { // Trust self-signed cert:
                JPanel jp = new JPanel();
                jp.setLayout(new FlowLayout(FlowLayout.LEFT));
                jp.add(jcb_ssl_trust_self_signed_cert);
                jpGrid.add(jp);
            }
            { // Hostname verifier:
                JPanel jp = new JPanel();
                jp.setLayout(new FlowLayout(FlowLayout.LEFT));
                jp.add(jcb_disable_hostname_verifier);
                jpGrid.add(jp);
            }

            jtp_ssl.addTab("General", UIUtil.getFlowLayoutPanelLeftAligned(jpGrid));
        }

        { // Trust / Key store:
            jp_truststore.setLabel("Truststore:");
            jp_truststore.setTitle("Enter Truststore Details");

            jp_keystore.setLabel("Keystore:");
            jp_keystore.setTitle("Enter Keystore Details");

            JPanel jp_grid = new JPanel(new GridLayout(2, 1));
            jp_grid.add(jp_truststore);
            jp_grid.add(jp_keystore);

            jtp_ssl.addTab("Truststore / Keystore",
                    UIUtil.getFlowLayoutPanelLeftAligned(jp_grid));
        }

        { // Info:
            JPanel jp_info = new JPanel(new BorderLayout());
            InputStream is = ReqTLSPanelImpl.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/tls.md");
            try {
                String md = StreamUtil.inputStream2String(is, StandardCharsets.UTF_8);
                String data = Processor.process(md);
                JEditorPane jep = new JEditorPane();
                jep.setContentType("text/html");
                jep.setEditable(false);
                jep.setText(data);
                jep.setCaretPosition(0);
                JScrollPane jsp = new JScrollPane(jep);
                jsp.setPreferredSize(new Dimension(0, 0));
                jp_info.add(jsp, BorderLayout.CENTER);
            } catch(IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            jtp_ssl.addTab("Info", jp_info);
        }

        add(jtp_ssl);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
