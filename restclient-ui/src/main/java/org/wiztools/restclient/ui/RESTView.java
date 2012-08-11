package org.wiztools.restclient.ui;

import com.jidesoft.swing.AutoCompletion;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import junit.framework.TestSuite;
import org.wiztools.commons.*;
import org.wiztools.restclient.*;
import org.wiztools.restclient.ui.reqauth.ReqAuthPanel;
import org.wiztools.restclient.ui.reqauth.ReqSSLPanel;
import org.wiztools.restclient.ui.reqbody.ReqBodyPanel;
import org.wiztools.restclient.ui.reqmethod.ReqMethodPanel;
import org.wiztools.restclient.ui.resbody.ResBodyPanel;
import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
@Singleton
public class RESTView extends JPanel implements View {
    private static final Logger LOG = Logger.getLogger(RESTView.class.getName());
    
    private ImageIcon icon_go = UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png");
    private ImageIcon icon_stop = UIUtil.getIconFromClasspath("org/wiztools/restclient/stop.png");
    
    @Inject private ReqMethodPanel jp_req_method;
    @Inject private ReqBodyPanel jp_req_body;
    @Inject private ReqAuthPanel jp_req_auth;
    @Inject private ReqSSLPanel jp_req_ssl;
    @Inject private ResBodyPanel jp_res_body;
    
    private JProgressBar jpb_status = new JProgressBar();
    
    private JLabel jl_status = new JLabel(RCConstants.TITLE);
    private JLabel jl_url = new JLabel("URL: ");
    private boolean fromKeyboard = false;
    private JComboBox jcb_url = new JComboBox();
    
    private JButton jb_request = null;
    
    private JTextField jtf_res_status = new JTextField();
    
    private ScriptEditor se_test_script = ScriptEditorFactory.getGroovyScriptEditor();
    private JButton jb_req_test_template = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_template.png"));
    private JButton jb_req_test_open = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_req_test_run = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "wand.png"));
    private JButton jb_req_test_quick = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "quick_test.png"));
    private RunTestDialog jd_runTestDialog;
    
    // HTTP Version Combo box
    private JComboBox jcb_http_version = new JComboBox(HTTPVersion.values());

    // Follow redirect
    private JCheckBox jcb_followRedirects = new JCheckBox("Follow HTTP Redirects? ");
    
    // Ignore body
    private JCheckBox jcb_ignoreResponseBody = new JCheckBox("Ignore Response Body? ");
    
    // Response
    
    private JTable jt_res_headers = new JTable();

    //private JScrollPane jsp_test_result;
    //private JTextArea jta_test_result = new JTextArea();
    private TestResultPanel jp_testResultPanel = new TestResultPanel();

    private TwoColumnTablePanel jp_2col_req_headers;
    private TwoColumnTablePanel jp_2col_req_cookies;
    
    private ResponseHeaderTableModel resHeaderTableModel = new ResponseHeaderTableModel();
    
    // Session Details
    SessionFrame sessionFrame = new SessionFrame("RESTClient: Session View");

    private MessageDialog messageDialog;
    private final RESTView view;
    private final RESTUserInterface rest_ui;
    
    public static final int BORDER_WIDTH = 5;

    // RequestThread
    private Thread requestThread;
    
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
    protected RESTView(final RESTUserInterface ui) {
        this.rest_ui = ui;
        // init();
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
        
        jtp.addTab("Method", jp_req_method);
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(new String[]{"Header", "Value"}, rest_ui);
        jtp.addTab("Header", jp_2col_req_headers);
        
        // Cookies Tab
        jp_2col_req_cookies = new TwoColumnTablePanel(new String[]{"Cookie", "Value"}, rest_ui);
        jtp.addTab("Cookie", jp_2col_req_cookies);
        
        // Body Tab
        jp_req_body.disableBody(); // disable control by default
        jtp.addTab("Body", jp_req_body);
        
        // Auth
        jtp.addTab("Auth", jp_req_auth);
        
        // SSL Tab
        jtp.addTab("SSL", jp_req_ssl);
        
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
        jtp.addTab("Body", jp_res_body);
        
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
    
    @PostConstruct
    private void init(){
        // Initialize the messageDialog
        messageDialog = new MessageDialog(rest_ui.getFrame());
        
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
            // se_req_body.getEditorComponent().setFont(f); TODO
            jp_res_body.setEditorFont(f);
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
        response.setResponseBody(jp_res_body.getBody());
        String statusLine = jtf_res_status.getText();
        response.setStatusLine(statusLine);
        response.setStatusCode(HttpUtil.getStatusCodeFromStatusLine(statusLine));
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
        
        if(jp_req_auth.isAuthSelected()) {
            authEnabled = true;
        }
        
        final String authSelected = jp_req_auth.getAuthMethod();
        if(authEnabled) {
            if(AuthHelper.isBasic(authSelected)){
                request.addAuthMethod(HTTPAuthMethod.BASIC);
            }
            else if(AuthHelper.isDigest(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.DIGEST);
            }
            else if(AuthHelper.isNtlm(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.NTLM);
                
                String domain = jp_req_auth.getDomain();
                String workstation = jp_req_auth.getWorkstation();
                String uid = jp_req_auth.getUsername();
                char[] pwd = jp_req_auth.getPassword();

                request.setAuthDomain(domain);
                request.setAuthWorkstation(workstation);
                request.setAuthUsername(uid);
                request.setAuthPassword(pwd);
            }
            else if(AuthHelper.isBearer(authSelected)) {
                request.addAuthMethod(HTTPAuthMethod.OAUTH_20_BEARER);
                
                request.setAuthBearerToken(jp_req_auth.getBearerToken());
            }
            
            if(AuthHelper.isBasicOrDigest(authSelected)){ // BASIC or DIGEST:
                String uid = jp_req_auth.getUsername();
                char[] pwd = jp_req_auth.getPassword();

                String realm = jp_req_auth.getRealm();
                String host = jp_req_auth.getHost();
                boolean preemptive = jp_req_auth.isPreemptive();

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
        
        // Method
        HTTPMethod method = jp_req_method.getSelectedMethod();
        request.setMethod(method);
        
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
        if(jp_req_method.doesSelectedMethodSupportEntityBody()){
            // Get request body
            request.setBody(jp_req_body.getEntity());
        }
        
        // SSL specific
        request.setSslTrustStore(jp_req_ssl.getTrustStoreFile());
        request.setSslTrustStorePassword(jp_req_ssl.getTrustStorePassword());
        request.setSslKeyStore(jp_req_ssl.getKeyStoreFile());
        request.setSslKeyStorePassword(jp_req_ssl.getKeyStorePassword());
        request.setSslHostNameVerifier((SSLHostnameVerifier)jp_req_ssl.getHostnameVerifier());
        request.setSslTrustSelfSignedCert(jp_req_ssl.isTrustSelfSignedCert());
        
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
        lastResponse = null;
        jtf_res_status.setText("");
        jp_res_body.clearBody();
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
    
    public void enableBody() {
        jp_req_body.enableBody();
    }
    
    public void disableBody() {
        jp_req_body.disableBody();
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
        if(jp_req_method.doesSelectedMethodSupportEntityBody()) {
            ReqEntity entity = jp_req_body.getEntity();
            if(entity instanceof ReqEntitySimple) {
                if(entity != null) {
                    if(entity.getCharset() == null) {
                        errors.add("Charset not set for body.");
                    }
                    if(entity.getContentType() == null) {
                        errors.add("Content type not set for body.");
                    }
                }
            }
        }
        
        return errors;
    }
    
    void clearUIRequest(){
        // Clear last cached request
        lastRequest = null;
        
        // URL
        jcb_url.setSelectedItem(null);
        
        // Method
        jp_req_method.setSelectedMethod(HTTPMethod.GET);
        
        // Headers
        jp_2col_req_headers.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Cookies
        jp_2col_req_cookies.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Body
        jp_req_body.clearBody();
        jp_req_body.disableBody();
        
        // Auth
        jp_req_auth.clear();
        
        // SSL
        jp_req_ssl.clear();
        
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
        jp_res_body.setBody(response.getResponseBody(), response.getContentType());

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
        jp_req_method.setSelectedMethod(reqMethod);

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
            if(jp_req_method.doesSelectedMethodSupportEntityBody()){
                jp_req_body.enableBody();
            }
            jp_req_body.setEntity(body);
        }

        // Authentication
        List<HTTPAuthMethod> authMethods = request.getAuthMethods();
        for(HTTPAuthMethod authMethod: authMethods){
            jp_req_auth.setAuthMethod(authMethod);
        }
        if(AuthHelper.isBasicOrDigest(authMethods)) {
            jp_req_auth.setPreemptive(request.isAuthPreemptive());
            jp_req_auth.setHost(StringUtil.getNullStrIfNull(request.getAuthHost()));
            jp_req_auth.setRealm(StringUtil.getNullStrIfNull(request.getAuthRealm()));
            jp_req_auth.setUsername(StringUtil.getNullStrIfNull(request.getAuthUsername()));
            if(request.getAuthPassword() != null){
                jp_req_auth.setPassword(new String(request.getAuthPassword()));
            }
        }
        if(AuthHelper.isNtlm(authMethods)) {
            jp_req_auth.setDomain(request.getAuthDomain());
            jp_req_auth.setWorkstation(request.getAuthWorkstation());
            jp_req_auth.setNtlmUsername(request.getAuthUsername());
            jp_req_auth.setNtlmPassword(new String(request.getAuthPassword()));
        }
        if(AuthHelper.isBearer(authMethods)) {
            jp_req_auth.setBearerToken(request.getAuthBearerToken());
        }

        // SSL
        String sslTruststore = request.getSslTrustStore();
        char[] sslTruststorePassword = request.getSslTrustStorePassword();
        if(sslTruststore != null){
            jp_req_ssl.setTrustStoreFile(sslTruststore);
        }
        if(sslTruststorePassword != null){
            jp_req_ssl.setTrustStorePassword(new String(sslTruststorePassword));
        }
        SSLHostnameVerifier sslHostnameVerifier = request.getSslHostNameVerifier();
        if(sslHostnameVerifier != null){
            jp_req_ssl.setHostnameVerifier(sslHostnameVerifier);
        }
        jp_req_ssl.setTrustSelfSignedCert(request.isSslTrustSelfSignedCert());

        String sslKeystore = request.getSslKeyStore();
        char[] sslKeystorePassword = request.getSslKeyStorePassword();
        if(sslKeystore != null){
        	jp_req_ssl.setKeyStoreFile(sslKeystore);
        }
        if(sslKeystorePassword != null){
        	jp_req_ssl.setKeyStorePassword(new String(sslKeystorePassword));
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
        // return se_req_body.getEditorComponent().getFont(); TODO
        return null;
    }
    
    public void setTextAreaFont(final Font f){
        // se_req_body.getEditorComponent().setFont(f); TODO
        jp_res_body.setEditorFont(f);
    }
    
}
