package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import org.wiztools.restclient.ui.dnd.FileDropTargetListener;
import org.wiztools.restclient.ui.history.HistoryManager;
import org.wiztools.restclient.ui.reqauth.ReqAuthPanel;
import org.wiztools.restclient.ui.reqssl.ReqSSLPanel;
import org.wiztools.restclient.ui.reqbody.ReqBodyPanel;
import org.wiztools.restclient.ui.reqetc.ReqEtcPanel;
import org.wiztools.restclient.ui.reqgo.ReqUrlGoPanel;
import org.wiztools.restclient.ui.reqmethod.ReqMethodPanel;
import org.wiztools.restclient.ui.reqtest.ReqTestPanel;
import org.wiztools.restclient.ui.resbody.ResBodyPanel;
import org.wiztools.restclient.ui.resheader.ResHeaderPanel;
import org.wiztools.restclient.ui.resstats.ResStatsPanel;
import org.wiztools.restclient.ui.resstatus.ResStatusPanel;
import org.wiztools.restclient.ui.restest.ResTestPanel;
import org.wiztools.restclient.util.ContentTypesCommon;
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
    @Inject private ReqUrlGoPanel jp_url_go;
    
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
    @Inject private ResStatusPanel jp_res_status;
    @Inject private ResHeaderPanel jp_res_headers;
    @Inject private ResBodyPanel jp_res_body;
    @Inject private ResTestPanel jp_res_test;
    @Inject private ResStatsPanel jp_res_stats;

    @Inject private MessageDialog messageDialog;
    @Inject private RESTUserInterface rest_ui;
    
    @Inject private HistoryManager historyManager;
    
    private TwoColumnTablePanel jp_2col_req_headers;
    private TwoColumnTablePanel jp_2col_req_cookies;
    
    private RESTView view = this;

    // RequestThread
    private Thread requestThread;
    
    // Cache the last request and response
    private Response lastResponse;
    
    private JTabbedPane initJTPRequest(){
        JTabbedPane jtp = new JTabbedPane();
        
        jtp.addTab("Method", jp_req_method.getComponent());
        
        // Headers Tab
        jp_2col_req_headers = new TwoColumnTablePanel(
                new String[]{"Header", "Value"}, ContentTypesCommon.getCommon(), rest_ui);
        jtp.addTab("Header", jp_2col_req_headers);
        
        // Cookies Tab
        jp_2col_req_cookies = new TwoColumnTablePanel(new String[]{"Cookie", "Value"}, rest_ui);
        jtp.addTab("Cookie", jp_2col_req_cookies);
        
        // Body Tab
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
    
    private JTabbedPane initJTPResponse(){
        JTabbedPane jtp = new JTabbedPane();
        
        // Header Tab
        jtp.addTab("Headers", jp_res_headers.getComponent());
        
        // Response body
        jtp.addTab("Body", jp_res_body.getComponent());
        
        // Test result
        jtp.addTab("Test Result", jp_res_test.getComponent());
        
        // Stats
        jtp.addTab("Stats", jp_res_stats.getComponent());
        
        return jtp;
    }
    
    private JPanel initUIRequest(){
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        jp.setLayout(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        
        // North
        jp_url_go.addActionListener((ActionEvent ae) -> {
            jb_requestActionPerformed();
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
        jp.add(jp_res_status.getComponent(), BorderLayout.NORTH);
        
        // Center having tabs
        jp.add(initJTPResponse(), BorderLayout.CENTER);
        
        jp.setBorder(BorderFactory.createTitledBorder(null, "HTTP Response", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        return jp;
    }
    
    @PostConstruct
    protected void init() {
        // DnD:
        FileDropTargetListener l = new FileDropTargetListener();
        l.addDndAction((List<File> files) -> {
            FileOpenUtil.open(view, files.get(0));
        });
        new DropTarget(this, l);
        
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
        if(historyManager.lastRequest() != null && lastResponse != null){
            setUIFromRequest(historyManager.lastRequest());
            setUIFromResponse(lastResponse);
        }
    }
    
    @Override
    public Response getResponseFromUI(){
        ResponseBean response = new ResponseBean();
        response.setResponseBody(jp_res_body.getBody());
        String statusLine = jp_res_status.getStatus();
        response.setStatusLine(statusLine);
        response.setStatusCode(HttpUtil.getStatusCodeFromStatusLine(statusLine));
        MultiValueMap<String, String> headers = jp_res_headers.getHeaders();
        headers.keySet().stream().forEach((String key) -> {
            headers.get(key).stream().forEach((value) -> {
                response.addHeader(key, value);
            });
        });
        response.setTestResult(jp_res_test.getTestResult());
        response.setExecutionTime(jp_res_stats.getExecutionTime());
        return response;
    }
    
    @Override
    public Request getRequestFromUI() throws IllegalStateException {
        correctRequestURL();
        
        RequestBean request = new RequestBean();
        
        // Auth
        Auth auth = jp_req_auth.getAuth();
        request.setAuth(auth);
        
        String url = jp_url_go.getUrlString();
        try{
            request.setUrl(new URL(url));
        }
        catch(MalformedURLException ex){
            throw new IllegalStateException("URL is malformed", ex);
        }
        
        // Method
        HTTPMethod method = jp_req_method.getSelectedMethod();
        if(StringUtil.isEmpty(method.name())) {
            throw new IllegalStateException("HTTP method name is empty.");
        }
        request.setMethod(method);
        
        { // Get request headers
            MultiValueMap<String, String> headers = jp_2col_req_headers.getData();
            headers.keySet().stream().forEach((key) -> {
                Collection<String> values = headers.get(key);
                values.stream().forEach((value) -> {
                    request.addHeader(key, value);
                });
            });
        }
        
        { // Cookies
            MultiValueMap<String, String> cookies = jp_2col_req_cookies.getData();
            for(final String key: cookies.keySet()) {
                Collection<String> values = cookies.get(key);
                for(final String value: values) {
                    try {
                        HttpCookie cookie = new HttpCookie(key, value);
                        cookie.setVersion(jp_req_etc.getCookieVersion().getIntValue());
                        request.addCookie(cookie);
                    }
                    catch(IllegalArgumentException ex) {
                        throw new IllegalStateException(ex);
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
        if(jp_url_go.isSslUrl()) {
            SSLReq sslReq = jp_req_ssl.getSslReq();
            request.setSslReq(sslReq);
        }
        
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
            try {
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
            catch(IllegalStateException ex) {
                doError(Util.getStackTrace(ex));
            }
        }
        else if(jp_url_go.isRunning()){
            requestThread.interrupt();
            jp_url_go.setAsIdle();
        }
    }                                          

    @Override
    public void doStart(Request request) {
        // Add to history manager:
        historyManager.add(request);
        
        // UI control:
        SwingUtilities.invokeLater(() -> {
            jp_status_bar.showProgressBar();
            jp_status_bar.setStatus("Processing request...");
            jp_url_go.setAsRunning();
        });
    }
    
    @Override
    public void doResponse(final Response response) {
        SwingUtilities.invokeLater(() -> {
            // Update the UI:
            setUIFromResponse(response);
            
            // Set lastResponse:
            lastResponse = response;
            
            // Update status message
            final int bodyLength = response.getResponseBody() != null? response.getResponseBody().length: 0;
            setStatusMessage("Response time: " + response.getExecutionTime() + " ms"
                    + "; body-size: " + bodyLength + " byte(s)");
        });
    }
    
    @Override
    public void doCancelled(){
        SwingUtilities.invokeLater(() -> {
            setStatusMessage("Request cancelled!");
        });
    }
    
    @Override
    public void doEnd(){
        SwingUtilities.invokeLater(() -> {
            jp_status_bar.hideProgressBar();
            jp_url_go.setAsIdle();
        });
    }
    
    @Override
    public void doError(final String error){
        SwingUtilities.invokeLater(() -> {
            showError(error);
            setStatusMessage("An error occurred during request.");
        });
        
    }
    
    @Override
    public void showError(final String error){
        messageDialog.showError(error);
    }
    
    @Override
    public void showError(final Throwable ex){
        messageDialog.showError(Util.getStackTrace(ex));
    }
    
    @Override
    public void showMessage(final String title, final String message){
        messageDialog.showMessage(title, message);
    }
    
    @Override
    public void clearUIResponse(){
        lastResponse = null;
        jp_res_status.clear();
        jp_res_body.clear();
        jp_res_headers.clear();
        jp_res_test.clear();
        jp_res_stats.clear();
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
        if(StringUtil.isNotEmpty(str)) {
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
        List<String> errors = new ArrayList<>();

        // Check URL
        if(request.getUrl() == null){
            errors.add("URL is invalid.");
        }
        
        { // Auth check
            List<String> authErrors = jp_req_auth.validateIfFilled();
            if(!authErrors.isEmpty()) {
                errors.addAll(authErrors);
            }
        }
        
        // Req Entity check
        if(jp_req_method.doesSelectedMethodSupportEntityBody()) {
            ReqEntity entity = jp_req_body.getEntity();
            if(entity instanceof ReqEntitySimple) {
                if(((ReqEntitySimple)entity).getContentType() == null) {
                    errors.add("Content type not set for body.");
                }
            }
        }
        
        return errors;
    }
    
    @Override
    public void clearUIRequest() {
        // URL
        jp_url_go.clear();
        
        // Method
        jp_req_method.clear();
        
        // Headers
        jp_2col_req_headers.setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
        // Cookies
        jp_2col_req_cookies.setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
        
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
        jp_res_status.setStatus(response.getStatusLine());

        // Response header
        jp_res_headers.setHeaders(response.getHeaders());

        // Response body
        if(response.getResponseBody() != null) {
            jp_res_body.setBody(response.getResponseBody(), response.getContentType());
        }

        // Response test result
        jp_res_test.setTestResult(response.getTestResult());
        
        // Stats:
        jp_res_stats.setBodySize(response.getResponseBody().length);
        jp_res_stats.setExecutionTime(response.getExecutionTime());
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
        jp_2col_req_headers.setData(headers);
        
        // Cookies
        List<HttpCookie> cookies = request.getCookies();
        MultiValueMap<String, String> cookiesMap = new MultiValueMapArrayList<>();
        
        int version = CookieVersion.DEFAULT_VERSION.getIntValue();
        for(HttpCookie cookie: cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
            version = cookie.getVersion();
        }
        jp_2col_req_cookies.setData(cookiesMap);
        
        // Cookie version
        jp_req_etc.setCookieVersion(CookieVersion.getValue(version));
        
        // Body
        ReqEntity body = request.getBody();
        if(body != null){
            if(jp_req_method.doesSelectedMethodSupportEntityBody()){
                jp_req_body.enableBody();
            }
            jp_req_body.setEntity(body);
        }

        // Authentication
        if(request.getAuth() != null) {
            jp_req_auth.setAuth(request.getAuth());
        }

        // SSL
        if(request.getSslReq() != null) {
            jp_req_ssl.setSslReq(request.getSslReq());
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
        return historyManager.lastRequest();
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
        ((FontableEditor) jp_res_body).setEditorFont(f);
    }

    @Override
    public void setTextAreaScrollSpeed(final int scrollSpeed) {
        ((ScrollableComponent) jp_req_body).setScrollSpeed(scrollSpeed);
        ((ScrollableComponent) jp_res_body).setScrollSpeed(scrollSpeed);
    }

    @Override
    public Container getContainer() {
        return this;
    }   
}
