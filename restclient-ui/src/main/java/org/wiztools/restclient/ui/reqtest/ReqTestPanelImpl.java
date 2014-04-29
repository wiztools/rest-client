package org.wiztools.restclient.ui.reqtest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import junit.framework.TestSuite;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.TestUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.ui.*;
import org.wiztools.restclient.ui.dnd.DndAction;
import org.wiztools.restclient.ui.dnd.FileDropTargetListener;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class ReqTestPanelImpl extends JPanel implements ReqTestPanel {
    
    @Inject private RESTView view;
    @Inject private RESTUserInterface rest_ui;
    
    @Inject private RunTestDialog jd_runTestDialog;
    
    private final ScriptEditor se_test_script = ScriptEditorFactory.getGroovyScriptEditor();
    private final JButton jb_req_test_template = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_template.png"));
    private final JButton jb_req_test_open = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private final JButton jb_req_test_run = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "wand.png"));
    private final JButton jb_req_test_quick = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "quick_test.png"));
    
    // Load templateTestScript:
    private static final String templateTestScript;
    static{
        InputStream is = RESTView.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/test-script.template");
        String t = null;
        try{
            StringBuilder sb;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String str = null;
                sb = new StringBuilder();
                while((str = br.readLine()) != null){
                    sb.append(str).append("\n");
                }
            }
            t = sb.toString();
        }
        catch(IOException ex){
            assert true: "Failed loading `test-script.template'!";
        }
        templateTestScript = t;
    }
    
    @PostConstruct
    protected void init() {
        // Test script panel
        setLayout(new BorderLayout());
        
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
                loadFile();
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
                if(view.getLastRequest() == null || view.getLastResponse() == null){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Last Request/Response", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String testScript = se_test_script.getText();
                if(StringUtil.isEmpty(testScript)){
                    JOptionPane.showMessageDialog(rest_ui.getFrame(), "No Script", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                runClonedRequestTest(view.getLastRequest(), view.getLastResponse());
            }
        });
        jp_test_north.add(jb_req_test_quick);
        add(jp_test_north, BorderLayout.NORTH);
        
        JScrollPane jsp = new JScrollPane(se_test_script.getEditorView());
        add(jsp, BorderLayout.CENTER);
        
        // DnD:
        FileDropTargetListener l = new FileDropTargetListener();
        l.addDndAction(new DndAction() {
            @Override
            public void onDrop(List<File> files) {
                loadFile(files.get(0));
            }
        });
        new DropTarget(se_test_script.getEditorView(), l);
        new DropTarget(jb_req_test_open, l);
    }
    
    private void loadFile() {
        String str = se_test_script.getText();
        if(!StringUtil.isEmpty(str)){
            int ret = JOptionPane.showConfirmDialog(rest_ui.getFrame(), "Script already exists. Erase?", "Erase existing script?", JOptionPane.YES_NO_OPTION);
            if(ret == JOptionPane.NO_OPTION){
                return;
            }
        }
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_TEST_SCRIPT);
        loadFile(f);
    }
    
    private void loadFile(File f) {
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
            view.showError(Util.getStackTrace(ex));
        }
    }
    
    @Override
    public void runClonedRequestTest(Request request, Response response) {
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

    @Override
    public void clear() {
        se_test_script.setText("");
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTestScript() {
        return se_test_script.getText();
    }

    @Override
    public void setTestScript(String script) {
        se_test_script.setText(script);
        se_test_script.setCaretPosition(0);
    }
    
}
