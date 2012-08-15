package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.ui.reqauth.ReqAuthPanel;
import org.wiztools.restclient.ui.reqauth.ReqSSLPanel;
import org.wiztools.restclient.ui.reqbody.ReqBodyPanel;
import org.wiztools.restclient.ui.reqetc.ReqEtcPanel;
import org.wiztools.restclient.ui.reqmethod.ReqMethodPanel;
import org.wiztools.restclient.ui.reqtest.ReqTestPanel;
import org.wiztools.restclient.ui.resbody.ResBodyPanel;
import org.wiztools.restclient.ui.resheader.ResHeaderPanel;
import org.wiztools.restclient.ui.restest.ResTestPanel;
import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
@Singleton
public class RESTViewImpl extends JPanel implements RESTView {
    private static final Logger LOG = Logger.getLogger(RESTViewImpl.class.getName());
    
    // URL go bar:
    @Inject private UrlGoPanel jp_url_go;
    
    // Status bar:
    @Inject private StatusBarPanel jp_status_bar;
    
    // Request panels:
    @Inject private ReqMethodPanel jp_req_method;
    @Inject private ReqBodyPanel jp_req_body;
    @Inject private ReqAuthPanel jp_req_auth;
    @Inject private ReqSSLPanel jp_req_ssl;
    @Inject private ReqEtcPanel jp_req_etc;
    @Inject private ReqTestPanel jp_req_test;
    
    // Response panels:
    @Inject private ResHeaderPanel jp_res_headers;
    @Inject private ResBodyPanel jp_res_body;
    @Inject private ResTestPanel jp_res_test;
    
    private JTextField jtf_res_status = new JTextField();

    private TwoColumnTablePanel jp_2col_req_headers;
    private TwoColumnTablePanel jp_2col_req_cookies;

    private MessageDialog messageDialog;
    private RESTView view = this;
    @Inject private RESTUserInterface rest_ui;

    // RequestThread
    private Thread requestThread;
    
    // Cache the last request and response
    private Request lastRequest;
    private Response lastResponse;
    
    private JTabbedPane initJTPRequest(){
        JTabbedPane jtp = new JTabbedPane();
        
        jtp.addTab("Method", jp_req_method.getComponent());
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(new String[]{"Header", "Value"}, rest_ui);
        jtp.addTab("Header", jp_2col_req_headers);
        
        // Cookies Tab
        jp_2col_req_cookies = new TwoColumnTablePanel(new String[]{"Cookie", "Value"}, rest_ui);
        jtp.addTab("Cookie", jp_2col_req_cookies);
        
        // Body Tab
        jp_req_body.disableBody(); // disable control by default
        jtp.addTab("Body", jp_req_body.getComponent());
        
        // Auth
        jtp.addTab("Auth", jp_req_auth.getComponent());
        
        // SSL Tab
        jtp.addTab("SSL", jp_req_ssl.getComponent());
        
        // Etc panel
        jtp.add("Etc.", jp_req_etc.getComponent());
        
        // Test script panel
        jtp.addTab("Test", jp_req_test.getComponent());
        
        return jtp;
    }
    
    void requestFocusAddressBar() {
        jp_url_go.getComponent().requestFocus();
    }
    
    private JTabbedPane initJTPResponse(){
        JTabbedPane jtp = new JTabbedPane();
        
        // Header Tab
        jtp.addTab("Headers", jp_res_headers.getComponent());
        
        // Response body
        jtp.addTab("Body", jp_res_body.getComponent());
        
        // Test result
        jtp.addTab("Test Result", jp_res_test.getComponent());
        
        return jtp;
    }
    
    private JPanel initUIRequest(){
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        jp.setLayout(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        
        // North
        jp_url_go.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                jb_requestActionPerformed();
            }
        });
        jp.add(jp_url_go.getComponent(), BorderLayout.NORTH);
        
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
    
    @PostConstruct
    private void init(){
        // Initialize the messageDialog
        messageDialog = new MessageDialog(rest_ui.getFrame());
        
        // Set the font of ScriptEditors:
        String fontName = ServiceLocator.getInstance(IGlobalOptions.class)
                .getProperty(FontableEditor.FONT_NAME_PROPERTY);
        String fontSizeStr = ServiceLocator.getInstance(IGlobalOptions.class)
                .getProperty(FontableEditor.FONT_SIZE_PROPERTY);
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
            ((FontableEditor)jp_res_body).setEditorFont(f);
        }
        
        this.setLayout(new BorderLayout());
        
        // Adding the Center portion
        JSplitPane jsp_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp_main.setDividerSize(5);
        jsp_main.add(initUIRequest());
        jsp_main.add(initUIResponse());
        this.add(jsp_main, BorderLayout.CENTER);
        
        // Now the South portion
        this.add(jp_status_bar.getComponent(), BorderLayout.SOUTH);
    }
    
    @Override
    public void setUIToLastRequestResponse(){
        if(lastRequest != null && lastResponse != null){
            setUIFromRequest(lastRequest);
            setUIFromResponse(lastResponse);
        }
    }
    
    @Override
    public Response getResponseFromUI(){
        ResponseBean response = new ResponseBean();
        response.setResponseBody(jp_res_body.getBody());
        String statusLine = jtf_res_status.getText();
        response.setStatusLine(statusLine);
        response.setStatusCode(HttpUtil.getStatusCodeFromStatusLine(statusLine));
        MultiValueMap<String, String> headers = jp_res_headers.getHeaders();
        for(String key: headers.keySet()){
            for(String value: headers.get(key)) {
                response.addHeader(key, value);
            }
        }
        response.setTestResult(jp_res_test.getTestResult());
        return response;
    }
    
    @Override
    public Request getRequestFromUI(){
        correctRequestURL();
        
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
        
        String url = jp_url_go.getUrlString();
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
        request.setHttpVersion(jp_req_etc.getHttpVersion());

        // Follow redirect
        request.setFollowRedirect(jp_req_etc.isFollowRedirects());
        
        // Ignore response body
        request.setIgnoreResponseBody(jp_req_etc.isIgnoreResponseBody());
        
        // Test script specific
        String testScript = jp_req_test.getTestScript();
        testScript = testScript == null || testScript.trim().equals("")?
            null: testScript.trim();
        request.setTestScript(testScript);
        return request;
    }

    private void jb_requestActionPerformed() {
        if(jp_url_go.isIdle()){
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
        else if(jp_url_go.isRunning()){
            requestThread.interrupt();
            jp_url_go.setAsIdle();
        }
    }                                          

    @Override
    public void doStart(Request request){
        lastRequest = request;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jp_status_bar.showProgressBar();
                jp_status_bar.setStatus("Processing request...");
                jp_url_go.setAsRunning();
            }
        });
    }
    
    @Override
    public void doResponse(final Response response) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Update the UI:
                setUIFromResponse(response);
                
                // Set lastResponse:
                lastResponse = response;

                // Update status message
                setStatusMessage("Response received in: " + response.getExecutionTime() + " ms");
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
                jp_status_bar.hideProgressBar();
                jp_url_go.setAsIdle();
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
    
    @Override
    public void showError(final String error){
        messageDialog.showError(error);
    }
    
    @Override
    public void showMessage(final String title, final String message){
        messageDialog.showMessage(title, message);
    }
    
    @Override
    public void clearUIResponse(){
        lastResponse = null;
        jtf_res_status.setText("");
        jp_res_body.clear();
        jp_res_headers.clear();
        jp_res_test.clear();
    }
    
    @Override
    public void enableBody() {
        jp_req_body.enableBody();
    }
    
    @Override
    public void disableBody() {
        jp_req_body.disableBody();
    }
   
    // Checks if URL starts with http:// or https://
    // If not, appends http:// to the hostname
    // This is just a UI convenience method.
    private void correctRequestURL(){
        String str = jp_url_go.getUrlString();
        if(StringUtil.isEmpty(str)){
            return;
        }
        else{
            String t = str.toLowerCase();
            if(!(t.startsWith("http://") 
                    || t.startsWith("https://")
                    || t.matches("^[a-z]+://.*"))){
                str = "http://" + str;
                jp_url_go.setUrlString(str);
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
                    if(((ReqEntitySimple)entity).getContentType().getCharset() == null) {
                        errors.add("Charset not set for body.");
                    }
                    if(((ReqEntitySimple)entity).getContentType().getContentType() == null) {
                        errors.add("Content type not set for body.");
                    }
                }
            }
        }
        
        return errors;
    }
    
    @Override
    public void clearUIRequest(){
        // Clear last cached request
        lastRequest = null;
        
        // URL
        jp_url_go.clear();
        
        // Method
        jp_req_method.clear();
        
        // Headers
        jp_2col_req_headers.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Cookies
        jp_2col_req_cookies.getTableModel().setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Body
        jp_req_body.clear();
        jp_req_body.disableBody();
        
        // Auth
        jp_req_auth.clear();
        
        // SSL
        jp_req_ssl.clear();
        
        // Etc panel
        jp_req_etc.clear();
        
        // Script
        jp_req_test.clear();
    }
    
    @Override
    public void setUIFromResponse(final Response response){
        // Clear first
        clearUIResponse();

        // Response status line
        jtf_res_status.setText(response.getStatusLine());

        // Response header
        jp_res_headers.setHeaders(response.getHeaders());

        // Response body
        jp_res_body.setBody(response.getResponseBody(), response.getContentType());

        // Response test result
        jp_res_test.setTestResult(response.getTestResult());
    }
    
    @Override
    public void setUIFromRequest(final Request request){
        // Clear first
        clearUIRequest();

        // URL
        jp_url_go.setUrlString(request.getUrl().toString());

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
            jp_req_etc.setHttpVersion(HTTPVersion.HTTP_1_1);
        }
        else if(request.getHttpVersion() == HTTPVersion.HTTP_1_0){
            jp_req_etc.setHttpVersion(HTTPVersion.HTTP_1_0);
        }

        // Follow redirect
        jp_req_etc.setFollowRedirects(request.isFollowRedirect());
        
        // Ignore response body
        jp_req_etc.setIgnoreResponseBody(request.isIgnoreResponseBody());

        // Test script
        jp_req_test.setTestScript(request.getTestScript()==null?"":request.getTestScript());
    }
    
    @Override
    public void setStatusMessage(final String msg){
        jp_status_bar.setStatus(msg);
    }
    
    @Override
    public Request getLastRequest() {
        return lastRequest;
    }

    @Override
    public Response getLastResponse() {
        return lastResponse;
    }
    
    @Override
    public String getUrl() {
        return jp_url_go.getUrlString();
    }
    
    @Override
    public void setUrl(String url) {
        jp_url_go.setUrlString(url);
    }
    
    @Override
    public void runClonedRequestTest(Request request, Response response) {
        jp_req_test.runClonedRequestTest(request, response);
    }
    
    @Override
    public Font getTextAreaFont() {
        return ((FontableEditor) jp_req_body).getEditorFont();
    }
    
    @Override
    public void setTextAreaFont(final Font f){
        ((FontableEditor) jp_req_body).setEditorFont(f);
        ((FontableEditor)jp_res_body).setEditorFont(f);
    }

    @Override
    public Container getContainer() {
        return this;
    }   
}
