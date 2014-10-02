package org.wiztools.restclient;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

/**
 *
 * @author Subhash
 */
@Singleton
final public class GlobalOptions implements IGlobalOptions {
    
    private final static Logger LOG = Logger.getLogger(GlobalOptions.class.getName());
    
    public static final Integer DEFAULT_TIMEOUT_MILLIS = 60000;
    private static final File CONF_PROPERTY = new File(CONF_DIR, "rest-client.properties");
    
    private final Properties prop = new Properties();
    
    Lock lock = new ReentrantLock();
    
    private int requestTimeoutInMillis;

    public int getRequestTimeoutInMillis() {
        return requestTimeoutInMillis;
    }

    public void setRequestTimeoutInMillis(int requestTimeoutInMillis) {
        this.requestTimeoutInMillis = requestTimeoutInMillis;
    }
    
    public GlobalOptions() {
        // Load default properties:
        prop.setProperty("request-timeout-in-millis", "60000");

        if(!CONF_DIR.exists()){
            LOG.info("Configuration directory does not exist. Creating...");
            CONF_DIR.mkdir();
        }
        if(CONF_PROPERTY.exists()){
            try{
                prop.load(new FileInputStream(CONF_PROPERTY));
            }
            catch(IOException ex){
                LOG.log(Level.WARNING, "Failed loading default properties!", ex);
            }
        }
        
        // Register shutdownhook to write properties on shutdown:
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                writeProperties();
            }
        }));
    }
    
    @Override
    public void setProperty(String key, String value){
        if(value == null){
            value = "";
        }
        prop.setProperty(key, value);
    }
    
    @Override
    public void removeProperty(String key){
        prop.remove(key);
    }
    
    @Override
    public String getProperty(String key){
        return prop.getProperty(key);
    }

    @Override
    public boolean isPropertyTrue(String key) {
        String value = prop.getProperty(key);
        if(value != null) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }
    
    @Override
    public void writeProperties(){
        try (final OutputStream os = new FileOutputStream(CONF_PROPERTY);) {
            prop.store(os, "RESTClient Properties");
        }
        catch(IOException ex){
            LOG.log(Level.WARNING, "Error writing to properties!", ex);
        }
    }
    
    @Override
    public OptionsLockImpl acquire(){
        return new OptionsLockImpl();
    }
    
    public class OptionsLockImpl implements OptionsLock {
        
        public OptionsLockImpl() {
            lock.lock();
        }

        @Override
        public void close() throws IOException {
            lock.unlock();
        }
        
    }
}
