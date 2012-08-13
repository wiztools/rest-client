package org.wiztools.restclient.ui.reqauth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.bean.HTTPAuthMethod;
import org.wiztools.restclient.ui.AuthHelper;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ReqAuthPanelImpl extends JPanel implements ReqAuthPanel {
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    // Authentication resources
    private JComboBox jcb_auth_types = new JComboBox(AuthHelper.getAll());
    private JCheckBox jcb_auth_preemptive = new JCheckBox();
    
    private static final int auth_text_size = 20;
    private JTextField jtf_auth_host = new JTextField(auth_text_size);
    private JTextField jtf_auth_realm = new JTextField(auth_text_size);
    private JTextField jtf_auth_domain = new JTextField(auth_text_size);
    private JTextField jtf_auth_workstation = new JTextField(auth_text_size);
    private JTextField jtf_auth_username = new JTextField(auth_text_size);
    private JPasswordField jpf_auth_password = new JPasswordField(auth_text_size);
    private JTextField jtf_auth_ntlm_username = new JTextField(auth_text_size);
    private JPasswordField jpf_auth_ntlm_password = new JPasswordField(auth_text_size);
    private JTextField jtf_auth_bearer_token = new JTextField(auth_text_size);
    
    @Override
    public boolean isPreemptive() {
        return jcb_auth_preemptive.isSelected();
    }
    
    @Override
    public void setPreemptive(boolean b) {
        jcb_auth_preemptive.setSelected(b);
    }
    
    @Override
    public String getHost() {
        return jtf_auth_host.getText();
    }
    
    @Override
    public void setHost(String authHost) {
        jtf_auth_host.setText(authHost);
    }
    
    @Override
    public String getRealm() {
        return jtf_auth_realm.getText();
    }
    
    @Override
    public void setRealm(String authRealm) {
        jtf_auth_realm.setText(authRealm);
    }
    
    @Override
    public String getDomain() {
        return jtf_auth_domain.getText();
    }
    
    @Override
    public void setDomain(String authDomain) {
        jtf_auth_domain.setText(authDomain);
    }
    
    @Override
    public String getWorkstation() {
        return jtf_auth_workstation.getText();
    }
    
    @Override
    public void setWorkstation(String authWorkstation) {
        jtf_auth_workstation.setText(authWorkstation);
    }
    
    @Override
    public String getUsername() {
        return jtf_auth_username.getText();
    }
    
    @Override
    public void setUsername(String authUsername) {
        jtf_auth_username.setText(authUsername);
    }
    
    @Override
    public char[] getPassword() {
        return jpf_auth_password.getPassword();
    }
    
    @Override
    public void setPassword(String authPassword) {
        jpf_auth_password.setText(authPassword);
    }
    
    @Override
    public String getNtlmUsername() {
        return jtf_auth_ntlm_username.getText();
    }
    
    @Override
    public void setNtlmUsername(String ntlmUsername) {
        jtf_auth_ntlm_username.setText(ntlmUsername);
    }
    
    @Override
    public char[] getNtlmPassword() {
        return jpf_auth_ntlm_password.getPassword();
    }
    
    @Override
    public void setNtlmPassword(String ntlmPassword) {
        jpf_auth_ntlm_password.setText(ntlmPassword);
    }
    
    @Override
    public String getBearerToken() {
        return jtf_auth_bearer_token.getText();
    }
    
    @Override
    public void setBearerToken(String bearerToken) {
        jtf_auth_bearer_token.setText(bearerToken);
    }
    
    @Override
    public void clear() {
        jcb_auth_types.setSelectedItem(AuthHelper.NONE);
        
        jcb_auth_preemptive.setSelected(false);
        
        jtf_auth_host.setText("");
        jtf_auth_realm.setText("");
        jtf_auth_domain.setText("");
        jtf_auth_workstation.setText("");
        jtf_auth_username.setText("");
        jpf_auth_password.setText("");
        jtf_auth_ntlm_username.setText("");
        jpf_auth_ntlm_password.setText("");
        jtf_auth_bearer_token.setText("");
    }
    
    @Override
    public void setAuthMethod(HTTPAuthMethod authMethod) {
        switch(authMethod){
            case BASIC:
                jcb_auth_types.setSelectedItem(AuthHelper.BASIC);
                break;
            case DIGEST:
                jcb_auth_types.setSelectedItem(AuthHelper.DIGEST);
                break;
            case NTLM:
                jcb_auth_types.setSelectedItem(AuthHelper.NTLM);
                break;
            case OAUTH_20_BEARER:
                jcb_auth_types.setSelectedItem(AuthHelper.OAUTH2_BEARER);
                break;
        }
    }
    
    @Override
    public boolean isAuthSelected() {
        return !((String) jcb_auth_types.getSelectedItem()).equals(AuthHelper.NONE);
    }
    
    @Override
    public String getAuthMethod() {
        return (String) jcb_auth_types.getSelectedItem();
    }
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout());
        add(jcb_auth_types, BorderLayout.NORTH);

        // BASIC / DIGEST form:
        JPanel jp_form_label = new JPanel(new GridLayout(5, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_form_label.add(new JLabel("<html>Host: </html>"));
        jp_form_label.add(new JLabel("<html>Realm: </html>"));
        jp_form_label.add(new JLabel("<html>Username: <font color=red>*</font></html>"));
        jp_form_label.add(new JLabel("<html>Password: <font color=red>*</font></html>"));
        JLabel jl_premptive = new JLabel("Preemptive?");
        String toolTipText = "Send authentication credentials before challenge";
        jl_premptive.setToolTipText(toolTipText);
        jcb_auth_preemptive.setToolTipText(toolTipText);
        jl_premptive.setLabelFor(jcb_auth_preemptive);
        jl_premptive.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if(jcb_auth_preemptive.isSelected()) {
                    jcb_auth_preemptive.setSelected(false);
                }
                else {
                    jcb_auth_preemptive.setSelected(true);
                }
            }
        });
        jp_form_label.add(jl_premptive);

        JPanel jp_form_input = new JPanel(new GridLayout(5, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_form_input.add(jtf_auth_host);
        jp_form_input.add(jtf_auth_realm);
        jp_form_input.add(jtf_auth_username);
        jp_form_input.add(jpf_auth_password);
        jp_form_input.add(jcb_auth_preemptive);

        JPanel jp_form = new JPanel(new BorderLayout());
        jp_form.add(jp_form_label, BorderLayout.WEST);
        jp_form.add(jp_form_input, BorderLayout.CENTER);
        final JPanel jp_jsp_form = UIUtil.getFlowLayoutPanelLeftAligned(jp_form);

        // None Panel:
        final JPanel jp_none = UIUtil.getFlowLayoutPanelLeftAligned(new JPanel());

        // OAuth 2 Panel:
        JPanel jp_oauth2_bearer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jl_oauth2_bearer = new JLabel("Bearer Token: ");
        jp_oauth2_bearer.add(jl_oauth2_bearer);
        jp_oauth2_bearer.add(jtf_auth_bearer_token);
        final JPanel jp_jsp_oauth2_bearer = UIUtil.getFlowLayoutPanelLeftAligned(jp_oauth2_bearer);

        // NTLM Panel:
        JPanel jp_ntlm_label = new JPanel(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_ntlm_label.add(new JLabel("<html>Domain: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Workstation: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Username: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Password: <font color=red>*</font></html>"));

        JPanel jp_ntlm_form = new JPanel(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_ntlm_form.add(jtf_auth_domain);
        jp_ntlm_form.add(jtf_auth_workstation);
        jp_ntlm_form.add(jtf_auth_ntlm_username);
        jp_ntlm_form.add(jpf_auth_ntlm_password);

        JButton jb_workstation_name = new JButton(UIUtil.getIconFromClasspath("org/wiztools/restclient/computer.png"));
        jb_workstation_name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    final String localHost = InetAddress.getLocalHost().getHostName();
                    jtf_auth_workstation.setText(localHost);
                    jtf_auth_workstation.selectAll();
                    jtf_auth_workstation.requestFocus();
                }
                catch(UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JPanel jp_ntlm_east = new JPanel(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_ntlm_east.add(new JPanel());
        jp_ntlm_east.add(jb_workstation_name);

        JPanel jp_ntlm = new JPanel(new BorderLayout());
        jp_ntlm.add(jp_ntlm_label, BorderLayout.WEST);
        jp_ntlm.add(jp_ntlm_form, BorderLayout.CENTER);
        jp_ntlm.add(jp_ntlm_east, BorderLayout.EAST);

        final JPanel jp_jsp_ntlm = UIUtil.getFlowLayoutPanelLeftAligned(jp_ntlm);

        // The Scrollpane:
        final JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(jp_none);
        jcb_auth_types.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                final String selected = (String) jcb_auth_types.getSelectedItem();
                if(AuthHelper.isNone(selected)) {
                    jsp.setViewportView(jp_none);
                }
                else if(AuthHelper.isBasicOrDigest(selected)) {
                    jsp.setViewportView(jp_jsp_form);
                    jtf_auth_host.requestFocus();
                }
                else if(AuthHelper.isNtlm(selected)) {
                    jsp.setViewportView(jp_jsp_ntlm);
                    jtf_auth_domain.requestFocus();
                }
                else if(AuthHelper.isBearer(selected)) {
                    jsp.setViewportView(jp_jsp_oauth2_bearer);
                    jtf_auth_bearer_token.requestFocus();
                }
            }
        });

        add(jsp, BorderLayout.CENTER);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
