package org.wiztools.restclient.ui.restest;

import java.awt.*;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.swing.*;
import org.wiztools.restclient.bean.TestExceptionResult;
import org.wiztools.restclient.bean.TestResult;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
class ResTestPanelImpl extends JPanel implements ResTestPanel {
    
    private TestResult lastTestResult;
    
    private final JLabel jl_runCount = new JLabel("");
    private final JLabel jl_failureCount = new JLabel("");
    private final JLabel jl_errorCount = new JLabel("");
    
    private final FailureTableModel tm_failures = new FailureTableModel();
    private final FailureTableModel tm_errors = new FailureTableModel();
    
    private JScrollPane jsp_jt_failures;
    private JScrollPane jsp_jt_errors;
    
    private final JTextArea jta_trace = new JTextArea();
    private JScrollPane jsp_jta_trace;
    
    private final JLabel jl_status = new JLabel();
    
    private final JLabel jl_icon = new JLabel();
    
    private final Icon ICON_DEFAULT = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/eye.png");
    private final Icon ICON_SUCCESS = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/accept.png");
    private final Icon ICON_FAILURE = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/cross.png");
    
    @PostConstruct
    protected void init() {
        JPanel jp = this;
        
        jp.setLayout(new BorderLayout(5, 5));
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new BorderLayout(5, 5));
        jl_icon.setIcon(ICON_DEFAULT);
        jp_north.add(jl_icon, BorderLayout.WEST);
        jp_north.add(jl_status, BorderLayout.CENTER);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JTabbedPane jtp = new JTabbedPane();
        
        JPanel jp_summary = new JPanel();
        jp_summary.setLayout(new GridLayout(3, 1));
        
        // Block to abstract local variables
        {
            JPanel jp_t;
            JLabel jl_t;
            
            // Set the font
            jl_runCount.setFont(UIUtil.FONT_BIG);
            jl_failureCount.setFont(UIUtil.FONT_BIG);
            jl_errorCount.setFont(UIUtil.FONT_BIG);
            
            // Tests Run
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Run: ");
            jl_t.setFont(UIUtil.FONT_BIG);
            jp_t.add(jl_t, BorderLayout.CENTER);
            jp_t.add(jl_runCount, BorderLayout.EAST);
            jp_summary.add(jp_t);
            
            // Tests Failures
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Failures: ");
            jl_t.setFont(UIUtil.FONT_BIG);
            jp_t.add(jl_t, BorderLayout.CENTER);
            jp_t.add(jl_failureCount, BorderLayout.EAST);
            jp_summary.add(jp_t);
            
            // Tests Errors
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Errors: ");
            jl_t.setFont(UIUtil.FONT_BIG);
            jp_t.add(jl_t, BorderLayout.CENTER);
            jp_t.add(jl_errorCount, BorderLayout.EAST);
            jp_summary.add(jp_t);
        }
        
        // Add to the summary tab:
        {
            JPanel jp_t = new JPanel();
            jp_t.setLayout(new FlowLayout(FlowLayout.LEFT));
            jp_t.add(jp_summary);
            jtp.add("Summary", jp_t);
        }
        
        // Add failures tab:
        {
            JTable jt = new JTable(tm_failures);
            Dimension d = jt.getPreferredSize();
            d.height = d.height / 2;
            jt.setPreferredScrollableViewportSize(d);
            jsp_jt_failures = new JScrollPane(jt);
            jtp.add("Failures", jsp_jt_failures);
        }
        
        // Add errors tab:
        {
            JTable jt = new JTable(tm_errors);
            Dimension d = jt.getPreferredSize();
            d.height = d.height / 2;
            jt.setPreferredScrollableViewportSize(d);
            jsp_jt_errors = new JScrollPane(jt);
            jtp.add("Errors", jsp_jt_errors);
        }
        
        // Add trace tab:
        {
            jta_trace.setEditable(false);
            jsp_jta_trace = new JScrollPane(jta_trace);
            jtp.add("Trace", jsp_jta_trace);
        }
        
        jp.add(jtp, BorderLayout.CENTER);
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void setTestResult(TestResult result) {
        if(result == null){
            return;
        }
        lastTestResult = result;
        int runCount = result.getRunCount();
        int failureCount = result.getFailureCount();
        int errorCount = result.getErrorCount();
        
        if(failureCount > 0 || errorCount > 0){
            jl_icon.setIcon(ICON_FAILURE);
        }
        else{
            jl_icon.setIcon(ICON_SUCCESS);
        }
        
        jl_status.setText(
                "Tests run: " + runCount + ", Failures: " 
                + failureCount + ", Errors: " + errorCount);
        
        jl_runCount.setText(String.valueOf(runCount));
        jl_failureCount.setText(String.valueOf(failureCount));
        jl_errorCount.setText(String.valueOf(errorCount));
        
        tm_failures.setData(result.getFailures());
        tm_errors.setData(result.getErrors());
        
        Dimension d = jsp_jta_trace.getPreferredSize();
        jta_trace.setText(result.toString());
        jta_trace.setCaretPosition(0);
        jsp_jta_trace.setPreferredSize(d);
    }
    
    @Override
    public TestResult getTestResult(){
        return lastTestResult;
    }

    @Override
    public void clear() {
        // Clear Summary tab:
        jl_icon.setIcon(ICON_DEFAULT);
        jl_runCount.setText("");
        jl_failureCount.setText("");
        jl_errorCount.setText("");
        jl_status.setText("");
        
        // Clear Failures tab:
        tm_failures.setData(Collections.<TestExceptionResult>emptyList());
        
        // Clear Errors tab:
        tm_errors.setData(Collections.<TestExceptionResult>emptyList());
        
        // Clear trace tab:
        jta_trace.setText("");
    }
    
}
