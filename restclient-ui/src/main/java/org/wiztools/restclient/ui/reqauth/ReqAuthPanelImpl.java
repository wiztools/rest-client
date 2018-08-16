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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.*;
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
    private final JComboBox<String> jcb_types = new JComboBox<String>(AuthHelper.getAll());
    private final JCheckBox jcb_preemptive = new JCheckBox();
    
    private static final int auth_text_size = 20;
    private final JTextField jtf_host = new JTextField(auth_text_size);
    private final JTextField jtf_realm = new JTextField(auth_text_size);
    private final JTextField jtf_domain = new JTextField(auth_text_size);
    private final JTextField jtf_workstation = new JTextField(auth_text_size);
    private final JTextField jtf_username = new JTextField(auth_text_size);
    private final JPasswordField jpf_password = new JPasswordField(auth_text_size);
    private final JTextField jtf_ntlm_username = new JTextField(auth_text_size);
    private final JPasswordField jpf_ntlm_password = new JPasswordField(auth_text_size);
    private final JTextField jtf_bearer_token = new JTextField(auth_text_size);

    @Override
    public Auth getAuth() {
        final String method = (String) jcb_types.getSelectedItem();
        if(AuthHelper.isBasic(method)) {
            BasicAuthBean out = new BasicAuthBean();
            populateBasicDigestAuth(out);
            return out;
        }
        else if(AuthHelper.isDigest(method)) {
            DigestAuthBean out = new DigestAuthBean();
            populateBasicDigestAuth(out);
            return out;
        }
        else if(AuthHelper.isNtlm(method)) {
            NtlmAuthBean out = new NtlmAuthBean();
            out.setDomain(jtf_domain.getText());
            out.setWorkstation(jtf_workstation.getText());
            out.setUsername(jtf_ntlm_username.getText());
            out.setPassword(jpf_ntlm_password.getPassword());
            return out;
        }
        else if(AuthHelper.isBearer(method)) {
            OAuth2BearerAuthBean out = new OAuth2BearerAuthBean();
            out.setOAuth2BearerToken(jtf_bearer_token.getText());
            return out;
        }
        
        return null;
    }
    
    private void populateBasicDigestAuth(BasicDigestAuthBaseBean bean) {
        if(StringUtil.isNotEmpty(jtf_host.getText()))
            bean.setHost(jtf_host.getText());
        if(StringUtil.isNotEmpty(jtf_realm.getText()))
            bean.setRealm(jtf_realm.getText());
        if(StringUtil.isNotEmpty(jtf_username.getText()))
            bean.setUsername(jtf_username.getText());
        if(jpf_password.getPassword().length > 0)
            bean.setPassword(jpf_password.getPassword());
        
        bean.setPreemptive(jcb_preemptive.isSelected());
    }

    @Override
    public void setAuth(Auth auth) {
        if(auth instanceof BasicDigestAuth) {
            final String authType = auth instanceof BasicAuth? AuthHelper.BASIC: AuthHelper.DIGEST;
            jcb_types.setSelectedItem(authType);
            
            BasicDigestAuth a = (BasicDigestAuth) auth;
            jtf_host.setText(a.getHost());
            jtf_realm.setText(a.getRealm());
            jtf_username.setText(a.getUsername());
            if(a.getPassword() != null && a.getPassword().length > 0)
                jpf_password.setText(new String(a.getPassword()));
            jcb_preemptive.setSelected(a.isPreemptive());
        }
        else if(auth instanceof NtlmAuth) {
            jcb_types.setSelectedItem(AuthHelper.NTLM);
            
            NtlmAuth a = (NtlmAuth) auth;
            jtf_domain.setText(a.getDomain());
            jtf_workstation.setText(a.getWorkstation());
            jtf_ntlm_username.setText(a.getUsername());
            if(a.getPassword() != null && a.getPassword().length > 0)
                jpf_ntlm_password.setText(new String(a.getPassword()));
        }
        else if(auth instanceof OAuth2BearerAuth) {
            jcb_types.setSelectedItem(AuthHelper.OAUTH2_BEARER);
            OAuth2BearerAuth a = (OAuth2BearerAuth) auth;
            jtf_bearer_token.setText(a.getOAuth2BearerToken());
        }
    }
    
    @Override
    public void clear() {
        jcb_types.setSelectedItem(AuthHelper.NONE);
        
        jcb_preemptive.setSelected(false);
        
        jtf_host.setText("");
        jtf_realm.setText("");
        jtf_domain.setText("");
        jtf_workstation.setText("");
        jtf_username.setText("");
        jpf_password.setText("");
        jtf_ntlm_username.setText("");
        jpf_ntlm_password.setText("");
        jtf_bearer_token.setText("");
    }

    @Override
    public List<String> validateIfFilled() {
        
        String method = (String) jcb_types.getSelectedItem();
        if(AuthHelper.isNone(method)) {
            return Collections.<String>emptyList();
        }
        
        List<String> errors = new ArrayList<String>();
        
        if(AuthHelper.isBasicOrDigest(method)) {
            if(StringUtil.isEmpty(jtf_username.getText())){
                errors.add("Username is empty.");
            }
        }
        else if(AuthHelper.isNtlm(method)) {
            if(StringUtil.isEmpty(jtf_domain.getText())){
                errors.add("Domain is empty.");
            }
            if(StringUtil.isEmpty(jtf_workstation.getText())){
                errors.add("Workstation is empty.");
            }
            if(StringUtil.isEmpty(jtf_ntlm_username.getText())){
                errors.add("Username is empty.");
            }
            if(StringUtil.isEmpty(new String(jpf_ntlm_password.getPassword()))){
                errors.add("Password is empty.");
            }
        }
        else { // OAuth
            if(StringUtil.isEmpty(jtf_bearer_token.getText())) {
                errors.add("OAuth2 Bearer Token is empty.");
            }
        }
        
        return errors;
    }
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout());
        add(jcb_types, BorderLayout.NORTH);

        // BASIC / DIGEST form:
        JPanel jp_form_label = new JPanel(new GridLayout(5, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_form_label.add(new JLabel("<html>Host: </html>"));
        jp_form_label.add(new JLabel("<html>Realm: </html>"));
        jp_form_label.add(new JLabel("<html>Username: <font color=red>*</font></html>"));
        jp_form_label.add(new JLabel("<html>Password: </html>"));
        JLabel jl_premptive = new JLabel("Preemptive?");
        String toolTipText = "Send authentication credentials before challenge";
        jl_premptive.setToolTipText(toolTipText);
        jcb_preemptive.setToolTipText(toolTipText);
        jl_premptive.setLabelFor(jcb_preemptive);
        jl_premptive.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if(jcb_preemptive.isSelected()) {
                    jcb_preemptive.setSelected(false);
                }
                else {
                    jcb_preemptive.setSelected(true);
                }
            }
        });
        jp_form_label.add(jl_premptive);

        JPanel jp_form_input = new JPanel(new GridLayout(5, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_form_input.add(jtf_host);
        jp_form_input.add(jtf_realm);
        jp_form_input.add(jtf_username);
        jp_form_input.add(jpf_password);
        jp_form_input.add(jcb_preemptive);

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
        jp_oauth2_bearer.add(jtf_bearer_token);
        final JPanel jp_jsp_oauth2_bearer = UIUtil.getFlowLayoutPanelLeftAligned(jp_oauth2_bearer);

        // NTLM Panel:
        JPanel jp_ntlm_label = new JPanel(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_ntlm_label.add(new JLabel("<html>Domain: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Workstation: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Username: <font color=red>*</font></html>"));
        jp_ntlm_label.add(new JLabel("<html>Password: <font color=red>*</font></html>"));

        JPanel jp_ntlm_form = new JPanel(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_ntlm_form.add(jtf_domain);
        jp_ntlm_form.add(jtf_workstation);
        jp_ntlm_form.add(jtf_ntlm_username);
        jp_ntlm_form.add(jpf_ntlm_password);

        JButton jb_workstation_name = new JButton(UIUtil.getIconFromClasspath("org/wiztools/restclient/computer.png"));
        jb_workstation_name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    final String localHost = InetAddress.getLocalHost().getHostName();
                    jtf_workstation.setText(localHost);
                    jtf_workstation.selectAll();
                    jtf_workstation.requestFocus();
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
        jcb_types.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                final String selected = (String) jcb_types.getSelectedItem();
                if(AuthHelper.isNone(selected)) {
                    jsp.setViewportView(jp_none);
                }
                else if(AuthHelper.isBasicOrDigest(selected)) {
                    jsp.setViewportView(jp_jsp_form);
                    jtf_host.requestFocus();
                }
                else if(AuthHelper.isNtlm(selected)) {
                    jsp.setViewportView(jp_jsp_ntlm);
                    jtf_domain.requestFocus();
                }
                else if(AuthHelper.isBearer(selected)) {
                    jsp.setViewportView(jp_jsp_oauth2_bearer);
                    jtf_bearer_token.requestFocus();
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
