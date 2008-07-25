package org.wiztools.restclient.ui;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.wiztools.restclient.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import junit.framework.TestSuite;
import org.wiztools.restclient.test.TestException;
import org.wiztools.restclient.test.TestUtil;
import org.wiztools.restclient.xml.XMLException;
import org.wiztools.restclient.xml.XMLUtil;
import org.xml.sax.SAXException;

/**
 *
 * @author Subhash
 */
public class RESTView extends JPanel implements View {
    
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
    private JTextArea jta_req_body = new JTextArea();
    private JButton jb_body_content_type = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "edit.png"));
    private JButton jb_body_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private JButton jb_body_params = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
    private BodyContentTypeDialog jd_body_content_type;
    private JScrollPane jsp_req_body;
    private Dimension d_jsp_req_body;
    
    private JScrollPane jsp_test_script;
    private JTextArea jta_test_script = new JTextArea();
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
    
    // Response
    private JScrollPane jsp_res_body = new JScrollPane();
    private JTextArea jta_response = new JTextArea();
    
    private JTable jt_res_headers = new JTable();
    
    private JScrollPane jsp_test_result;
    private JTextArea jta_test_result = new JTextArea();

    private TwoColumnTablePanel jp_2col_req_headers;
    
    private ParameterDialog jd_req_paramDialog;
    
    private ResponseHeaderTableModel resHeaderTableModel = new ResponseHeaderTableModel();
    
    // Session Details
    SessionFrame sessionFrame = new SessionFrame("RESTClient: Session View");

    private MessageDialog messageDialog;
    private final RESTView view;
    private final RESTUserInterface rest_ui;
    
    public static final int BORDER_WIDTH = 5;
    
    // Cache the last request and response
    private RequestBean lastRequest;
    private ResponseBean lastResponse;
    
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
        this.rest_ui = ui;
        init();
        view = this;
        
        // Start status clear thread:
        statusLastUpdated = Calendar.getInstance();
        new StatusClearerThread().start();
    }
    
    private static String getFormattedContentType(final String contentType, final String charset){
        return "Content-Type: " + contentType + "; charset=" + charset;
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
                            setUIReqBodyEnabled(true);
                        }
                        else{
                            setUIReqBodyEnabled(false);
                        }
                    }
                });
                
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
        jtf_body_content_type.setText(getFormattedContentType(
                jd_body_content_type.getContentType(),
                jd_body_content_type.getCharSet()));
        jp_body_north.add(jtf_body_content_type);
    
        jb_body_content_type.setToolTipText("Edit Content-type & Charset");
        jb_body_content_type.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jd_body_content_type.setVisible(true);
                    }
                });
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
        
        jp_body.add(jp_body_north, BorderLayout.NORTH);
        JPanel jp_body_center = new JPanel();
        jp_body_center.setLayout(new GridLayout(1, 1));
        jsp_req_body = new JScrollPane(jta_req_body);
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
        jtp.addTab("Authentication", jp_auth_encp);
        
        // Test script panel
        JPanel jp_test = new JPanel();
        jp_test.setLayout(new BorderLayout());
        
        JPanel jp_test_north = new JPanel();
        jp_test_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jb_req_test_template.setToolTipText("Insert Template");
        jb_req_test_template.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String t = jta_test_script.getText();
                if(!Util.isStrEmpty(t)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "Script text already present! Please clear existing script!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Dimension d = jsp_test_script.getPreferredSize();
                        jta_test_script.setText(templateTestScript);
                        jta_test_script.setCaretPosition(0);
                        jsp_test_script.setPreferredSize(d);
                    }
                });
            }
        });
        jp_test_north.add(jb_req_test_template);
        jb_req_test_open.setToolTipText("Open Test Script From File");
        jb_req_test_open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String str = jta_test_script.getText();
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
                            jta_test_script.setText(testScript);
                            jta_test_script.setCaretPosition(0);
                            jsp_test_script.setPreferredSize(d);
                        }
                        catch(IOException ex){
                            doError(Util.getStackTrace(ex));
                        }
                    }
                });
            }
        });
        jp_test_north.add(jb_req_test_open);
        jp_test_north.add(new JSeparator(JSeparator.VERTICAL));
        jb_req_test_run.setToolTipText("Run Test");
        jb_req_test_run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(Util.isStrEmpty(jta_test_script.getText())){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(),
                            "No script!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(jd_runTestDialog == null){
                            jd_runTestDialog = new RunTestDialog(rest_ui);
                        }
                        jd_runTestDialog.setVisible(true);
                    }
                });
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
                String testScript = jta_test_script.getText();
                if(Util.isStrEmpty(testScript)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Script", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                runClonedRequestTest(lastRequest, lastResponse);
            }
        });
        jp_test_north.add(jb_req_test_quick);
        jp_test.add(jp_test_north, BorderLayout.NORTH);
        
        jsp_test_script = new JScrollPane(jta_test_script);
        jp_test.add(jsp_test_script, BorderLayout.CENTER);
        jtp.addTab("Test Script", jp_test);
        
        return jtp;
    }
    
    void runClonedRequestTest(RequestBean request, ResponseBean response){
        RequestBean t_request = (RequestBean)request.clone();
        t_request.setTestScript(jta_test_script.getText());
        try{
            TestSuite ts = TestUtil.getTestSuite(t_request, response);
            String testResult = TestUtil.execute(ts);
            view.doMessage("Test Result", testResult);
        }
        catch(TestException ex){
            view.doError(Util.getStackTrace(ex));
        }
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
        JMenuItem jmi_indentXml = new JMenuItem("Indent XML");
        jmi_indentXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String resText = jta_response.getText();
                if("".equals(resText.trim())){
                    setStatusMessage("No response body!");
                    return;
                }
                try {
                    final String indentedXML = XMLUtil.indentXML(resText);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jta_response.setText(indentedXML);
                            jta_response.setCaretPosition(0);
                            setStatusMessage("Indent XML: Success");
                        }
                    });
                } catch (ParserConfigurationException ex) {
                    setStatusMessage("Indent XML: XML Parser Configuration Error.");
                } catch (SAXException ex) {
                    setStatusMessage("Indent XML: Not an XML.");
                } catch (IOException ex) {
                    setStatusMessage("Indent XML: IOError while processing XML.");
                } catch (TransformerConfigurationException ex) {
                    setStatusMessage("Indent XML: TransformerConfiguration error.");
                } catch (TransformerException ex) {
                    setStatusMessage("Indent XML: XML transformation error.");
                }
            }
        });
        popupMenu.add(jmi_indentXml);
        
        // Attach popup menu
        jta_response.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            private void showPopup(final MouseEvent e) {
                if("".equals(jta_response.getText().trim())){
                    // No response body
                    return;
                }
                if (e.isPopupTrigger()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    });
                }
            }
        });
        
        JPanel jp_body = new JPanel();
        jp_body.setLayout(new GridLayout(1,1));
        jta_response.setEditable(false);
        jsp_res_body = new JScrollPane(jta_response);
        jp_body.add(jsp_res_body);
        JPanel jp_body_encp = new JPanel();
        jp_body_encp.setBorder(BorderFactory.createEmptyBorder());
        jp_body_encp.setLayout(new GridLayout(1, 1));
        jp_body_encp.add(jp_body);
        jtp.addTab("Body", jp_body_encp);
        
        // Test result
        JPanel jp_test_result = new JPanel();
        jp_test_result.setLayout(new GridLayout(1, 1));
        jta_test_result.setEditable(false);
        jsp_test_result = new JScrollPane(jta_test_result);
        jp_test_result.add(jsp_test_result);
        jtp.addTab("Test Result", jp_test_result);
        
        return jtp;
    }
    
    private JPanel initNorth(){
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
        jp_north.add(jcb_url, BorderLayout.CENTER);
        jb_request = new JButton(UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png"));
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
    
    private JPanel initCenter(){
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
    
    private JPanel initSouth(){
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jta_req_body.setText(params);
                    }
                });
            }
            
        };
        jd_req_paramDialog = new ParameterDialog(rest_ui, pv);
        
        // Initialize jd_body_content_type
        jd_body_content_type = new BodyContentTypeDialog(rest_ui.getFrame());
        jd_body_content_type.addContentTypeCharSetChangeListener(new ContentTypeCharSetChangeListener() {
            public void changed(String contentType, String charSet) {
                jtf_body_content_type.setText(RESTView.getFormattedContentType(contentType, charSet));
            }
        });
        
        this.setLayout(new BorderLayout());
        
        this.add(initNorth(), BorderLayout.NORTH);
        this.add(initCenter(), BorderLayout.CENTER);
        this.add(initSouth(), BorderLayout.SOUTH);
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
    
    ResponseBean getResponseFromUI(){
        ResponseBean response = new ResponseBean();
        response.setResponseBody(jta_response.getText());
        String statusLine = jtf_res_status.getText();
        response.setStatusLine(statusLine);
        response.setStatusCode(Util.getStatusCodeFromStatusLine(statusLine));
        String[][] headers = ((ResponseHeaderTableModel)jt_res_headers.getModel()).getHeaders();
        for(int i=0; i<headers.length; i++){
            response.addHeader(headers[i][0], headers[i][1]);
        }
        String testResult = Util.isStrEmpty(jta_test_result.getText())? null: jta_test_result.getText();
        response.setTestResult(testResult);
        return response;
    }
    
    public RequestBean getRequestFromUI(){
        List<String> errors = validateForRequest();
        if(errors.size()!=0){
            String errStr = Util.getHTMLListFromList(errors);
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                errStr,
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        RequestBean request = new RequestBean();
        boolean authEnabled = false;
        
        if(jcb_auth_basic.isSelected()){
            request.addAuthMethod("BASIC");
            authEnabled = true;
        }
        if(jcb_auth_digest.isSelected()){
            request.addAuthMethod("DIGEST");
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
            assert true: "Should not come here as validation logic checks this.";
        }
        if(jrb_req_get.isSelected()){
            request.setMethod("GET");
        }
        else if(jrb_req_head.isSelected()){
            request.setMethod("HEAD");
        }
        else if(jrb_req_post.isSelected()){
            request.setMethod("POST");
        }
        else if(jrb_req_put.isSelected()){
            request.setMethod("PUT");
        }
        else if(jrb_req_delete.isSelected()){
            request.setMethod("DELETE");
        }
        else if(jrb_req_options.isSelected()){
            request.setMethod("OPTIONS");
        }
        else if(jrb_req_trace.isSelected()){
            request.setMethod("TRACE");
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
            String req_body = jta_req_body.getText();
            if(!Util.isStrEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                ReqEntityBean body = new ReqEntityBean(req_body,
                        req_content_type,
                        req_char_set);
                request.setBody(body);
            }
        }
        
        // Test script specific
        String testScript = jta_test_script.getText();
        testScript = testScript == null || testScript.trim().equals("")?
            null: testScript.trim();
        request.setTestScript(testScript);
        return request;
    }

    private void jb_requestActionPerformed() {                                           
        RequestBean request = getRequestFromUI();
        if(request!=null){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    clearUIResponse();
                }
            });
            new NewHTTPRequestThread(request, view).start();
        }
    }                                          

    @Override
    public void doStart(RequestBean request){
        lastRequest = request;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jpb_status.setVisible(true);
                jb_request.setEnabled(false);
            }
        });
        
        // Update status message
        setStatusMessage("Processing request...");
    }
    
    @Override
    public void doResponse(final ResponseBean response){
        lastResponse = response;
        
        // Update the UI:
        setUIFromResponse(response);
        
        // Update status message
        setStatusMessage("Response received.");
        
        // Update Session View
        if(sessionFrame.isVisible()){
            sessionFrame.getSessionView().add(lastRequest, lastResponse);
        }
    }
    
    @Override
    public void doEnd(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jpb_status.setVisible(false);
                jb_request.setEnabled(true);
            }
        });
    }
    
    @Override
    public void doError(final String error){
        messageDialog.showError(error);
    }
    
    public void doMessage(final String title, final String message){
        messageDialog.showMessage(title, message);
    }
    
    void clearUIResponse(){
        jtf_res_status.setText("");
        jta_response.setText("");
        ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_res_headers.getModel();
        model.setHeaders(null);
        jta_test_result.setText("");
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
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    // System.out.println("Removing and inserting at top");
                    jcb_url.removeItem(item);
                    jcb_url.insertItemAt(item, 0);
                }
            });
        }
        else{ // Add new item
            if(((String)item).trim().length() != 0 ) {
                // The total number of items should not exceed 20
                if(count > 19){
                    // Remove last item to give place
                    // to new one
                    //l.removeLast();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            jcb_url.removeItemAt(count - 1);
                        }
                    });
                }
                //l.addFirst(item);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        // System.out.println("Inserting at top");
                        jcb_url.insertItemAt(item, 0);
                    }
                });
            }
        }
        // Use this to trigger request action on pressing Enter:
        // jb_requestActionPerformed(event);
    }
    
    private void auth_enableActionPerformed(final ActionEvent event){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(jcb_auth_basic.isSelected() || jcb_auth_digest.isSelected()){
                    setUIReqAuthEnabled(true);
                }
                else{
                    setUIReqAuthEnabled(false);
                }
            }
        });
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
                    jta_req_body.setText(body);
                    jta_req_body.setCaretPosition(0);
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
        });
    }
    
    private void jb_body_paramActionPerformed(ActionEvent event){
        if(!canSetReqBodyText()){
            return;
        }
        checkAndSetParameterContentType();
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jd_req_paramDialog.setLocationRelativeTo(rest_ui.getFrame());
                jd_req_paramDialog.setVisible(true);
            }
        });
    }
    
    private boolean canSetReqBodyText(){
        if(Util.isStrEmpty(jta_req_body.getText())){
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jd_body_content_type.setContentType(BodyContentTypeDialog.PARAM_CONTENT_TYPE);
                        jd_body_content_type.setCharSet(BodyContentTypeDialog.PARAM_CHARSET);
                    }
                });
            }
        }
    }
    
    private void setUIReqBodyEnabled(final boolean boo){
        setJTAReqBodyDimension();
                
        jta_req_body.setEnabled(boo);
        jb_body_content_type.setEnabled(boo);
        jb_body_file.setEnabled(boo);
        jb_body_params.setEnabled(boo);
    }
    
    private void setJTAReqBodyDimension(){
        // The TextArea was re-drawing to a bigger size
        // when large text was placed. This check is for
        // avoiding that.
        // This method will be invoked from calls that are running
        // inside SwingUtilities.invokeLater()
        if(d_jsp_req_body == null){
            Dimension d = jta_req_body.getPreferredScrollableViewportSize();
            d_jsp_req_body = d;
        }
        if(jsp_req_body != null){
            jsp_req_body.setPreferredSize(d_jsp_req_body);
        }
    }
    
    private List<String> validateForRequest(){
        List<String> errors = new ArrayList<String>();
        Object o = null;
        String str = null;
        str = (String)jcb_url.getSelectedItem();
        if(Util.isStrEmpty(str)){
            errors.add("URL field is empty.");
        }
        else{
            try{
                new URL(str);
            }
            catch(MalformedURLException ex){
                errors.add("URL is malformed.");
            }
        }
        if(jcb_auth_basic.isSelected() || jcb_auth_digest.isSelected()){
            if(Util.isStrEmpty(jtf_auth_username.getText())){
                errors.add("Username is empty.");
            }
            if(Util.isStrEmpty(new String(jpf_auth_password.getPassword()))){
                errors.add("Password is empty.");
            }
        }
        if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
            // Get request body
            String req_body = jta_req_body.getText();
            if(!Util.isStrEmpty(req_body)){
                String req_content_type = jd_body_content_type.getContentType();
                String req_char_set = jd_body_content_type.getCharSet();
                if(Util.isStrEmpty(req_content_type)
                        || Util.isStrEmpty(req_char_set)){
                    errors.add("Body content is set, but `Content-type' and/or `Char-set' not set.");
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
        jta_req_body.setText("");
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
        
        // Script
        jta_test_script.setText("");
    }
    
    void setUIFromResponse(final ResponseBean response){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Clear first
                clearUIResponse();
                
                // Response status line
                jtf_res_status.setText(response.getStatusLine());
                
                // Response header
                resHeaderTableModel.setHeaders(response.getHeaders());
                
                // Response body
                Dimension d = jsp_res_body.getPreferredSize();
                jta_response.setText(response.getResponseBody());
                jsp_res_body.setPreferredSize(d);
                jta_response.setCaretPosition(0);
                
                // Response test result
                d = jsp_test_result.getPreferredSize();
                jta_test_result.setText(response.getTestResult());
                jsp_test_result.setPreferredSize(d);
                jta_test_result.setCaretPosition(0);
            }
        });
    }
    
    void setUIFromRequest(final RequestBean request){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Clear first
                clearUIRequest();
                
                // URL
                jcb_url.setSelectedItem(request.getUrl().toString());
                
                // Method
                String reqMethod = request.getMethod();
                if("GET".equals(reqMethod)){
                    jrb_req_get.setSelected(true);
                }
                else if("POST".equals(reqMethod)){
                    jrb_req_post.setSelected(true);
                }
                else if("PUT".equals(reqMethod)){
                    jrb_req_put.setSelected(true);
                }
                else if("DELETE".equals(reqMethod)){
                    jrb_req_delete.setSelected(true);
                }
                else if("HEAD".equals(reqMethod)){
                    jrb_req_head.setSelected(true);
                }
                else if("OPTIONS".equals(reqMethod)){
                    jrb_req_options.setSelected(true);
                }
                else if("TRACE".equals(reqMethod)){
                    jrb_req_trace.setSelected(true);
                }
                
                // Headers
                Map<String, String> headers = request.getHeaders();
                jp_2col_req_headers.getTableModel().setData(headers);
                
                // Body
                ReqEntityBean body = request.getBody();
                if(body != null){
                    if(jrb_req_post.isSelected() || jrb_req_put.isSelected()){
                        setUIReqBodyEnabled(true);
                    }
                    jd_body_content_type.setContentType(body.getContentType());
                    jd_body_content_type.setCharSet(body.getCharSet());
                    jta_req_body.setText(body.getBody());
                    jta_req_body.setCaretPosition(0);
                }
                
                // Authentication
                List<String> authMethods = request.getAuthMethods();
                if(authMethods.size() > 0){
                    setUIReqAuthEnabled(true);
                }
                for(String authMethod: authMethods){
                    if("BASIC".equals(authMethod)){
                        jcb_auth_basic.setSelected(true);
                    }
                    else if("DIGEST".equals(authMethod)){
                        jcb_auth_digest.setSelected(true);
                    }
                }
                jcb_auth_preemptive.setSelected(request.isAuthPreemptive());
                jtf_auth_host.setText(Util.getNullStrIfNull(request.getAuthHost()));
                jtf_auth_realm.setText(Util.getNullStrIfNull(request.getAuthRealm()));
                jtf_auth_username.setText(Util.getNullStrIfNull(request.getAuthUsername()));
                if(request.getAuthPassword() != null){
                    jpf_auth_password.setText(new String(request.getAuthPassword()));
                }
                
                // Test script
                Dimension d = jsp_test_script.getPreferredSize();
                jta_test_script.setText(request.getTestScript()==null?"":request.getTestScript());
                jta_test_script.setCaretPosition(0);
                jsp_test_script.setPreferredSize(d);
            }
        });
    }
    
    private Calendar statusLastUpdated;
    
    public void setStatusMessage(final String msg){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jl_status.setText(" " + msg);
                statusLastUpdated = Calendar.getInstance();
            }
        });
    }
    
    public RequestBean getLastRequest() {
        return lastRequest;
    }

    public ResponseBean getLastResponse() {
        return lastResponse;
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
