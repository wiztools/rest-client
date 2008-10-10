/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import org.wiztools.restclient.test.TestFailureResultBean;
import org.wiztools.restclient.test.TestResultBean;

/**
 *
 * @author NEWUSER
 */
public class TestResultPanel extends JPanel {
    
    private JLabel jl_runCount = new JLabel("");
    private JLabel jl_failureCount = new JLabel("");
    private JLabel jl_errorCount = new JLabel("");
    
    private FailureTableModel tm_failures = new FailureTableModel();
    private FailureTableModel tm_errors = new FailureTableModel();
    
    private JScrollPane jsp_jt_failures;
    private JScrollPane jsp_jt_errors;
    
    private JTextArea jta_trace = new JTextArea();
    private JScrollPane jsp_jta_trace;
    
    private JLabel jl_icon = new JLabel();
    
    private Icon ICON_DEFAULT = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/eye.png");
    private Icon ICON_SUCCESS = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/accept.png");
    private Icon ICON_FAILURE = UIUtil.getIconFromClasspath("org/wiztools/restclient/test/cross.png");
    
    private static final Font BOLD_FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
    
    public TestResultPanel(){
        super();
        
        init();
    }
    
    private void init(){
        JPanel jp = this;
        
        jp.setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jl_icon.setIcon(ICON_DEFAULT);
        jp_north.add(jl_icon);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JTabbedPane jtp = new JTabbedPane();
        
        JPanel jp_summary = new JPanel();
        jp_summary.setLayout(new GridLayout(3, 1));
        
        // Block to abstract local variables
        {
            JPanel jp_t;
            JLabel jl_t;
            
            // Set the font
            jl_runCount.setFont(BOLD_FONT);
            jl_failureCount.setFont(BOLD_FONT);
            jl_errorCount.setFont(BOLD_FONT);
            
            // Tests Run
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Run: ");
            jl_t.setFont(BOLD_FONT);
            jp_t.add(jl_t, BorderLayout.CENTER);
            jp_t.add(jl_runCount, BorderLayout.EAST);
            jp_summary.add(jp_t);
            
            // Tests Failures
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Failures: ");
            jl_t.setFont(BOLD_FONT);
            jp_t.add(jl_t, BorderLayout.CENTER);
            jp_t.add(jl_failureCount, BorderLayout.EAST);
            jp_summary.add(jp_t);
            
            // Tests Errors
            jp_t = new JPanel();
            jp_t.setLayout(new BorderLayout());
            jl_t = new JLabel("Tests Errors: ");
            jl_t.setFont(BOLD_FONT);
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
    
    public void clear(){
        jl_icon.setIcon(ICON_DEFAULT);
        jl_runCount.setText("");
        jl_failureCount.setText("");
        jl_errorCount.setText("");
    }
    
    public void setTestResult(TestResultBean result){
        int runCount = result.getRunCount();
        int failureCount = result.getFailureCount();
        int errorCount = result.getErrorCount();
        
        if(failureCount > 0 || errorCount > 0){
            jl_icon.setIcon(ICON_FAILURE);
        }
        else{
            jl_icon.setIcon(ICON_SUCCESS);
        }
        jl_icon.repaint();
        
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
    
    class FailureTableModel extends AbstractTableModel{
        
        private Object[] failures;
        
        public void setData(List<TestFailureResultBean> failures){
            if(failures != null){
                this.failures = failures.toArray();
            }
            fireTableDataChanged();
        }
        
        @Override
        public String getColumnName(int col){
            if(col == 0){
                return "Message";
            }
            else{
                return "Line";
            }
        }

        public int getRowCount() {
            if(failures == null){
                return 0;
            }
            return failures.length;
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            TestFailureResultBean bean = (TestFailureResultBean)failures[rowIndex];
            if(columnIndex == 0){
                return bean.getExceptionMessage();
            }
            else{
                return bean.getLineNumber();
            }
        }
        
    }
}
