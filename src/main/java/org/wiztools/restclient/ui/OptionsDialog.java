package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;

import org.wiztools.restclient.GlobalOptions;
import org.wiztools.restclient.Util;

/**
 *
 * @author schandran
 */
public class OptionsDialog extends EscapableDialog {
    
    private final JFrame frame;
    private final OptionsDialog me;
    
    private OptionsConnectionPanel jp_conn_panel;
    private OptionsProxyPanel jp_proxy_panel;
    
    public OptionsDialog(JFrame f){
        super(f, true);
        frame = f;
        me = this;
        init();
    }
    
    private void init(){
        this.setTitle("Options");
        
        jp_conn_panel = new OptionsConnectionPanel();
        jp_conn_panel.initOptions();
        jp_proxy_panel = new OptionsProxyPanel();
        jp_proxy_panel.initOptions();
        
        // Tabbed pane
        JTabbedPane jtp = new JTabbedPane();
        jtp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        
        jtp.addTab("Connection", UIUtil.getFlowLayoutPanelLeftAligned("Request Timeout", jp_conn_panel));
        jtp.addTab("Proxy", UIUtil.getFlowLayoutPanelLeftAligned(jp_proxy_panel));
        
        // Encapsulating
        JPanel jp_encp = new JPanel();
        jp_encp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_encp.setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_encp.add(jtp, BorderLayout.CENTER);
        
        // South
        JPanel jp_encp_south = new JPanel();
        jp_encp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionOk();
            }
        });
        JButton jb_cancel = new JButton("Cancel");
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionCancel();
            }
        });
        jp_encp_south.add(jb_ok);
        jp_encp_south.add(jb_cancel);
        
        jp_encp.add(jp_encp_south, BorderLayout.SOUTH);
        
        this.setContentPane(jp_encp);
        
        pack();
        
        // Add shutdownhook--so that options are persisted
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                writeProperties();
            }
        });
    }
    
    private void writeProperties(){
        jp_conn_panel.shutdownOptions();
        jp_proxy_panel.shutdownOptions();
        
        GlobalOptions.getInstance().writeProperties();
    }
    
    @Override
    public void doEscape(AWTEvent event) {
        actionCancel();
    }
    
    private void actionOk(){
        List<String> errors = new ArrayList<String>();
        
        List<String> t = jp_conn_panel.validateInput();
        if(t != null){
            errors.addAll(t);
        }
        
        t = jp_proxy_panel.validateInput();
        if(t != null){
            errors.addAll(t);
        }
        
        if(errors.size() > 0){
            final String errStr = Util.getHTMLListFromList(errors);
            JOptionPane.showMessageDialog(me,
                    errStr,
                    "Error in input.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Save the options
        jp_conn_panel.saveOptions();
        jp_proxy_panel.saveOptions();
        
        me.setVisible(false);
    }
    
    private void actionCancel(){
        jp_conn_panel.revertOptions();
        jp_proxy_panel.revertOptions();
        
        // Finally, hide:
        me.setVisible(false);
    }
}
