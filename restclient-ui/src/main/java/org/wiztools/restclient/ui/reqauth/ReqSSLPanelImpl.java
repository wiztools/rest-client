package org.wiztools.restclient.ui.reqauth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.bean.SSLHostnameVerifier;
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
    
    private static final int auth_text_size = 20;
    
    // SSL - general
    private JComboBox jcb_ssl_hostname_verifier = new JComboBox(SSLHostnameVerifier.getAll());
    private JCheckBox jcb_ssl_trust_self_signed_cert = new JCheckBox("Trust self-signed certificate? ");
    
    // SSL - trust store
    private JTextField jtf_ssl_truststore_file = new JTextField(auth_text_size);
    private JButton jb_ssl_browse = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JPasswordField jpf_ssl_truststore_pwd = new JPasswordField(auth_text_size);
    
    // SSL - key store
    private JTextField jtf_ssl_keystore_file = new JTextField(auth_text_size);
    private JButton jb_ssl_keystore_browse = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JPasswordField jpf_ssl_keystore_pwd = new JPasswordField(auth_text_size);

    @Override
    public SSLReq getSslReq() {
        SSLReqBean out = new SSLReqBean();
        
        out.setHostNameVerifier((SSLHostnameVerifier) jcb_ssl_hostname_verifier.getSelectedItem());
        out.setTrustSelfSignedCert(jcb_ssl_trust_self_signed_cert.isSelected());
        
        out.setKeyStore(jtf_ssl_keystore_file.getText());
        out.setKeyStorePassword(jpf_ssl_keystore_pwd.getPassword());
        
        out.setTrustStore(jtf_ssl_truststore_file.getText());
        out.setTrustStorePassword(jpf_ssl_truststore_pwd.getPassword());
        
        return out;
    }

    @Override
    public void setSslReq(SSLReq sslReq) {
        jcb_ssl_hostname_verifier.setSelectedItem(sslReq.getHostNameVerifier());
        jcb_ssl_trust_self_signed_cert.setSelected(sslReq.isTrustSelfSignedCert());
        
        jtf_ssl_keystore_file.setText(sslReq.getKeyStore());
        jpf_ssl_keystore_pwd.setText(new String(sslReq.getKeyStorePassword()));
        
        jtf_ssl_truststore_file.setText(sslReq.getTrustStore());
        jpf_ssl_truststore_pwd.setText(new String(sslReq.getTrustStorePassword()));
    }
    
    @Override
    public void clear() {
        jcb_ssl_hostname_verifier.setSelectedItem(SSLHostnameVerifier.STRICT);
        jcb_ssl_trust_self_signed_cert.setSelected(false);
        jtf_ssl_keystore_file.setText("");
        jtf_ssl_truststore_file.setText("");
        jpf_ssl_keystore_pwd.setText("");
        jpf_ssl_truststore_pwd.setText("");
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

        { // Trust store:
            JPanel jp = new JPanel(new BorderLayout(RESTView.BORDER_WIDTH, 2));

            JPanel jp_label = new JPanel(new GridLayout(2, 1));
            jp_label.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Truststore file:")));
            jp_label.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Truststore password:")));
            jp.add(jp_label, BorderLayout.WEST);

            JPanel jp_input = new JPanel(new GridLayout(2, 1));
            JPanel jp_truststore_file = UIUtil.getFlowLayoutPanelLeftAligned(jtf_ssl_truststore_file);
            jb_ssl_browse.setToolTipText("Open truststore file.");
            jb_ssl_browse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    File f = rest_ui.getOpenFile(FileChooserType.OPEN_GENERIC);
                    if(f == null){
                        // do nothing--cancel pressed
                    }
                    else if(f.canRead()){
                        jtf_ssl_truststore_file.setText(f.getAbsolutePath());
                    }
                    else{
                        view.setStatusMessage("Truststore file cannot be read.");
                    }
                }
            });
            jp_truststore_file.add(jb_ssl_browse);
            jp_input.add(jp_truststore_file);

            jp_input.add(UIUtil.getFlowLayoutPanelLeftAligned(jpf_ssl_truststore_pwd));
            jp.add(jp_input, BorderLayout.CENTER);

            jtp_ssl.addTab("Truststore", UIUtil.getFlowLayoutPanelLeftAligned(jp));
        }

        { // Key store
            JPanel jp = new JPanel(new BorderLayout(RESTView.BORDER_WIDTH, 2));

            JPanel jp_label = new JPanel(new GridLayout(2, 1));
            jp_label.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Keystore file:")));
            jp_label.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Keystore password:")));
            jp.add(jp_label, BorderLayout.WEST);

            JPanel jp_input = new JPanel(new GridLayout(2, 1));
            JPanel jp_keystore_file = UIUtil.getFlowLayoutPanelLeftAligned(jtf_ssl_keystore_file);
            jb_ssl_keystore_browse.setToolTipText("Open keystore file.");
            jb_ssl_keystore_browse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    File f = rest_ui.getOpenFile(FileChooserType.OPEN_GENERIC);
                    if(f == null){
                        // do nothing--cancel pressed
                    }
                    else if(f.canRead()){
                        jtf_ssl_keystore_file.setText(f.getAbsolutePath());
                    }
                    else{
                        view.setStatusMessage("Keystore file cannot be read.");
                    }
                }
            });
            jp_keystore_file.add(jb_ssl_keystore_browse);
            jp_input.add(jp_keystore_file);

            jp_input.add(UIUtil.getFlowLayoutPanelLeftAligned(jpf_ssl_keystore_pwd));

            jp.add(jp_input, BorderLayout.CENTER);

            jtp_ssl.addTab("Keystore", UIUtil.getFlowLayoutPanelLeftAligned(jp));
        }

        add(jtp_ssl);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
