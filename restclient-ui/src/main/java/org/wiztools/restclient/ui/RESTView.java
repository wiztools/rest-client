package org.wiztools.restclient.ui;

import com.jidesoft.swing.AutoCompletion;
import org.wiztools.restclient.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import junit.framework.TestSuite;
import org.wiztools.restclient.Implementation;
import org.wiztools.restclient.TestException;
import org.wiztools.restclient.TestResult;
import org.wiztools.restclient.TestUtil;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.XMLUtil;

/**
 *
 * @author Subhash
 */
class RESTView extends JPanel implements View {
    private static final Logger LOG = Logger.getLogger(RESTView.class.getName());
    
    private ImageIcon icon_go = UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png");
    private ImageIcon icon_stop = UIUtil.getIconFromClasspath("org/wiztools/restclient/stop.png");
    
    private JRadioButton jrb_req_get = new JRadioButton("GET");
    private JRadioButton jrb_req_post = new JRadioButton("POST");
    private JRadioButton jrb_req_put = new JRadioButton("PUT");
    private JRadioButton jrb_req_delete = new JRadioButton("DELETE");
    private JRadioButton jrb_req_head = new JRadioButton("HEAD");
    private JRadioButton jrb_req_options = new JRadioButton("OPTIONS");
    private JRadioButton jrb_req_trace = new JRadioButton("TRACE");
    
    private JProgressBar jpb_status = new JProgressBar();
    
    private JLabel jl_status = new JLabel(RCConstants.TITLE);
    private JLabel jl_url = new JLabel("URL: ");
    private JComboBox jcb_url = new JComboBox();
    
    private JButton jb_request = null;
    
    private JTextField jtf_res_status = new JTextField();
    
    private JTextField jtf_body_content_type = new JTextField();
    private ScriptEditor se_req_body = ScriptEditorFactory.getXMLScriptEditor();
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    private BodyContentTypeDialog jd_body_content_type;
    private JScrollPane jsp_req_body;
    private Dimension d_jsp_req_body;
    
    private JScrollPane jsp_test_script;
    private ScriptEditor se_test_script = ScriptEditorFactory.getGroovyScriptEditor();
    private JButton jb_req_test_template = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_template.png"));
    private JButton jb_req_test_open = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_req_test_run = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "wand.png"));
    private JButton jb_req_test_quick = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "quick_test.png"));
    private RunTestDialog jd_runTestDialog;
    
    // Authentication resources
    private JCheckBox jcb_auth_basic = new JCheckBox("BASIC");
    private JCheckBox jcb_auth_digest = new JCheckBox("DIGEST");
    private JCheckBox jcb_auth_preemptive = new JCheckBox("Preemptive");
    private JLabel jl_auth_host = new JLabel("<html>Host: </html>");
    private JLabel jl_auth_realm = new JLabel("<html>Realm: </html>");
    private JLabel jl_auth_username = new JLabel("<html>Username: <font color=red>*</font></html>");
    private JLabel jl_auth_password = new JLabel("<html>Password: <font color=red>*</font></html>");
    private static final int auth_text_size = 20;
    private JTextField jtf_auth_host = new JTextField(auth_text_size);
    private JTextField jtf_auth_realm = new JTextField(auth_text_size);
    private JTextField jtf_auth_username = new JTextField(auth_text_size);
    private JPasswordField jpf_auth_password = new JPasswordField(auth_text_size);
    
    // SSL
    private JTextField jtf_ssl_truststore_file = new JTextField(auth_text_size);
    private JButton jb_ssl_browse = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JPasswordField jpf_ssl_truststore_pwd = new JPasswordField(auth_text_size);
    private JComboBox jcb_ssl_hostname_verifier = new JComboBox(SSLHostnameVerifier.getAll());
    
    // HTTP Version Combo box
    JComboBox jcb_http_version = new JComboBox(HTTPVersion.values());

    // Auto-redirect switch:
    JCheckBox jcb_auto_redirect = new JCheckBox("Auto-redirect");
    
    // Response
    private JScrollPane jsp_res_body = new JScrollPane();
    private ScriptEditor se_response = ScriptEditorFactory.getXMLScriptEditor();
    
    private JTable jt_res_headers = new JTable();
    
    //private JScrollPane jsp_test_result;
    //private JTextArea jta_test_result = new JTextArea();
    private TestResultPanel jp_testResultPanel = new TestResultPanel();

    private TwoColumnTablePanel jp_2col_req_headers;
    
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
        
        // Start status clear thread:
        statusLastUpdated = Calendar.getInstance();
        new StatusClearerThread().start();
    }
    
    private JTabbedPane initJTPRequest(){
        JTabbedPane jtp = new JTabbedPane();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb_req_get);
        bg.add(jrb_req_post);
        bg.add(jrb_req_put);
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
            public void actionPerformed(ActionEvent arg0) {
                if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
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
        jp_method.add(jrb_req_delete);
        jp_method.add(jrb_req_head);
        jp_method.add(jrb_req_options);
        jp_method.add(jrb_req_trace);
        jp_method_encp.add(jp_method);
        jtp.addTab("Method", jp_method_encp);
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(new String[]{"Header", "Value"}, rest_ui);
        jtp.addTab("Headers", jp_2col_req_headers);
        
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
            public void actionPerformed(ActionEvent arg0) {
                jd_body_content_type.setVisible(true);
            }
        });
        jp_body_north.add(jb_body_content_type);
        
        JSeparator js = new JSeparator(SwingConstants.VERTICAL);
        jp_body_north.add(js);
        
        jb_body_file.setToolTipText("Load from file");
        jb_body_file.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jb_body_fileActionPerformed(event);
            }
        });
        jp_body_north.add(jb_body_file);
        
        jb_body_params.setToolTipText("Insert parameters");
        jb_body_params.addActionListener(new ActionListener() {
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
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        JMenuItem jmi_syntax_xml = new JMenuItem("XML");
        jmi_syntax_xml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_req_body, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        
        jpm_req_body.add(jm_syntax);
        
        if (se_req_body.getEditorView() instanceof JEditorPane) {
            se_req_body.getEditorView().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                private void showPopup(final MouseEvent e) {
                    if(!se_req_body.getEditorView().isEnabled()){
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
        jsp_req_body = new JScrollPane(se_req_body.getEditorView());
        jp_body_center.add(jsp_req_body);
        jp_body.add(jp_body_center, BorderLayout.CENTER);
        jtp.addTab("Body", jp_body);
        
        // Auth Tab
        JPanel jp_auth = new JPanel();
        jp_auth.setLayout(new BorderLayout());
        JPanel jp_auth_west = new JPanel();
        jp_auth_west.setLayout(new BorderLayout());
        JPanel jp_auth_west_center = new JPanel();
        jp_auth_west_center.setBorder(BorderFactory.createTitledBorder("Auth Type"));
        jp_auth_west_center.setLayout(new GridLayout(2,1));
        jcb_auth_basic.setSelected(false);
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                auth_enableActionPerformed(event);
            }
        };
        jcb_auth_basic.addActionListener(action);
        jcb_auth_digest.addActionListener(action);
        jp_auth_west_center.add(jcb_auth_basic);
        jp_auth_west_center.add(jcb_auth_digest);
        jp_auth_west.add(jp_auth_west_center, BorderLayout.CENTER);
        JPanel jp_auth_west_south = new JPanel();
        jp_auth_west_south.setBorder(BorderFactory.createTitledBorder("Preemptive?"));
        jp_auth_west_south.setLayout(new GridLayout(1, 1));
        jcb_auth_preemptive.setToolTipText("Send authentication credentials before challenge");
        jcb_auth_preemptive.setSelected(true);
        jcb_auth_preemptive.setEnabled(false);
        jp_auth_west_south.add(jcb_auth_preemptive);
        jp_auth_west.add(jp_auth_west_south, BorderLayout.SOUTH);
        jp_auth.add(jp_auth_west, BorderLayout.WEST);
        JPanel jp_auth_center = new JPanel();
        jp_auth_center.setBorder(BorderFactory.createTitledBorder("Details"));
        jp_auth_center.setLayout(new BorderLayout());
        JPanel jp_auth_center_west = new JPanel();
        jp_auth_center_west.setLayout(new GridLayout(4, 1, BORDER_WIDTH, BORDER_WIDTH));
        jl_auth_host.setEnabled(false);
        jl_auth_realm.setEnabled(false);
        jl_auth_username.setEnabled(false);
        jl_auth_password.setEnabled(false);
        jp_auth_center_west.add(jl_auth_host);
        jp_auth_center_west.add(jl_auth_realm);
        jp_auth_center_west.add(jl_auth_username);
        jp_auth_center_west.add(jl_auth_password);
        jp_auth_center.add(jp_auth_center_west, BorderLayout.WEST);
        JPanel jp_auth_center_center = new JPanel();
        jp_auth_center_center.setLayout(new GridLayout(4, 1, BORDER_WIDTH, BORDER_WIDTH));
        jtf_auth_host.setEnabled(false);
        jtf_auth_realm.setEnabled(false);
        jtf_auth_username.setEnabled(false);
        jpf_auth_password.setEnabled(false);
        jp_auth_center_center.add(jtf_auth_host);
        jp_auth_center_center.add(jtf_auth_realm);
        jp_auth_center_center.add(jtf_auth_username);
        jp_auth_center_center.add(jpf_auth_password);
        jp_auth_center.add(jp_auth_center_center, BorderLayout.CENTER);
        jp_auth.add(jp_auth_center, BorderLayout.CENTER);
        JPanel jp_auth_encp = new JPanel();
        jp_auth_encp.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp_auth_encp.add(jp_auth);
        jtp.addTab("Auth", jp_auth_encp);
        
        // SSL Tab
        JPanel jp_ssl = new JPanel();
        jp_ssl.setLayout(new BorderLayout(BORDER_WIDTH, 2));
        // SSL West
        JPanel jp_ssl_west = new JPanel();
        jp_ssl_west.setLayout(new GridLayout(3, 1));
        jp_ssl_west.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Trust store file:")));
        jp_ssl_west.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Trust store password:")));
        jp_ssl_west.add(UIUtil.getFlowLayoutPanelLeftAligned(new JLabel("Hostname verifier:")));
        jp_ssl.add(jp_ssl_west, BorderLayout.WEST);
        // SSL Center
        JPanel jp_ssl_center = new JPanel();
        jp_ssl_center.setLayout(new GridLayout(3, 1));
        JPanel jp_ssl_center_flow = UIUtil.getFlowLayoutPanelLeftAligned(jtf_ssl_truststore_file);
        jb_ssl_browse.setToolTipText("Open truststore file.");
        jb_ssl_browse.addActionListener(new ActionListener() {
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
        jp_ssl_center_flow.add(jb_ssl_browse);
        jp_ssl_center.add(jp_ssl_center_flow);
        jp_ssl_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jpf_ssl_truststore_pwd));
        jp_ssl_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jcb_ssl_hostname_verifier));
        jp_ssl.add(jp_ssl_center, BorderLayout.CENTER);
        jtp.addTab("SSL", UIUtil.getFlowLayoutPanelLeftAligned("SSL Trust Store Configuration", jp_ssl));
        
        // Etc panel
        JPanel jp_etc = new JPanel();
        jp_etc.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel jp_etc_grid = new JPanel(new GridLayout(2, 1));
        {
            // Add HTTP Version
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(new JLabel("HTTP Version: "));
            jp.add(jcb_http_version);
            jp_etc_grid.add(jp);
        }
        {
            // Add auto-redirect:
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jcb_auto_redirect.setSelected(true); // default true for b/ward compatibility
            jcb_auto_redirect.setToolTipText("Auto redirects to redirect-URL when response status 3XX encountered");
            jp.add(jcb_auto_redirect);
            jp_etc_grid.add(jp);
        }
        jp_etc.add(jp_etc_grid);
        jtp.add("Etc.", jp_etc);
        
        // Test script panel
        JPanel jp_test = new JPanel();
        jp_test.setLayout(new BorderLayout());
        
        JPanel jp_test_north = new JPanel();
        jp_test_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jb_req_test_template.setToolTipText("Insert Template");
        jb_req_test_template.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String t = se_test_script.getText();
                if(!Util.isStrEmpty(t)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "Script text already present! Please clear existing script!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Dimension d = jsp_test_script.getPreferredSize();
                se_test_script.setText(templateTestScript);
                se_test_script.setCaretPosition(0);
                jsp_test_script.setPreferredSize(d);
            }
        });
        jp_test_north.add(jb_req_test_template);
        jb_req_test_open.setToolTipText("Open Test Script From File");
        jb_req_test_open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String str = se_test_script.getText();
                if(!Util.isStrEmpty(str)){
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
                    String testScript = Util.getStringFromFile(f);
                    Dimension d = jsp_test_script.getPreferredSize();
                    se_test_script.setText(testScript);
                    se_test_script.setCaretPosition(0);
                    jsp_test_script.setPreferredSize(d);
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
            public void actionPerformed(ActionEvent e) {
                if(Util.isStrEmpty(se_test_script.getText())){
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
            public void actionPerformed(ActionEvent e) {
                if(lastRequest == null || lastResponse == null){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Last Request/Response", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String testScript = se_test_script.getText();
                if(Util.isStrEmpty(testScript)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Script", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                runClonedRequestTest(lastRequest, lastResponse);
            }
        });
        jp_test_north.add(jb_req_test_quick);
        jp_test.add(jp_test_north, BorderLayout.NORTH);
        
        jsp_test_script = new JScrollPane(se_test_script.getEditorView());
        jp_test.add(jsp_test_script, BorderLayout.CENTER);
        jtp.addTab("Test Script", jp_test);
        
        return jtp;
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
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        JMenuItem jmi_syntax_none = new JMenuItem("None");
        jmi_syntax_none.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        
        popupMenu.add(jm_syntax);
        
        // Attach popup menu
        if (se_response.getEditorView() instanceof JEditorPane) {
            se_response.getEditorView().addMouseListener(new MouseAdapter() {
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
        jsp_res_body = new JScrollPane(se_response.getEditorView());
        jp_body.add(jsp_res_body);
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
        
        jcb_url.setToolTipText("URL");
        jcb_url.setEditable(true);
        jcb_url.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_urlActionPerformed(evt);
            }
        });
        // AutoCompleteDecorator.decorate(jcb_url);
        AutoCompletion ac = new AutoCompletion(jcb_url);
        ac.setStrict(false);
        jp_north.add(jcb_url, BorderLayout.CENTER);
        jb_request = new JButton(icon_go);
        jb_request.setToolTipText("Go!");
        jb_request.addActionListener(new ActionListener() {
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
            public void setParameter(final String params) {
                se_req_body.setText(params);
            }
            
        };
        jd_req_paramDialog = new ParameterDialog(rest_ui, pv);
        
        // Initialize jd_body_content_type
        jd_body_content_type = new BodyContentTypeDialog(rest_ui.getFrame());
        jd_body_content_type.addContentTypeCharSetChangeListener(new ContentTypeCharSetChangeListener() {
            public void changed(String contentType, String charSet) {
                jtf_body_content_type.setText(Util.getFormattedContentType(contentType, charSet));
            }
        });
        
        // Set the font of ScriptEditors:
        String fontName = Implementation.of(IGlobalOptions.class).getProperty("font.options.font");
        String fontSizeStr = Implementation.of(IGlobalOptions.class).getProperty("font.options.fontSize");
        int fontSize = 12; // Default font size is 12
        if(fontSizeStr != null){
            try{
                fontSize = Integer.parseInt(fontSizeStr);
            }
            catch(NumberFormatException ex){
                LOG.log(Level.WARNING, "Font size property is not a number: " + fontSizeStr);
            }
        }
        if(fontName != null){
            Font f = new Font(fontName, Font.PLAIN, fontSize);
            se_req_body.getEditorView().setFont(f);
            se_response.getEditorView().setFont(f);
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
        
        if(jcb_auth_basic.isSelected()){
            request.addAuthMethod(HTTPAuthMethod.BASIC);
            authEnabled = true;
        }
        if(jcb_auth_digest.isSelected()){
            request.addAuthMethod(HTTPAuthMethod.DIGEST);
            authEnabled = true;
        }
        
        if(authEnabled){
            // Pass the credentials
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
        else if(jrb_req_delete.isSelected()){
            request.setMethod(HTTPMethod.DELETE);
        }
        else if(jrb_req_options.isSelected()){
            request.setMethod(HTTPMethod.OPTIONS);
        }
        else if(jrb_req_trace.isSelected()){
            request.setMethod(HTTPMethod.TRACE);
        }
        
        // Get request headers
        Object[][] header_data = jp_2col_req_headers.getTableModel().getData();
        if(header_data.length > 0){
            for(int i=0; i<header_data.length; i++){
                String key = (String)header_data[i][0];
                String value = (String)header_data[i][1];
                request.addHeader(key, value);
            }
        }
        
        // EntityEnclosing method specific
        if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
            // Get request body
            String req_body = se_req_body.getText();
            if(!Util.isStrEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                ReqEntityBean body = new ReqEntityBean(req_body,
                        req_content_type,
                        req_char_set);
                request.setBody(body);
            }
        }
        
        // SSL specific
        request.setSslTrustStore(jtf_ssl_truststore_file.getText());
        request.setSslTrustStorePassword(jpf_ssl_truststore_pwd.getPassword());
        request.setSslHostNameVerifier((SSLHostnameVerifier)jcb_ssl_hostname_verifier.getSelectedItem());
        
        // HTTP version
        request.setHttpVersion((HTTPVersion)jcb_http_version.getSelectedItem());

        // Auto-redirect
        request.setAutoRedirect(jcb_auto_redirect.isSelected());
        
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
            if(errors.size() == 0){
                clearUIResponse();
                final RequestExecuter executer = Implementation.of(RequestExecuter.class);
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
            public void run() {
                setStatusMessage("Request cancelled!");
            }
        });
    }
    
    @Override
    public void doEnd(){
        SwingUtilities.invokeLater(new Runnable() {
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
        // Use this to trigger request action on pressing Enter:
        // jb_requestActionPerformed(event);
    }
    
    private void auth_enableActionPerformed(final ActionEvent event){
        if(jcb_auth_basic.isSelected() || jcb_auth_digest.isSelected()){
            setUIReqAuthEnabled(true);
        }
        else{
            setUIReqAuthEnabled(false);
        }
    }
    
    private void setUIReqAuthEnabled(final boolean boo){
        jcb_auth_preemptive.setEnabled(boo);
        jtf_auth_host.setEnabled(boo);
        jtf_auth_realm.setEnabled(boo);
        jtf_auth_username.setEnabled(boo);
        jpf_auth_password.setEnabled(boo);

        // Disable/enable labels:
        jl_auth_host.setEnabled(boo);
        jl_auth_realm.setEnabled(boo);
        jl_auth_username.setEnabled(boo);
        jl_auth_password.setEnabled(boo);
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
        String contentType = Util.getMimeType(f);
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
            String body = Util.getStringFromFile(f);
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
        if(Util.isStrEmpty(se_req_body.getText())){
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
    
    private void setUIReqBodyEnabled(final boolean boo){
        setJTAReqBodyDimension();
                
        se_req_body.getEditorView().setEnabled(boo);
        jb_body_content_type.setEnabled(boo);
        jb_body_file.setEnabled(boo);
        jb_body_params.setEnabled(boo);
    }
    
    private void setJTAReqBodyDimension(){
        // The TextArea was re-drawing to a bigger size
        // when large text was placed. This check is for
        // avoiding that.
        if(d_jsp_req_body == null){
            Dimension d = ((JEditorPane)se_req_body.getEditorView()).getPreferredScrollableViewportSize();
            d_jsp_req_body = d;
        }
        if(jsp_req_body != null){
            jsp_req_body.setPreferredSize(d_jsp_req_body);
        }
    }
    
    // Checks if URL starts with http:// or https://
    // If not, appends http:// to the hostname
    // This is just a UI convenience method.
    private void correctRequestURL(){
        String str = (String)jcb_url.getSelectedItem();
        if(Util.isStrEmpty(str)){
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
        if(request.getAuthMethods().size() > 0){
            if(Util.isStrEmpty(request.getAuthUsername())){
                errors.add("Username is empty.");
            }
            if(Util.isStrEmpty(new String(request.getAuthPassword()))){
                errors.add("Password is empty.");
            }
        }
        
        // Req Entity check
        final HTTPMethod METHOD = request.getMethod();
        if(METHOD == HTTPMethod.POST || METHOD == HTTPMethod.PUT){
            // Get request body
            ReqEntity reBean = request.getBody();
            if(reBean != null){
                String req_body = reBean.getBody();
                if(!Util.isStrEmpty(req_body)){
                    String req_content_type = reBean.getContentType();
                    String req_char_set = reBean.getCharSet();
                    if(Util.isStrEmpty(req_content_type)
                            || Util.isStrEmpty(req_char_set)){
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
        jp_2col_req_headers.getTableModel().setData(Collections.EMPTY_MAP);
        
        // Body
        jd_body_content_type.setContentType(BodyContentTypeDialog.DEFAULT_CONTENT_TYPE);
        jd_body_content_type.setCharSet(BodyContentTypeDialog.DEFAULT_CHARSET);
        se_req_body.setText("");
        setUIReqBodyEnabled(false);
        
        // Auth
        jcb_auth_basic.setSelected(false);
        jcb_auth_digest.setSelected(false);
        jcb_auth_preemptive.setSelected(true);
        jtf_auth_host.setText("");
        jtf_auth_realm.setText("");
        jtf_auth_username.setText("");
        jpf_auth_password.setText("");
        setUIReqAuthEnabled(false);
        
        // SSL
        jtf_ssl_truststore_file.setText("");
        jpf_ssl_truststore_pwd.setText("");
        jcb_ssl_hostname_verifier.setSelectedIndex(0);
        
        // HTTP version
        jcb_http_version.setSelectedItem(HTTPVersion.getDefault());
        
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
        Dimension d = jsp_res_body.getPreferredSize();
        //// Set the unindentedResponseBody:
        unindentedResponseBody = response.getResponseBody();
        IGlobalOptions options = Implementation.of(IGlobalOptions.class);
        String indentStr = options.getProperty("response.body.indent");
        boolean indent = indentStr==null? false: (indentStr.equals("true")? true: false);
        if(indent){
            boolean isXml = false;
            boolean isJson = false;
            Map<String, String> headers = response.getHeaders();
            for(String key: headers.keySet()){
                if("content-type".equalsIgnoreCase(key)){
                    final String contentType = headers.get(key);
                    // We are using startsWith instead of equals
                    // because to match headers like:
                    // Content-type: text/plain; charset=UTF-8
                    if(contentType != null){
                        if(contentType.startsWith("application/xml")
                                || contentType.startsWith("text/xml")){
                            isXml = true;
                        }
                        else if(contentType.startsWith("application/json")){
                            isJson = true;
                        }
                    }
                    break;
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
        jsp_res_body.setPreferredSize(d);
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
        Map<String, String> headers = request.getHeaders();
        jp_2col_req_headers.getTableModel().setData(headers);

        // Body
        ReqEntity body = request.getBody();
        if(body != null){
            if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
                setUIReqBodyEnabled(true);
            }
            jd_body_content_type.setContentType(body.getContentType());
            jd_body_content_type.setCharSet(body.getCharSet());
            se_req_body.setText(body.getBody());
            se_req_body.setCaretPosition(0);
        }

        // Authentication
        List<HTTPAuthMethod> authMethods = request.getAuthMethods();
        if(authMethods.size() > 0){
            setUIReqAuthEnabled(true);
        }
        for(HTTPAuthMethod authMethod: authMethods){
            switch(authMethod){
                case BASIC:
                    jcb_auth_basic.setSelected(true);
                    break;
                case DIGEST:
                    jcb_auth_digest.setSelected(true);
                    break;
            }
        }
        jcb_auth_preemptive.setSelected(request.isAuthPreemptive());
        jtf_auth_host.setText(Util.getNullStrIfNull(request.getAuthHost()));
        jtf_auth_realm.setText(Util.getNullStrIfNull(request.getAuthRealm()));
        jtf_auth_username.setText(Util.getNullStrIfNull(request.getAuthUsername()));
        if(request.getAuthPassword() != null){
            jpf_auth_password.setText(new String(request.getAuthPassword()));
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

        // HTTP Version
        if(request.getHttpVersion() == HTTPVersion.HTTP_1_1){
            jcb_http_version.setSelectedItem(HTTPVersion.HTTP_1_1);
        }
        else if(request.getHttpVersion() == HTTPVersion.HTTP_1_0){
            jcb_http_version.setSelectedItem(HTTPVersion.HTTP_1_0);
        }

        // Auto-redirect:
        jcb_auto_redirect.setSelected(request.isAutoRedirect());

        // Test script
        Dimension d = jsp_test_script.getPreferredSize();
        se_test_script.setText(request.getTestScript()==null?"":request.getTestScript());
        se_test_script.setCaretPosition(0);
        jsp_test_script.setPreferredSize(d);
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
        return se_req_body.getEditorView().getFont();
    }
    
    public void setTextAreaFont(final Font f){
        se_req_body.getEditorView().setFont(f);
        se_response.getEditorView().setFont(f);
    }
    
    private class StatusClearerThread extends Thread{
        @Override
        public void run(){
            while(true){
                try{
                    Thread.sleep(5*1000);
                }
                catch(InterruptedException ex){
                    // Do nothing!
                }
                Calendar c = (Calendar)statusLastUpdated.clone();
                c.add(Calendar.SECOND, 20);
                if(Calendar.getInstance().after(c)){
                    setStatusMessage(RCConstants.TITLE);
                }
            }
        }
    }
}
