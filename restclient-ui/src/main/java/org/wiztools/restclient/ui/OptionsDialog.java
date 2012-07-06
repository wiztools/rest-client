package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.Util;

/**
 *
 * @author schandran
 */
class OptionsDialog extends EscapableDialog {
    
    private static final Logger LOG = Logger.getLogger(OptionsDialog.class.getName());
    
    private final OptionsDialog me;
    
    private Map<String, IOptionsPanel> panels = new LinkedHashMap<String, IOptionsPanel>();
    
    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.restclient.uioptionsdialog");
    
    public OptionsDialog(){
        super(ServiceLocator.getInstance(RESTUserInterface.class).getFrame(), true);
        me = this;
        init();
    }
    
    private void init(){
        this.setTitle("Options");
        
        String t = rb.getString("panel");
        String[] arr = t.split(",");
        for(String s: arr){
            String[] arrt = s.split(":");
            try {
                LOG.finest("OptionsPanel adding: " + arrt[1]);
                panels.put(arrt[0], (IOptionsPanel) Class.forName(arrt[1]).newInstance());
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch(InstantiationException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch(IllegalAccessException ex){
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        
        // Call the init of all the panels:
        for(String key: panels.keySet()){
            IOptionsPanel p = panels.get(key);
            p.initOptions();
        }
        
        // Tabbed pane
        JTabbedPane jtp = new JTabbedPane();
        jtp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        
        // Add all to tab:
        for(String key: panels.keySet()){
            jtp.addTab(key, UIUtil.getFlowLayoutPanelLeftAligned((JPanel)panels.get(key)));
        }
        
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
        me.getRootPane().setDefaultButton(jb_ok);
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
        for(String key: panels.keySet()){
            panels.get(key).shutdownOptions();
        }
        
        ServiceLocator.getInstance(IGlobalOptions.class).writeProperties();
    }
    
    @Override
    public void doEscape(AWTEvent event) {
        actionCancel();
    }
    
    private void actionOk(){
        List<String> errors = new ArrayList<String>();
        
        for(String key: panels.keySet()){
            List<String> t = panels.get(key).validateInput();
            if(t != null){
                errors.addAll(t);
            }
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
        for(String key: panels.keySet()){
            panels.get(key).saveOptions();
        }

        me.setVisible(false);
    }
    
    private void actionCancel(){
        // Revert all the options
        for(String key: panels.keySet()){
            panels.get(key).revertOptions();
        }
        
        // Finally, hide:
        me.setVisible(false);
    }
}
