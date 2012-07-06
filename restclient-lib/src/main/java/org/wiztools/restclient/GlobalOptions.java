package org.wiztools.restclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Subhash
 */
final public class GlobalOptions implements IGlobalOptions {
    
    private final static Logger LOG = Logger.getLogger(GlobalOptions.class.getName());
    
    public static final Integer DEFAULT_TIMEOUT_MILLIS = new Integer(60000);
    private static final File CONF_DIR = new File(
            System.getProperty("user.home") +
            System.getProperty("file.separator") +
            ".rest-client");
    private static final File CONF_PROPERTY = new File(CONF_DIR, "rest-client.properties");
    
    private final Properties prop = new Properties();
    
    private static GlobalOptions me;
    Lock lock = new ReentrantLock();
    
    private int requestTimeoutInMillis;

    public int getRequestTimeoutInMillis() {
        return requestTimeoutInMillis;
    }

    public void setRequestTimeoutInMillis(int requestTimeoutInMillis) {
        this.requestTimeoutInMillis = requestTimeoutInMillis;
    }
    
    public GlobalOptions(){
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
    public void writeProperties(){
        try{
            prop.store(new FileOutputStream(CONF_PROPERTY), "RESTClient Properties");
        }
        catch(IOException ex){
            LOG.log(Level.WARNING, "Error writing to properties!", ex);
        }
    }
    
    @Override
    public void acquire(){
        lock.lock();
    }
    
    @Override
    public void release(){
        lock.unlock();
    }
    
}
