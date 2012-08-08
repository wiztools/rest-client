package org.wiztools.restclient.ui;

import com.jidesoft.swing.AutoCompletion;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import junit.framework.TestSuite;
import org.wiztools.commons.*;
import org.wiztools.restclient.*;

/**
 *
 * @author Subhash
 */
@Singleton
class RESTView extends JPanel implements View {
    private static final Logger LOG = Logger.getLogger(RESTView.class.getName());
    
    private ImageIcon icon_go = UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png");
    private ImageIcon icon_stop = UIUtil.getIconFromClasspath("org/wiztools/restclient/stop.png");
    
    private JRadioButton jrb_req_get = new JRadioButton("GET");
    private JRadioButton jrb_req_post = new JRadioButton("POST");
    private JRadioButton jrb_req_put = new JRadioButton("PUT");
    private JRadioButton jrb_req_patch = new JRadioButton("PATCH");
    private JRadioButton jrb_req_delete = new JRadioButton("DELETE");
    private JRadioButton jrb_req_head = new JRadioButton("HEAD");
    private JRadioButton jrb_req_options = new JRadioButton("OPTIONS");
    private JRadioButton jrb_req_trace = new JRadioButton("TRACE");
    
    private JProgressBar jpb_status = new JProgressBar();
    
    private JLabel jl_status = new JLabel(RCConstants.TITLE);
    private JLabel jl_url = new JLabel("URL: ");
    private boolean fromKeyboard = false;
    private JComboBox jcb_url = new JComboBox();
    
    private JButton jb_request = null;
    
    private JTextField jtf_res_status = new JTextField();
    
    private JTextField jtf_body_content_type = new JTextField();
    private ScriptEditor se_req_body;
    {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        final boolean enableSyntaxColoring = Boolean.valueOf(
                options.getProperty("request.body.syntax.color")==null?
                    "true": options.getProperty("request.body.syntax.color"));
        if(enableSyntaxColoring) {
            se_req_body = ScriptEditorFactory.getXMLScriptEditor();
        }
        else {
            se_req_body = ScriptEditorFactory.getTextAreaScriptEditor();
        }
    }
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    private BodyContentTypeDialog jd_body_content_type;
    
    // private JScrollPane jsp_test_script;
    private ScriptEditor se_test_script = ScriptEditorFactory.getGroovyScriptEditor();
    private JButton jb_req_test_template = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_template.png"));
    private JButton jb_req_test_open = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_req_test_run = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "wand.png"));
    private JButton jb_req_test_quick = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "quick_test.png"));
    private RunTestDialog jd_runTestDialog;
    
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
    
    // SSL - trust store
    private JTextField jtf_ssl_truststore_file = new JTextField(auth_text_size);
    private JButton jb_ssl_browse = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JPasswordField jpf_ssl_truststore_pwd = new JPasswordField(auth_text_size);
    
    // SSL - key store
    private JTextField jtf_ssl_keystore_file = new JTextField(auth_text_size);
    private JButton jb_ssl_keystore_browse = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JPasswordField jpf_ssl_keystore_pwd = new JPasswordField(auth_text_size);
    
    // SSL - misc
    private JComboBox jcb_ssl_hostname_verifier = new JComboBox(SSLHostnameVerifier.getAll());
    private JCheckBox jcb_ssl_trust_self_signed_cert = new JCheckBox("Trust self-signed certificate? ");
    
    // HTTP Version Combo box
    private JComboBox jcb_http_version = new JComboBox(HTTPVersion.values());

    // Follow redirect
    private JCheckBox jcb_followRedirects = new JCheckBox("Follow HTTP Redirects? ");
    
    // Ignore body
    private JCheckBox jcb_ignoreResponseBody = new JCheckBox("Ignore Response Body? ");
    
    // Response
    // private JScrollPane jsp_res_body = new JScrollPane();
    private ScriptEditor se_response;
    {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        final boolean enableSyntaxColoring = Boolean.valueOf(
                options.getProperty("response.body.syntax.color")==null?
                    "true": options.getProperty("response.body.syntax.color"));
        if(enableSyntaxColoring) {
            se_response = ScriptEditorFactory.getXMLScriptEditor();
        }
        else {
            se_response = ScriptEditorFactory.getTextAreaScriptEditor();
        }
    }
    
    private JTable jt_res_headers = new JTable();

    //private JScrollPane jsp_test_result;
    //private JTextArea jta_test_result = new JTextArea();
    private TestResultPanel jp_testResultPanel = new TestResultPanel();

    private TwoColumnTablePanel jp_2col_req_headers;
    private TwoColumnTablePanel jp_2col_req_cookies;
    
    private ParameterDialog jd_req_paramDialog;
    
    private ResponseHeaderTableModel resHeaderTableModel = new ResponseHeaderTableModel();
    
    // Session Details
    SessionFrame sessionFrame = new SessionFrame("RESTClient: Session View");

    private MessageDialog messageDialog;
    private final RESTView view;
    private final RESTUserInterface rest_ui;
    
    public static final int BORDER_WIDTH = 5;

    // RequestThread
    private Thread requestThread;

    /*
     * unindentedResponseBody holds the unindented version of the response body
     * text which is shown in UI currently. This variable should be carefully
     * be dealt with, as wrong handling could make this value stale.
     */
    private String unindentedResponseBody;
    
    // Cache the last request and response
    private Request lastRequest;
    private Response lastResponse;
    
    // Load templateTestScript:
    private static final String templateTestScript;
    static{
        InputStream is = RESTView.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/test-script.template");
        String t = null;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            StringBuilder sb = new StringBuilder();
            while((str = br.readLine()) != null){
                sb.append(str).append("\n");
            }
            br.close();
            t = sb.toString();
        }
        catch(IOException ex){
            assert true: "Failed loading `test-script.template'!";
        }
        templateTestScript = t;
    }

    @Inject
    protected RESTView(final RESTUserInterface ui){
        this(ui, null, null);
    }

    public RESTView(final RESTUserInterface ui, ScriptEditor scriptEditor, ScriptEditor responseViewer) {
        if (scriptEditor != null)
            this.se_test_script = scriptEditor;
        if (responseViewer != null)
            this.se_response = responseViewer;
        this.rest_ui = ui;
        init();
        view = this;
        
        // Start status clear timer:
        statusLastUpdated = Calendar.getInstance();
        new Timer(5*1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar c = (Calendar)statusLastUpdated.clone();
                c.add(Calendar.SECOND, 20);
                if(Calendar.getInstance().after(c)){
                    setStatusMessage(RCConstants.TITLE);
                }
            }
        }).start();
    }
    
    private JTabbedPane initJTPRequest(){
        JTabbedPane jtp = new JTabbedPane();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb_req_get);
        bg.add(jrb_req_post);
        bg.add(jrb_req_put);
        bg.add(jrb_req_patch);
        bg.add(jrb_req_delete);
        bg.add(jrb_req_head);
        bg.add(jrb_req_options);
        bg.add(jrb_req_trace);
        
        // Default selected button
        jrb_req_get.setSelected(true);
        
        // Mnemonic
        jrb_req_get.setMnemonic('g');
        jrb_req_post.setMnemonic('p');
        jrb_req_put.setMnemonic('t');
        jrb_req_delete.setMnemonic('d');
        //jrb_req_head.setMnemonic('h');
        //jrb_req_options.setMnemonic('o');
        //jrb_req_trace.setMnemonic('e');
        
        ActionListener jrbAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(doesSelectedMethodSupportEntityBody()){
                    setUIReqBodyEnabled(true);
                }
                else{
                    setUIReqBodyEnabled(false);
                }
            }
        };
        
        jrb_req_get.addActionListener(jrbAL);
        jrb_req_post.addActionListener(jrbAL);
        jrb_req_put.addActionListener(jrbAL);
        jrb_req_patch.addActionListener(jrbAL);
        jrb_req_delete.addActionListener(jrbAL);
        jrb_req_head.addActionListener(jrbAL);
        jrb_req_options.addActionListener(jrbAL);
        jrb_req_trace.addActionListener(jrbAL);
        
        JPanel jp_method_encp = new JPanel();
        jp_method_encp.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel jp_method = new JPanel();
        jp_method.setBorder(BorderFactory.createTitledBorder("HTTP Method"));
        jp_method.setLayout(new GridLayout(4, 2));
        jp_method.add(jrb_req_get);
        jp_method.add(jrb_req_post);
        jp_method.add(jrb_req_put);
        jp_method.add(jrb_req_patch);
        jp_method.add(jrb_req_delete);
        jp_method.add(jrb_req_head);
        jp_method.add(jrb_req_options);
        jp_method.add(jrb_req_trace);
        jp_method_encp.add(jp_method);
        jtp.addTab("Method", jp_method_encp);
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(new String[]{"Header", "Value"}, rest_ui);
        jtp.addTab("Header", jp_2col_req_headers);
        
        // Cookies Tab
        jp_2col_req_cookies = new TwoColumnTablePanel(new String[]{"Cookie", "Value"}, rest_ui);
        jtp.addTab("Cookie", jp_2col_req_cookies);
        
        // Body Tab
        setUIReqBodyEnabled(false); // disable control by default
        JPanel jp_body = new JPanel();
        jp_body.setLayout(new BorderLayout());
        JPanel jp_body_north = new JPanel();
        jp_body_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        jtf_body_content_type.setEditable(false);
        jtf_body_content_type.setColumns(24);
        jtf_body_content_type.setToolTipText("Selected Content-type & Charset");
        jtf_body_content_type.setText(Util.getFormattedContentType(
                jd_body_content_type.getContentType(),
                jd_body_content_type.getCharSet()));
        jp_body_north.add(jtf_body_content_type);
    
        jb_body_content_type.setToolTipText("Edit Content-type & Charset");
        jb_body_content_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jd_body_content_type.setVisible(true);
            }
        });
        jp_body_north.add(jb_body_content_type);
        
        JSeparator js = new JSeparator(SwingConstants.VERTICAL);
        jp_body_north.add(js);
        
        jb_body_file.setToolTipText("Load from file");
        jb_body_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_body_fileActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_file);
        
        jb_body_params.setToolTipText("Insert parameters");
        jb_body_params.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_body_paramActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_params);
        
        // Popup menu for body content tab
        final JPopupMenu jpm_req_body = new JPopupMenu();
        
        JMenu jm_syntax = new JMenu("Syntax Color");
        JMenuItem jmi_syntax_none = new JMenuItem("None");
        jmi_syntax_none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        JMenuItem jmi_syntax_xml = new JMenuItem("XML");
        jmi_syntax_xml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        
        jpm_req_body.add(jm_syntax);
        
        if (se_req_body.getEditorComponent() instanceof JEditorPane) {
            se_req_body.getEditorComponent().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                private void showPopup(final MouseEvent e) {
                    if(!se_req_body.getEditorComponent().isEnabled()){
                        // do not show popup menu when component is disabled:
                        return;
                    }
                    if (e.isPopupTrigger()) {
                        jpm_req_body.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        
        jp_body.add(jp_body_north, BorderLayout.NORTH);
        JPanel jp_body_center = new JPanel();
        jp_body_center.setLayout(new GridLayout(1, 1));
        jp_body_center.add(se_req_body.getEditorView());
        jp_body.add(jp_body_center, BorderLayout.CENTER);
        jtp.addTab("Body", jp_body);
        
        { // Auth
            JPanel jp = new JPanel(new BorderLayout());
            jp.add(jcb_auth_types, BorderLayout.NORTH);
            
            // BASIC / DIGEST form:
            JPanel jp_form_label = new JPanel(new GridLayout(5, 1, BORDER_WIDTH, BORDER_WIDTH));
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
            
            JPanel jp_form_input = new JPanel(new GridLayout(5, 1, BORDER_WIDTH, BORDER_WIDTH));
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
            JPanel jp_ntlm_label = new JPanel(new GridLayout(4, 1, BORDER_WIDTH, BORDER_WIDTH));
            jp_ntlm_label.add(new JLabel("<html>Domain: <font color=red>*</font></html>"));
            jp_ntlm_label.add(new JLabel("<html>Workstation: <font color=red>*</font></html>"));
            jp_ntlm_label.add(new JLabel("<html>Username: <font color=red>*</font></html>"));
            jp_ntlm_label.add(new JLabel("<html>Password: <font color=red>*</font></html>"));
            
            JPanel jp_ntlm_form = new JPanel(new GridLayout(4, 1, BORDER_WIDTH, BORDER_WIDTH));
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
            JPanel jp_ntlm_east = new JPanel(new GridLayout(4, 1, BORDER_WIDTH, BORDER_WIDTH));
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
            
            jp.add(jsp, BorderLayout.CENTER);
            
            jtp.addTab("Auth", jp);
        }
        
        // SSL Tab
        JPanel jp_ssl = new JPanel();
        jp_ssl.setLayout(new BorderLayout(BORDER_WIDTH, 2));
        
        {
            JTabbedPane jtp_ssl = new JTabbedPane();
            // jtp_ssl.setTabPlacement(JTabbedPane.LEFT);
            
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
                JPanel jp = new JPanel(new BorderLayout(BORDER_WIDTH, 2));
                
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
                            setStatusMessage("Truststore file cannot be read.");
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
                JPanel jp = new JPanel(new BorderLayout(BORDER_WIDTH, 2));
            
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
                            setStatusMessage("Keystore file cannot be read.");
                        }
                    }
                });
                jp_keystore_file.add(jb_ssl_keystore_browse);
                jp_input.add(jp_keystore_file);
                
                jp_input.add(UIUtil.getFlowLayoutPanelLeftAligned(jpf_ssl_keystore_pwd));
                
                jp.add(jp_input, BorderLayout.CENTER);
                
                jtp_ssl.addTab("Keystore", UIUtil.getFlowLayoutPanelLeftAligned(jp));
            }
            
            jp_ssl.add(jtp_ssl);
        }
        jtp.addTab("SSL", jp_ssl);
        
        // Etc panel
        JPanel jp_etc = new JPanel();
        jp_etc.setLayout(new GridLayout(3, 1));
        { // Http Version
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(new JLabel("HTTP Version: "));
            jp.add(jcb_http_version);
            jp_etc.add(jp);
        }
        { // Follow Redirect
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            // Previous version of RESTClient had follow redirects as true.
            // To maintain backward compatibility in default behavior:
            jcb_followRedirects.setSelected(true);
            jcb_followRedirects.setHorizontalTextPosition(SwingConstants.LEFT);
            jcb_followRedirects.setBorder(null);
            jp.add(jcb_followRedirects);
            jp_etc.add(jp);
        }
        { // Ignore response body
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jcb_ignoreResponseBody.setHorizontalTextPosition(SwingConstants.LEFT);
            jcb_ignoreResponseBody.setBorder(null);
            jp.add(jcb_ignoreResponseBody);
            jp_etc.add(jp);
        }
        { // add jp_etc in an enclosing panel
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(jp_etc);
            jtp.add("Etc.", jp);
        }
        
        // Test script panel
        JPanel jp_test = new JPanel();
        jp_test.setLayout(new BorderLayout());
        
        JPanel jp_test_north = new JPanel();
        jp_test_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jb_req_test_template.setToolTipText("Insert Template");
        jb_req_test_template.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = se_test_script.getText();
                if(!StringUtil.isEmpty(t)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "Script text already present! Please clear existing script!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                se_test_script.setText(templateTestScript);
                se_test_script.setCaretPosition(0);
            }
        });
        jp_test_north.add(jb_req_test_template);
        jb_req_test_open.setToolTipText("Open Test Script From File");
        jb_req_test_open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = se_test_script.getText();
                if(!StringUtil.isEmpty(str)){
                    int ret = JOptionPane.showConfirmDialog(rest_ui.getFrame(), "Script already exists. Erase?", "Erase existing script?", JOptionPane.YES_NO_OPTION);
                    if(ret == JOptionPane.NO_OPTION){
                        return;
                    }
                }
                File f = rest_ui.getOpenFile(FileChooserType.OPEN_TEST_SCRIPT);
                if(f == null){ // Cancel pressed
                    return;
                }
                if(!f.canRead()){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "IO Error (Read permission denied): " + f.getAbsolutePath(),
                            "IO Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    String testScript = FileUtil.getContentAsString(f, Charsets.UTF_8);
                    se_test_script.setText(testScript);
                    se_test_script.setCaretPosition(0);
                }
                catch(IOException ex){
                    showError(Util.getStackTrace(ex));
                }
            }
        });
        jp_test_north.add(jb_req_test_open);
        jp_test_north.add(new JSeparator(JSeparator.VERTICAL));
        jb_req_test_run.setToolTipText("Run Test");
        jb_req_test_run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(StringUtil.isEmpty(se_test_script.getText())){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "No script!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(jd_runTestDialog == null){
                    jd_runTestDialog = new RunTestDialog(rest_ui);
                }
                jd_runTestDialog.setVisible(true);
            }
        });
        jp_test_north.add(jb_req_test_run);
        jb_req_test_quick.setToolTipText("Quick Run Test-Using last request & response");
        jb_req_test_quick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lastRequest == null || lastResponse == null){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Last Request/Response", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String testScript = se_test_script.getText();
                if(StringUtil.isEmpty(testScript)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Script", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                runClonedRequestTest(lastRequest, lastResponse);
            }
        });
        jp_test_north.add(jb_req_test_quick);
        jp_test.add(jp_test_north, BorderLayout.NORTH);
        
        jp_test.add(se_test_script.getEditorView(), BorderLayout.CENTER);
        jtp.addTab("Test", jp_test);
        
        return jtp;
    }
    
    void requestFocusAddressBar() {
        jcb_url.requestFocus();
    }
    
    void runClonedRequestTest(Request request, Response response){
        RequestBean t_request = (RequestBean)request.clone();
        t_request.setTestScript(se_test_script.getText());
        try{
            TestSuite ts = TestUtil.getTestSuite(t_request, response);
            TestResult testResult = TestUtil.execute(ts);
            view.showMessage("Test Result", testResult.toString());
        }
        catch(TestException ex){
            view.showError(Util.getStackTrace(ex));
        }
    }
    
    private void actionTextEditorSyntaxChange(final ScriptEditor editor, final TextEditorSyntax syntax){
        ((JSyntaxPaneScriptEditor)editor).setSyntax(syntax);
    }
    
    private JTabbedPane initJTPResponse(){
        JTabbedPane jtp = new JTabbedPane();
        
        // Header Tab
        JPanel jp_headers = new JPanel();
        jp_headers.setLayout(new BorderLayout());
        
        // Header Tab: Other Headers
        JPanel jp_headers_others = new JPanel();
        jp_headers_others.setLayout(new GridLayout(1, 1));
        jt_res_headers.addMouseListener(new MouseAdapter() {
            private JPopupMenu popup = new JPopupMenu();
            private JMenuItem jmi_copy = new JMenuItem("Copy Selected Header(s)");
            private JMenuItem jmi_copy_all = new JMenuItem("Copy All Headers");
            {
                jmi_copy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int[] rows = jt_res_headers.getSelectedRows();
                        Arrays.sort(rows);
                        StringBuilder sb = new StringBuilder();
                        for(final int row: rows) {
                            final String key = (String) jt_res_headers.getValueAt(row, 0);
                            final String value = (String) jt_res_headers.getValueAt(row, 1);
                            sb.append(key).append(": ").append(value).append("\r\n");
                        }
                        UIUtil.clipboardCopy(sb.toString());
                    }
                });
                popup.add(jmi_copy);

                jmi_copy_all.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int totalRows = jt_res_headers.getRowCount();

                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<totalRows; i++) {
                            final String key = (String) jt_res_headers.getValueAt(i, 0);
                            final String value = (String) jt_res_headers.getValueAt(i, 1);

                            sb.append(key).append(": ").append(value).append("\r\n");
                        }
                        UIUtil.clipboardCopy(sb.toString());
                    }
                });
                popup.add(jmi_copy_all);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if(jt_res_headers.getSelectedRowCount() == 0) {
                    jmi_copy.setEnabled(false);
                }
                else {
                    jmi_copy.setEnabled(true);
                }
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        jt_res_headers.setModel(resHeaderTableModel);
        JScrollPane jsp = new JScrollPane(jt_res_headers);
        Dimension d = jsp.getPreferredSize();
        d.height = d.height / 2;
        jsp.setPreferredSize(d);
        jp_headers_others.add(jsp);
        jp_headers.add(jp_headers_others, BorderLayout.CENTER);
        jtp.addTab("Headers", jp_headers);
        
        // Response body
        // First the pop-up menu for xml formatting:
        final JPopupMenu popupMenu = new JPopupMenu();
        
        JMenu jm_indent = new JMenu("Indent");
        
        // Indent XML
        JMenuItem jmi_indentXml = new JMenuItem("Indent XML");
        jmi_indentXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String resText = se_response.getText();
                if("".equals(resText.trim())){
                    setStatusMessage("No response body!");
                    return;
                }
                try {
                    final String indentedXML = XMLUtil.indentXML(resText);
                    se_response.setText(indentedXML);
                    se_response.setCaretPosition(0);
                    setStatusMessage("Indent XML: Success");
                } catch (XMLException ex) {
                    setStatusMessage("Indent XML: XML Parser Configuration Error.");
                } catch (IOException ex) {
                    setStatusMessage("Indent XML: IOError while processing XML.");
                }
            }
        });
        jm_indent.add(jmi_indentXml);
        
        // Indent JSON
        JMenuItem jmi_indentJson = new JMenuItem("Indent JSON");
        jmi_indentJson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String resText = se_response.getText();
                if("".equals(resText.trim())){
                    setStatusMessage("No response body!");
                    return;
                }
                try{
                    String indentedJSON = JSONUtil.indentJSON(resText);
                    se_response.setText(indentedJSON);
                    se_response.setCaretPosition(0);
                    setStatusMessage("Indent JSON: Success");
                }
                catch(JSONUtil.JSONParseException ex){
                    setStatusMessage("Indent JSON: Not a valid JSON text.");
                }
            };
        });
        jm_indent.add(jmi_indentJson);
        
        popupMenu.add(jm_indent);
        
        // Syntax color change
        JMenu jm_syntax = new JMenu("Syntax Color");
        JMenuItem jmi_syntax_xml = new JMenuItem("XML");
        jmi_syntax_xml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        JMenuItem jmi_syntax_none = new JMenuItem("None");
        jmi_syntax_none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        
        popupMenu.add(jm_syntax);
        
        // Attach popup menu
        if (se_response.getEditorComponent() instanceof JEditorPane) {
            se_response.getEditorComponent().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                private void showPopup(final MouseEvent e) {
                    if("".equals(se_response.getText().trim())){
                        // No response body
                        return;
                    }
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        JPanel jp_body = new JPanel();
        jp_body.setLayout(new GridLayout(1,1));
        se_response.setEditable(false);
        jp_body.add(se_response.getEditorView());
        JPanel jp_body_encp = new JPanel();
        jp_body_encp.setBorder(BorderFactory.createEmptyBorder());
        jp_body_encp.setLayout(new GridLayout(1, 1));
        jp_body_encp.add(jp_body);
        jtp.addTab("Body", jp_body_encp);
        
        // Test result
        JPanel jp_test_result = new JPanel();
        jp_test_result.setLayout(new GridLayout(1, 1));
        jp_test_result.add(jp_testResultPanel);
        jtp.addTab("Test Result", jp_test_result);
        
        return jtp;
    }
    
    private JPanel initUIRequest(){
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        jp.setLayout(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new BorderLayout(BORDER_WIDTH, 0));
        jl_url.setLabelFor(jcb_url);
        jl_url.setDisplayedMnemonic('u');
        jp_north.add(jl_url, BorderLayout.WEST);

        { // Keystroke for focusing on the address bar:
            final KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_L,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            final String actionName = "org.wiztools.restclient:ADDRESS_FOCUS";
            jcb_url.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(ks, actionName);
            jcb_url.getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    requestFocusAddressBar();
                }
            });
        }
        jcb_url.setToolTipText("URL");
        jcb_url.setEditable(true);
        jcb_url.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_urlActionPerformed(evt);
            }
        });
        jcb_url.getEditor().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fromKeyboard = true;
            }
        });
        jcb_url.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ((JTextField) jcb_url.getEditor().getEditorComponent()).selectAll();
            }
        });
        // AutoCompleteDecorator.decorate(jcb_url);
        AutoCompletion ac = new AutoCompletion(jcb_url);
        ac.setStrict(false);
        ac.setStrictCompletion(false);
        jp_north.add(jcb_url, BorderLayout.CENTER);
        jb_request = new JButton(icon_go);
        jb_request.setToolTipText("Go!");
        rest_ui.getFrame().getRootPane().setDefaultButton(jb_request);
        jb_request.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_requestActionPerformed();
            }
        });
        jp_north.add(jb_request, BorderLayout.EAST);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center
        jp.add(initJTPRequest(), BorderLayout.CENTER);
        
        jp.setBorder(BorderFactory.createTitledBorder(null, "HTTP Request", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        return jp;
    }
    
    private JPanel initUIResponse(){
        JPanel jp = new JPanel();
        // Set top as 0:
        jp.setBorder(BorderFactory.createEmptyBorder(
                0, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        jp.setLayout(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        
        // Header Tab: Status Line Header
        JPanel jp_status = new JPanel();
        jp_status.setLayout(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        JLabel jl_res_statusLine = new JLabel("Status: ");
        jp_status.add(jl_res_statusLine, BorderLayout.WEST);
        jtf_res_status.setColumns(35);
        jtf_res_status.setEditable(false);
        jp_status.add(jtf_res_status, BorderLayout.CENTER);
        
        jp.add(jp_status, BorderLayout.NORTH);
        
        // Center having tabs
        jp.add(initJTPResponse(), BorderLayout.CENTER);
        
        jp.setBorder(BorderFactory.createTitledBorder(null, "HTTP Response", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        return jp;
    }
    
    private JPanel initUIStatusBar(){
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createBevelBorder(1));
        jp.setLayout(new GridLayout(1, 2));
        jl_status.setFont(UIUtil.FONT_DIALOG_12_PLAIN);
        jp.add(jl_status);
        JPanel jp_south_jcb = new JPanel();
        jp_south_jcb.setLayout(new FlowLayout(FlowLayout.RIGHT));
        Dimension d = jpb_status.getPreferredSize();
        d.height = d.height - 2;
        jpb_status.setPreferredSize(d);
        jpb_status.setIndeterminate(true);
        jpb_status.setVisible(false);
        jp_south_jcb.add(jpb_status);
        jp.add(jp_south_jcb);
        return jp;
    }
    
    private void init(){
        // Initialize the messageDialog
        messageDialog = new MessageDialog(rest_ui.getFrame());
        
        // Initialize parameter dialog
        ParameterView pv = new ParameterView(){
            @Override
            public void setParameter(final String params) {
                se_req_body.setText(params);
            }
            
        };
        jd_req_paramDialog = new ParameterDialog(rest_ui, pv);
        
        // Initialize jd_body_content_type
        jd_body_content_type = new BodyContentTypeDialog(rest_ui.getFrame());
        jd_body_content_type.addContentTypeCharSetChangeListener(new ContentTypeCharSetChangeListener() {
            @Override
            public void changed(String contentType, String charSet) {
                jtf_body_content_type.setText(Util.getFormattedContentType(contentType, charSet));
            }
        });
        
        // Set the font of ScriptEditors:
        String fontName = ServiceLocator.getInstance(IGlobalOptions.class).getProperty("font.options.font");
        String fontSizeStr = ServiceLocator.getInstance(IGlobalOptions.class).getProperty("font.options.fontSize");
        int fontSize = 12; // Default font size is 12
        if(fontSizeStr != null){
            try{
                fontSize = Integer.parseInt(fontSizeStr);
            }
            catch(NumberFormatException ex){
                LOG.log(Level.WARNING, "Font size property is not a number: {0}", fontSizeStr);
            }
        }
        if(fontName != null){
            Font f = new Font(fontName, Font.PLAIN, fontSize);
            se_req_body.getEditorComponent().setFont(f);
            se_response.getEditorComponent().setFont(f);
        }
        
        this.setLayout(new BorderLayout());
        
        // Adding the Center portion
        JSplitPane jsp_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp_main.setDividerSize(5);
        jsp_main.add(initUIRequest());
        jsp_main.add(initUIResponse());
        this.add(jsp_main, BorderLayout.CENTER);
        
        // Now the South portion
        this.add(initUIStatusBar(), BorderLayout.SOUTH);
    }
    
    void showSessionFrame(){
        if(!sessionFrame.isVisible()){
            sessionFrame.setVisible(true);
        }
    }
    
    void setUIToLastRequestResponse(){
        if(lastRequest != null && lastResponse != null){
            setUIFromRequest(lastRequest);
            setUIFromResponse(lastResponse);
        }
    }
    
    Response getResponseFromUI(){
        ResponseBean response = new ResponseBean();
        response.setResponseBody(unindentedResponseBody);
        String statusLine = jtf_res_status.getText();
        response.setStatusLine(statusLine);
        response.setStatusCode(Util.getStatusCodeFromStatusLine(statusLine));
        String[][] headers = ((ResponseHeaderTableModel)jt_res_headers.getModel()).getHeaders();
        for(int i=0; i<headers.length; i++){
            response.addHeader(headers[i][0], headers[i][1]);
        }
        response.setTestResult(jp_testResultPanel.getTestResult());
        return response;
    }
    
    public Request getRequestFromUI(){
        correctRequestURL();
        /*List<String> errors = validateForRequest();
        if(errors.size()!=0){
            String errStr = Util.getHTMLListFromList(errors);
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                errStr,
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }*/
        
        RequestBean request = new RequestBean();
        boolean authEnabled = false;
        
        final String authSelected = (String) jcb_auth_types.getSelectedItem();
        if(!authSelected.equals("None")) {
            authEnabled = true;
        }
        
        if(authEnabled) {
            if(AuthHelper.isBasic(authSelected)){
                request.addAuthMethod(HTTPAuthMethod.BASIC);
            }
            else if(AuthHelper.isDigest(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.DIGEST);
            }
            else if(AuthHelper.isNtlm(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.NTLM);
                
                String domain = jtf_auth_domain.getText();
                String workstation = jtf_auth_workstation.getText();
                String uid = jtf_auth_ntlm_username.getText();
                char[] pwd = jpf_auth_ntlm_password.getPassword();

                request.setAuthDomain(domain);
                request.setAuthWorkstation(workstation);
                request.setAuthUsername(uid);
                request.setAuthPassword(pwd);
            }
            else if(AuthHelper.isBearer(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.OAUTH_20_BEARER);
                
                request.setAuthBearerToken(jtf_auth_bearer_token.getText());
            }
            
            if(AuthHelper.isBasicOrDigest(authSelected)){ // BASIC or DIGEST:
                String uid = jtf_auth_username.getText();
                char[] pwd = jpf_auth_password.getPassword();

                String realm = jtf_auth_realm.getText();
                String host = jtf_auth_host.getText();
                boolean preemptive = jcb_auth_preemptive.isSelected();

                request.setAuthPreemptive(preemptive);
                request.setAuthUsername(uid);
                request.setAuthPassword(pwd);
                request.setAuthRealm(realm);
                request.setAuthHost(host);
            }
        }
        
        String url = (String)jcb_url.getSelectedItem();
        try{
            request.setUrl(new URL(url));
        }
        catch(MalformedURLException ex){
            // URL is left null!
        }
        if(jrb_req_get.isSelected()){
            request.setMethod(HTTPMethod.GET);
        }
        else if(jrb_req_head.isSelected()){
            request.setMethod(HTTPMethod.HEAD);
        }
        else if(jrb_req_post.isSelected()){
            request.setMethod(HTTPMethod.POST);
        }
        else if(jrb_req_put.isSelected()){
            request.setMethod(HTTPMethod.PUT);
        }
        else if(jrb_req_patch.isSelected()) {
            request.setMethod(HTTPMethod.PATCH);
        }
        else if(jrb_req_delete.isSelected()){
            request.setMethod(HTTPMethod.DELETE);
        }
        else if(jrb_req_options.isSelected()){
            request.setMethod(HTTPMethod.OPTIONS);
        }
        else if(jrb_req_trace.isSelected()){
            request.setMethod(HTTPMethod.TRACE);
        }
        
        { // Get request headers
            Object[][] header_data = jp_2col_req_headers.getTableModel().getData();
            if(header_data.length > 0){
                for(int i=0; i<header_data.length; i++){
                    String key = (String)header_data[i][0];
                    String value = (String)header_data[i][1];
                    request.addHeader(key, value);
                }
            }
        }
        
        { // Cookies
            Object[][] cookie_data = jp_2col_req_cookies.getTableModel().getData();
            if(cookie_data.length > 0) {
                for(int i=0; i<cookie_data.length; i++){
                    String key = (String)cookie_data[i][0];
                    String value = (String)cookie_data[i][1];
                    try {
                        HttpCookie cookie = new HttpCookie(key, value);
                        request.addCookie(cookie);
                    }
                    catch(IllegalArgumentException ex) {
                        doError(Util.getStackTrace(ex));
                    }
                }
            }
        }
        
        // EntityEnclosing method specific
        if(doesSelectedMethodSupportEntityBody()){
            // Get request body
            String req_body = se_req_body.getText();
            if(!StringUtil.isEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                ReqEntityStringBean body = new ReqEntityStringBean(req_body,
                        req_content_type,
                        req_char_set);
                request.setBody(body);
            }
        }
        
        // SSL specific
        request.setSslTrustStore(jtf_ssl_truststore_file.getText());
        request.setSslTrustStorePassword(jpf_ssl_truststore_pwd.getPassword());
        request.setSslKeyStore(jtf_ssl_keystore_file.getText());
        request.setSslKeyStorePassword(jpf_ssl_keystore_pwd.getPassword());
        request.setSslHostNameVerifier((SSLHostnameVerifier)jcb_ssl_hostname_verifier.getSelectedItem());
        request.setSslTrustSelfSignedCert(jcb_ssl_trust_self_signed_cert.isSelected());
        
        // HTTP version
        request.setHttpVersion((HTTPVersion)jcb_http_version.getSelectedItem());

        // Follow redirect
        request.setFollowRedirect(jcb_followRedirects.isSelected());
        
        // Ignore response body
        request.setIgnoreResponseBody(jcb_ignoreResponseBody.isSelected());
        
        // Test script specific
        String testScript = se_test_script.getText();
        testScript = testScript == null || testScript.trim().equals("")?
            null: testScript.trim();
        request.setTestScript(testScript);
        return request;
    }

    private void jb_requestActionPerformed() {
        if(jb_request.getIcon() == icon_go){
            final Request request = getRequestFromUI();
            List<String> errors = validateRequest(request);
            if(errors.isEmpty()){
                clearUIResponse();
                final RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
                // Execute the request:
                requestThread = new Thread(){
                    @Override
                    public void run(){
                        executer.execute(request, view);
                    }

                    @Override
                    public void interrupt(){
                        executer.abortExecution();
                        super.interrupt();
                    }
                };
                requestThread.start();
            }
            else{
                String errStr = Util.getHTMLListFromList(errors);
                JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    errStr,
                    "Validation error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(jb_request.getIcon() == icon_stop){
            requestThread.interrupt();
            jb_request.setIcon(icon_go);
        }
    }                                          

    @Override
    public void doStart(Request request){
        lastRequest = request;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jpb_status.setVisible(true);
                // jb_request.setEnabled(false);
                jb_request.setIcon(icon_stop);
                jb_request.setToolTipText("Stop!");

                // Update status message
                setStatusMessage("Processing request...");
            }
        });
    }
    
    @Override
    public void doResponse(final Response response){
        lastResponse = response;
    
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Update the UI:
                setUIFromResponse(response);

                // Update status message
                setStatusMessage("Response received in: " + response.getExecutionTime() + " ms");

                // Update Session View
                if(sessionFrame.isVisible()){
                    sessionFrame.getSessionView().add(lastRequest, lastResponse);
                }
            }
        });
    }
    
    @Override
    public void doCancelled(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setStatusMessage("Request cancelled!");
            }
        });
    }
    
    @Override
    public void doEnd(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jpb_status.setVisible(false);
                // jb_request.setEnabled(true);
                jb_request.setIcon(icon_go);
                jb_request.setToolTipText("Go!");
            }
        });
    }
    
    @Override
    public void doError(final String error){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showError(error);
                setStatusMessage("An error occurred during request.");
            }
        });
        
    }
    
    public void showError(final String error){
        messageDialog.showError(error);
    }
    
    public void showMessage(final String title, final String message){
        messageDialog.showMessage(title, message);
    }
    
    void clearUIResponse(){
        unindentedResponseBody = null;
        jtf_res_status.setText("");
        se_response.setText("");
        ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_res_headers.getModel();
        model.setHeaders(null);
        jp_testResultPanel.clear();
    }
    
    private void jcb_urlActionPerformed(final ActionEvent event){
        if("comboBoxChanged".equals(event.getActionCommand())){
            return;
        }
        final Object item = jcb_url.getSelectedItem();
        final int count = jcb_url.getItemCount();
        final LinkedList l = new LinkedList();
        for(int i=0; i<count; i++){
            l.add(jcb_url.getItemAt(i));
        }
        if(l.contains(item)){ // Item already present
            // Remove and add to bring it to the top
            // l.remove(item);
            // l.addFirst(item);
            // System.out.println("Removing and inserting at top");
            jcb_url.removeItem(item);
            jcb_url.insertItemAt(item, 0);
        }
        else{ // Add new item
            if(((String)item).trim().length() != 0 ) {
                // The total number of items should not exceed 20
                if(count > 19){
                    // Remove last item to give place
                    // to new one
                    //l.removeLast();
                    jcb_url.removeItemAt(count - 1);
                }
                //l.addFirst(item);
                jcb_url.insertItemAt(item, 0);
            }
        }
        // make the selected item is the item we want
        jcb_url.setSelectedItem(item);
        // Use this to trigger request action on pressing Enter:
        if (fromKeyboard) {
            fromKeyboard = false;
            jb_requestActionPerformed();
        }
    }
    
    private void jb_body_fileActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_REQUEST_BODY);
        if(f == null){ // Pressed cancel?
            return;
        }
        if(!f.canRead()){
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    "File not readable: " + f.getAbsolutePath(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Determine the MIME type and set parameter
        String contentType = FileUtil.getMimeType(f);
        String charset = null;
        if(XMLUtil.XML_MIME.equals(contentType)){
            try{
                charset = XMLUtil.getDocumentCharset(f);
            }
            catch(IOException ex){
                // Do nothing!
            }
            catch(XMLException ex){
                // Do nothing!
            } 
        }
        String oldContentType = jd_body_content_type.getContentType();
        String oldCharset = jd_body_content_type.getCharSet();
        if(!oldContentType.equals(contentType)){
            int contentTypeYesNo = JOptionPane.showConfirmDialog(view,
                    "Change ContentType To: " + contentType + "?",
                    "Change ContentType?", JOptionPane.YES_NO_OPTION);
            if(contentTypeYesNo == JOptionPane.YES_OPTION){
                jd_body_content_type.setContentType(contentType);
                if(charset != null){ // is XML file
                    jd_body_content_type.setCharSet(charset);
                }
            }
        }
        // Only the charset has changed:
        else if((charset != null) && (!oldCharset.equals(charset))){
            int charsetYesNo = JOptionPane.showConfirmDialog(view,
                    "Change Charset To: " + charset + "?",
                    "Change Charset?", JOptionPane.YES_NO_OPTION);
            if(charsetYesNo == JOptionPane.YES_OPTION){
                jd_body_content_type.setCharSet(charset);
            }
        }
        // Get text from file and set
        try{
            String body = FileUtil.getContentAsString(f, Charsets.UTF_8);
            se_req_body.setText(body);
            se_req_body.setCaretPosition(0);
        }
        catch(IOException ex){
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    "IO Error: " + ex.getMessage(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            jd_body_content_type.setContentType(oldContentType);
            jd_body_content_type.setCharSet(oldCharset);
        }
    }
    
    private void jb_body_paramActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        checkAndSetParameterContentType();
        jd_req_paramDialog.setLocationRelativeTo(rest_ui.getFrame());
        jd_req_paramDialog.setVisible(true);
    }
    
    private boolean canSetReqBodyText(){
        if(StringUtil.isEmpty(se_req_body.getText())){
            return true;
        }
        else{
            int response = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                    "Body text exists. Erase?",
                    "Erase?",
                    JOptionPane.YES_NO_OPTION);
            if(response == JOptionPane.YES_OPTION){
                return true;
            }
        }
        return false;
    }
    
    
    private void checkAndSetParameterContentType(){
        if(!jd_body_content_type.getContentType().equals(BodyContentTypeDialog.PARAM_CONTENT_TYPE)
                || !jd_body_content_type.getCharSet().equals(BodyContentTypeDialog.PARAM_CHARSET)){
            int status = JOptionPane.showConfirmDialog(rest_ui.getFrame(),
                    "<html>For parameter the Content-type and Charset needs <br>" +
                    "to be `" + BodyContentTypeDialog.PARAM_CONTENT_TYPE +
                    "' and `"+
                    BodyContentTypeDialog.PARAM_CHARSET +
                    "' respectively.<br>"+
                    "Do you want to set this option?</html>",
                    "Parameter Content-type and Charset",
                    JOptionPane.YES_NO_OPTION);
            if(status == JOptionPane.YES_OPTION){
                jd_body_content_type.setContentType(BodyContentTypeDialog.PARAM_CONTENT_TYPE);
                jd_body_content_type.setCharSet(BodyContentTypeDialog.PARAM_CHARSET);
            }
        }
    }
    
    private boolean doesSelectedMethodSupportEntityBody() {
        return jrb_req_post.isSelected()
                || jrb_req_put.isSelected()
                || jrb_req_patch.isSelected()
                || jrb_req_delete.isSelected();
    }
    
    private void setUIReqBodyEnabled(final boolean boo){
        se_req_body.getEditorComponent().setEnabled(boo);
        jb_body_content_type.setEnabled(boo);
        jb_body_file.setEnabled(boo);
        jb_body_params.setEnabled(boo);
    }
    
   
    // Checks if URL starts with http:// or https://
    // If not, appends http:// to the hostname
    // This is just a UI convenience method.
    private void correctRequestURL(){
        String str = (String)jcb_url.getSelectedItem();
        if(StringUtil.isEmpty(str)){
            return;
        }
        else{
            String t = str.toLowerCase();
            if(!(t.startsWith("http://") 
                    || t.startsWith("https://")
                    || t.matches("^[a-z]+://.*"))){
                str = "http://" + str;
                jcb_url.setSelectedItem(str);
            }
        }
    }
    
    private List<String> validateRequest(Request request){
        List<String> errors = new ArrayList<String>();

        // Check URL
        if(request.getUrl() == null){
            errors.add("URL is invalid.");
        }
        
        // Auth check
        final List<HTTPAuthMethod> authMethods = request.getAuthMethods();
        if(!authMethods.isEmpty()){
            // BASIC & DIGEST:
            if(AuthHelper.isBasicOrDigest(authMethods)) {
                if(StringUtil.isEmpty(request.getAuthUsername())){
                    errors.add("Username is empty.");
                }
                if(StringUtil.isEmpty(new String(request.getAuthPassword()))){
                    errors.add("Password is empty.");
                }
            }
            // NTLM:
            if(AuthHelper.isNtlm(authMethods)) {
                if(StringUtil.isEmpty(request.getAuthDomain())){
                    errors.add("Domain is empty.");
                }
                if(StringUtil.isEmpty(request.getAuthWorkstation())){
                    errors.add("Workstation is empty.");
                }
                if(StringUtil.isEmpty(request.getAuthUsername())){
                    errors.add("Username is empty.");
                }
                if(StringUtil.isEmpty(new String(request.getAuthPassword()))){
                    errors.add("Password is empty.");
                }
            }
            // OAuth2 Bearer
            if(AuthHelper.isBearer(authMethods)) {
                if(StringUtil.isEmpty(request.getAuthBearerToken())) {
                    errors.add("OAuth2 Bearer Token is empty.");
                }
            }
        }
        
        // Req Entity check
        final HTTPMethod METHOD = request.getMethod();
        if(METHOD == HTTPMethod.POST || METHOD == HTTPMethod.PUT){
            // Get request body
            ReqEntityString reBean = (ReqEntityString) request.getBody();
            if(reBean != null){
                String req_body = reBean.getBody();
                if(!StringUtil.isEmpty(req_body)){
                    String req_content_type = reBean.getContentType();
                    String req_char_set = reBean.getCharset();
                    if(StringUtil.isEmpty(req_content_type)
                            || StringUtil.isEmpty(req_char_set)){
                        errors.add("Body content is set, but `Content-type' and/or `Char-set' not set.");
                    }
                }
            }
        }
        return errors;
    }
    
    void clearUIRequest(){
        // URL
        jcb_url.setSelectedItem(null);
        
        // Method
        jrb_req_get.setSelected(true);
        
        // Headers
        jp_2col_req_headers.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Cookies
        jp_2col_req_cookies.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Body
        jd_body_content_type.setContentType(BodyContentTypeDialog.DEFAULT_CONTENT_TYPE);
        jd_body_content_type.setCharSet(BodyContentTypeDialog.DEFAULT_CHARSET);
        se_req_body.setText("");
        setUIReqBodyEnabled(false);
        
        // Auth
        jcb_auth_types.setSelectedItem(AuthHelper.NONE);
        jcb_auth_preemptive.setSelected(true);
        jtf_auth_host.setText("");
        jtf_auth_realm.setText("");
        jtf_auth_username.setText("");
        jpf_auth_password.setText("");
        jtf_auth_ntlm_username.setText("");
        jpf_auth_ntlm_password.setText("");
        jtf_auth_domain.setText("");
        jtf_auth_workstation.setText("");
        jtf_auth_bearer_token.setText("");
        
        // SSL
        jtf_ssl_truststore_file.setText("");
        jpf_ssl_truststore_pwd.setText("");
        jcb_ssl_hostname_verifier.setSelectedIndex(0);
        jcb_ssl_trust_self_signed_cert.setSelected(false);
        
        // HTTP version
        jcb_http_version.setSelectedItem(HTTPVersion.getDefault());
        
        // Follow HTTP redirects
        jcb_followRedirects.setSelected(true);
        
        // Ignore response body
        jcb_ignoreResponseBody.setSelected(false);
        
        // Script
        se_test_script.setText("");
    }
    
    void setUIFromResponse(final Response response){
        // Clear first
        clearUIResponse();

        // Response status line
        jtf_res_status.setText(response.getStatusLine());

        // Response header
        resHeaderTableModel.setHeaders(response.getHeaders());

        // Response body
        //// Set the unindentedResponseBody:
        unindentedResponseBody = response.getResponseBody();
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        String indentStr = options.getProperty("response.body.indent");
        boolean indent = indentStr==null? false: (indentStr.equals("true")? true: false);
        if(indent){
            boolean isXml = false;
            boolean isJson = false;
            MultiValueMap<String, String> headers = response.getHeaders();
            for(String key: headers.keySet()){
                if("content-type".equalsIgnoreCase(key)){
                    for(String v: headers.get(key)) {
                        final String contentType = Util.getMimeFromContentType(v);
                        if(ContentTypeUtil.isXmlContentType(contentType)){
                            isXml = true;
                        }
                        else if(ContentTypeUtil.isJsonContentType(contentType)){
                            isJson = true;
                        }
                        break;
                    }
                }
            }
            final String responseBody = response.getResponseBody();
            if(isXml){
                try{
                    String indentedResponseBody = XMLUtil.indentXML(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(IOException ex){
                    setStatusMessage("XML indentation failed.");
                    LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
                catch(XMLException ex){
                    setStatusMessage("XML indentation failed.");
                    LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
            }
            else if(isJson){
                try{
                    String indentedResponseBody = JSONUtil.indentJSON(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(JSONUtil.JSONParseException ex){
                    setStatusMessage("JSON indentation failed.");
                    LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
            }
            else{
                setStatusMessage("Response body neither XML nor JSON. No indentation.");
                se_response.setText(responseBody);
            }
        }
        else{
            se_response.setText(response.getResponseBody());
        }
        se_response.setCaretPosition(0);

        // Response test result
        jp_testResultPanel.setTestResult(response.getTestResult());
    }
    
    void setUIFromRequest(final Request request){
        // Clear first
        clearUIRequest();

        // URL
        jcb_url.setSelectedItem(request.getUrl().toString());

        // Method
        final HTTPMethod reqMethod = request.getMethod();
        switch(reqMethod){
            case GET:
                jrb_req_get.setSelected(true);
                break;
            case POST:
                jrb_req_post.setSelected(true);
                break;
            case PUT:
                jrb_req_put.setSelected(true);
                break;
            case PATCH:
                jrb_req_patch.setSelected(true);
                break;
            case DELETE:
                jrb_req_delete.setSelected(true);
                break;
            case HEAD:
                jrb_req_head.setSelected(true);
                break;
            case OPTIONS:
                jrb_req_options.setSelected(true);
                break;
            case TRACE:
                jrb_req_trace.setSelected(true);
                break;
        }

        // Headers
        MultiValueMap<String, String> headers = request.getHeaders();
        jp_2col_req_headers.getTableModel().setData(headers);
        
        // Cookies
        List<HttpCookie> cookies = request.getCookies();
        MultiValueMap<String, String> cookiesMap = new MultiValueMapArrayList<String, String>();
        for(HttpCookie cookie: cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        jp_2col_req_cookies.getTableModel().setData(cookiesMap);

        // Body
        ReqEntity body = request.getBody();
        if(body != null){
            if(doesSelectedMethodSupportEntityBody()){
                setUIReqBodyEnabled(true);
            }
            jd_body_content_type.setContentType(body.getContentType());
            jd_body_content_type.setCharSet(body.getCharset());
            se_req_body.setText(((ReqEntityString)body).getBody());
            se_req_body.setCaretPosition(0);
        }

        // Authentication
        List<HTTPAuthMethod> authMethods = request.getAuthMethods();
        for(HTTPAuthMethod authMethod: authMethods){
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
        if(AuthHelper.isBasicOrDigest(authMethods)) {
            jcb_auth_preemptive.setSelected(request.isAuthPreemptive());
            jtf_auth_host.setText(StringUtil.getNullStrIfNull(request.getAuthHost()));
            jtf_auth_realm.setText(StringUtil.getNullStrIfNull(request.getAuthRealm()));
            jtf_auth_username.setText(StringUtil.getNullStrIfNull(request.getAuthUsername()));
            if(request.getAuthPassword() != null){
                jpf_auth_password.setText(new String(request.getAuthPassword()));
            }
        }
        if(AuthHelper.isNtlm(authMethods)) {
            jtf_auth_domain.setText(request.getAuthDomain());
            jtf_auth_workstation.setText(request.getAuthWorkstation());
            jtf_auth_ntlm_username.setText(request.getAuthUsername());
            jpf_auth_ntlm_password.setText(new String(request.getAuthPassword()));
        }
        if(AuthHelper.isBearer(authMethods)) {
            jtf_auth_bearer_token.setText(request.getAuthBearerToken());
        }

        // SSL
        String sslTruststore = request.getSslTrustStore();
        char[] sslTruststorePassword = request.getSslTrustStorePassword();
        if(sslTruststore != null){
            jtf_ssl_truststore_file.setText(sslTruststore);
        }
        if(sslTruststorePassword != null){
            jpf_ssl_truststore_pwd.setText(new String(sslTruststorePassword));
        }
        SSLHostnameVerifier sslHostnameVerifier = request.getSslHostNameVerifier();
        if(sslHostnameVerifier != null){
            jcb_ssl_hostname_verifier.setSelectedItem(sslHostnameVerifier);
        }
        jcb_ssl_trust_self_signed_cert.setSelected(request.isSslTrustSelfSignedCert());

        String sslKeystore = request.getSslKeyStore();
        char[] sslKeystorePassword = request.getSslKeyStorePassword();
        if(sslKeystore != null){
        	jtf_ssl_keystore_file.setText(sslKeystore);
        }
        if(sslKeystorePassword != null){
        	jpf_ssl_keystore_pwd.setText(new String(sslKeystorePassword));
        }

        // HTTP Version
        if(request.getHttpVersion() == HTTPVersion.HTTP_1_1){
            jcb_http_version.setSelectedItem(HTTPVersion.HTTP_1_1);
        }
        else if(request.getHttpVersion() == HTTPVersion.HTTP_1_0){
            jcb_http_version.setSelectedItem(HTTPVersion.HTTP_1_0);
        }

        // Follow redirect
        jcb_followRedirects.setSelected(request.isFollowRedirect());
        
        // Ignore response body
        jcb_ignoreResponseBody.setSelected(request.isIgnoreResponseBody());

        // Test script
        se_test_script.setText(request.getTestScript()==null?"":request.getTestScript());
        se_test_script.setCaretPosition(0);
    }
    
    private Calendar statusLastUpdated;
    
    public void setStatusMessage(final String msg){
        jl_status.setText(" " + msg);
        statusLastUpdated = Calendar.getInstance();
    }
    
    public Request getLastRequest() {
        return lastRequest;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
    
    public Font getTextAreaFont(){
        return se_req_body.getEditorComponent().getFont();
    }
    
    public void setTextAreaFont(final Font f){
        se_req_body.getEditorComponent().setFont(f);
        se_response.getEditorComponent().setFont(f);
    }
    
}
