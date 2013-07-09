package org.wiztools.restclient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author schandran
 */
public final class ProxyConfig {
    
    private static final Logger LOG = Logger.getLogger(ProxyConfig.class.getName());
    
    // Global
    public static final int DEFAULT_PORT = 8080;
    
    private static final String PROP_PREFIX = "proxy.options.";

    // Singleton
    
    private static ProxyConfig _proxy;
    
    public static ProxyConfig getInstance(){
        if(_proxy == null){
            _proxy = new ProxyConfig();
            try {
                _proxy.init();
            }
            catch(Exception ex) {
                LOG.log(Level.INFO, "Cannot load Proxy options from properties.", ex);
            }
        }
        return _proxy;
    }
    
    private void init() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        _proxy.setEnabled(Boolean.valueOf(options.getProperty(PROP_PREFIX + "is_enabled")));
        _proxy.setHost(options.getProperty(PROP_PREFIX + "host"));
        _proxy.setPort(Integer.parseInt(options.getProperty(PROP_PREFIX + "port")));
        _proxy.setAuthEnabled(Boolean.valueOf(options.getProperty(PROP_PREFIX + "is_auth_enabled")));
        _proxy.setUsername(options.getProperty(PROP_PREFIX + "username"));
        _proxy.setPassword(options.getProperty(PROP_PREFIX + "password").toCharArray());
    }
    
    public void write() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        options.setProperty(PROP_PREFIX + "is_enabled", String.valueOf(_proxy.isEnabled()));
        options.setProperty(PROP_PREFIX + "host", _proxy.getHost());
        options.setProperty(PROP_PREFIX + "port", String.valueOf(_proxy.getPort()));
        options.setProperty(PROP_PREFIX + "is_auth_enabled", String.valueOf(_proxy.isAuthEnabled()));
        options.setProperty(PROP_PREFIX + "username", _proxy.getUsername());
        String pwd = _proxy.getPassword()==null? "": new String(_proxy.getPassword());
        options.setProperty(PROP_PREFIX + "password", pwd);
    }
    
    private ProxyConfig(){}
    
    // Lock to ensure consistency
    private final Lock _lck = new ReentrantLock();
    
    public void acquire(){
        _lck.lock();
    }
    
    public void release(){
        _lck.unlock();
    }
    
    // Data
    
    private boolean enabled = false;
    private String host;
    private int port = DEFAULT_PORT;
    private boolean authEnabled = false;
    private String username;
    private char[] password;

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
