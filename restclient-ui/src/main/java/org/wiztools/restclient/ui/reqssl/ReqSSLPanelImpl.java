package org.wiztools.restclient.ui.reqssl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.bean.SSLHostnameVerifier;
import org.wiztools.restclient.bean.SSLKeyStore;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.bean.SSLReqBean;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ReqSSLPanelImpl extends JPanel implements ReqSSLPanel {
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    // SSL - general
    private final JComboBox<SSLHostnameVerifier> jcb_ssl_hostname_verifier = new JComboBox<>(SSLHostnameVerifier.getAll());
    private final JCheckBox jcb_ssl_trust_self_signed_cert = new JCheckBox("Trust self-signed certificate? ");
    
    // SSL - trust store
    @Inject private KeyStorePanel jp_truststore;
    
    // SSL - key store
    @Inject private KeyStorePanel jp_keystore;

    @Override
    public SSLReq getSslReq() {
        SSLReqBean out = new SSLReqBean();
        
        out.setHostNameVerifier((SSLHostnameVerifier) jcb_ssl_hostname_verifier.getSelectedItem());
        out.setTrustSelfSignedCert(jcb_ssl_trust_self_signed_cert.isSelected());
        
        SSLKeyStore trustStore = jp_truststore.getKeyStore();    
        out.setTrustStore(trustStore);
        
        SSLKeyStore keyStore = jp_keystore.getKeyStore();
        out.setKeyStore(keyStore);

        return out;
    }

    @Override
    public void setSslReq(SSLReq sslReq) {
        jcb_ssl_hostname_verifier.setSelectedItem(sslReq.getHostNameVerifier());
        jcb_ssl_trust_self_signed_cert.setSelected(sslReq.isTrustSelfSignedCert());
        
        { // key store:
            final SSLKeyStore keyStore = sslReq.getKeyStore();
            if(keyStore != null) {
                jp_keystore.setKeyStore(keyStore);
            }
        }
        
        { // trust store:
            final SSLKeyStore trustStore = sslReq.getTrustStore();
            if(trustStore != null) {
                jp_truststore.setKeyStore(trustStore);
            }
        }
    }
    
    @Override
    public void clear() {
        jcb_ssl_hostname_verifier.setSelectedItem(SSLHostnameVerifier.STRICT);
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
                jcb_ssl_trust_self_signed_cert.setHorizontalTextPosition(SwingConstants.LEFT);
                jp.add(jcb_ssl_trust_self_signed_cert);
                jpGrid.add(jp);
            }
            { // Hostname verifier:
                JPanel jp = new JPanel();
                jp.setLayout(new FlowLayout(FlowLayout.LEFT));
                jp.add(new JLabel(" Hostname verifier:"));
                jp.add(jcb_ssl_hostname_verifier);
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

        add(jtp_ssl);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
