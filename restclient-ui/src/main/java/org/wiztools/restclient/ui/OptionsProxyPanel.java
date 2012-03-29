package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ProxyConfig;
import org.wiztools.restclient.Util;
import org.wiztools.commons.Implementation;
import org.wiztools.commons.StringUtil;

/**
 *
 * @author Subhash
 */
class OptionsProxyPanel extends JPanel implements IOptionsPanel {
    
    private static final Logger LOG = Logger.getLogger(OptionsProxyPanel.class.getName());
    
    private static final String PROP_PREFIX = "proxy.options.";
    
    private JCheckBox jcb_enable = new JCheckBox("Enable");
    private JCheckBox jcb_auth_enable = new JCheckBox("Authentication");
    
    private final int jtf_size = 25;
    private JTextField jtf_host = new JTextField(jtf_size);
    private JTextField jtf_port = new JTextField(jtf_size);
    private JTextField jtf_username = new JTextField(jtf_size);
    private JPasswordField jpf_password = new JPasswordField(jtf_size);
    
    public OptionsProxyPanel(){
        toggleEnable(false);
        
        JPanel jp = this;
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp.setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jcb_enable.setMnemonic('e');
        jcb_enable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jcb_enable.isSelected()){
                    toggleEnable(true);
                }
                else{
                    toggleEnable(false);
                }
            }
        });
        jp_north.add(jcb_enable);
        jcb_auth_enable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jcb_auth_enable.isSelected()){
                    toggleAuthEnable(true);
                }
                else{
                    toggleAuthEnable(false);
                }
            }
        });
        jp_north.add(jcb_auth_enable);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout(RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_center_west.add(new JLabel("Host: "));
        jp_center_west.add(new JLabel("Port: "));
        jp_center_west.add(new JLabel("Username: "));
        jp_center_west.add(new JLabel("Password: "));
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(4, 1, RESTView.BORDER_WIDTH, RESTView.BORDER_WIDTH));
        jp_center_center.add(jtf_host);
        jtf_port.setText("" + ProxyConfig.DEFAULT_PORT);
        jp_center_center.add(jtf_port);
        jp_center_center.add(jtf_username);
        jp_center_center.add(jpf_password);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);
       
    }
    
    private void toggleEnable(final boolean boo){
        jtf_host.setEnabled(boo);
        jtf_port.setEnabled(boo);
        jcb_auth_enable.setEnabled(boo);
        if(!boo){ // if boo is false
            toggleAuthEnable(false);
        }
        else if(boo && jcb_auth_enable.isSelected()){
            toggleAuthEnable(true);
        }
    }
    
    private void toggleAuthEnable(final boolean boo){
        jtf_username.setEnabled(boo);
        jpf_password.setEnabled(boo);
    }
    
    @Override
    public List<String> validateInput(){
        List<String> errors = new ArrayList<String>();
        
        boolean enabled = jcb_enable.isSelected();
        
        if(enabled){
            String host = jtf_host.getText();
            int port = -1;
            try{
                port = Integer.parseInt(jtf_port.getText());
                if(!(0 < port && port < 65536)){
                    throw new IllegalArgumentException("Proxy Port not in valid range.");
                }
            }
            catch(NumberFormatException ex){
                errors.add("Proxy Port is not integer.");
            }
            catch(IllegalArgumentException ex){
                errors.add(ex.getMessage());
            }
            boolean authEnabled = jcb_auth_enable.isSelected();

            String username = jtf_username.getText();
            char[] password = jpf_password.getPassword();
            
            // Validation
            if(StringUtil.isEmpty(host)){
                errors.add("Proxy Host is empty.");
            }
            // port is already validated
            if(authEnabled){
                if(StringUtil.isEmpty(username)){
                    errors.add("Proxy Username is empty.");
                }
                if(password == null || password.length == 0){
                    errors.add("Proxy Password is empty.");
                }
            }
        }
        if(errors.size() == 0){
            return null;
        }
        return errors;
    }
    
    @Override
    public boolean saveOptions(){
        boolean enabled = jcb_enable.isSelected();
        String host = jtf_host.getText();
        int port = -1;
        try{
            port = Integer.parseInt(jtf_port.getText());
            if(!(0 < port && port < 65536)){
                throw new IllegalArgumentException("Port not in valid range.");
            }
        }
        catch(NumberFormatException ex){
            return false;
        }
        catch(IllegalArgumentException ex){
            return false;
        }
        boolean authEnabled = jcb_auth_enable.isSelected();
        
        String username = jtf_username.getText();
        char[] password = jpf_password.getPassword();
        
        // Setting proxy object
        ProxyConfig proxy = ProxyConfig.getInstance();
        proxy.acquire();
        if(enabled){
            proxy.setEnabled(true);
            proxy.setHost(host);
            proxy.setPort(port);
            
            if(authEnabled){
                proxy.setAuthEnabled(true);
                proxy.setUsername(username);
                proxy.setPassword(password);
            }
        }
        else{
            proxy.setEnabled(false);
        }
        proxy.release();
        return true;
    }
    
    private void setUIFromCache(){
        ProxyConfig proxy = ProxyConfig.getInstance();
        
        proxy.acquire();
        jcb_enable.setSelected(proxy.isEnabled());
        jtf_host.setText(proxy.getHost());
        jtf_port.setText("" + proxy.getPort());
        jcb_auth_enable.setSelected(proxy.isAuthEnabled());
        jtf_username.setText(proxy.getUsername());
        if(proxy.getPassword() == null){
            jpf_password.setText("");
        }
        else{
            jpf_password.setText(new String(proxy.getPassword()));
        }
        proxy.release();

        // Disable/enable the interface:
        toggleEnable(proxy.isEnabled());
        // toggleAuthEnable(proxy.isAuthEnabled());
    }
    
    @Override
    public boolean revertOptions(){
        setUIFromCache();
        
        return true;
    }

    @Override
    public void initOptions() {
        IGlobalOptions options = Implementation.of(IGlobalOptions.class);
        ProxyConfig proxy = ProxyConfig.getInstance();
        
        proxy.acquire();
        try{
            proxy.setEnabled(Boolean.valueOf(options.getProperty(PROP_PREFIX + "is_enabled")));
            proxy.setHost(options.getProperty(PROP_PREFIX + "host"));
            proxy.setPort(Integer.parseInt(options.getProperty(PROP_PREFIX + "port")));
            proxy.setAuthEnabled(Boolean.valueOf(options.getProperty(PROP_PREFIX + "is_auth_enabled")));
            proxy.setUsername(options.getProperty(PROP_PREFIX + "username"));
            proxy.setPassword(options.getProperty(PROP_PREFIX + "password").toCharArray());
            setUIFromCache();
        }
        catch(Exception ex){
            LOG.info("Cannot load Proxy options from properties.");
        }
        proxy.release();
    }

    @Override
    public void shutdownOptions() {
        IGlobalOptions options = Implementation.of(IGlobalOptions.class);
        ProxyConfig proxy = ProxyConfig.getInstance();
        
        proxy.acquire();
        options.setProperty(PROP_PREFIX + "is_enabled", String.valueOf(proxy.isEnabled()));
        options.setProperty(PROP_PREFIX + "host", proxy.getHost());
        options.setProperty(PROP_PREFIX + "port", String.valueOf(proxy.getPort()));
        options.setProperty(PROP_PREFIX + "is_auth_enabled", String.valueOf(proxy.isAuthEnabled()));
        options.setProperty(PROP_PREFIX + "username", proxy.getUsername());
        String pwd = proxy.getPassword()==null? "": new String(proxy.getPassword());
        options.setProperty(PROP_PREFIX + "password", pwd);
        proxy.release();
    }
}
