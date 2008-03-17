/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Subhash
 */
public final class GlobalOptions {
    
    public static final Integer DEFAULT_TIMEOUT_MILLIS = new Integer(60000);
    
    private static GlobalOptions me;
    Lock lock = new ReentrantLock();
    
    private int requestTimeoutInMillis;

    public int getRequestTimeoutInMillis() {
        return requestTimeoutInMillis;
    }

    public void setRequestTimeoutInMillis(int requestTimeoutInMillis) {
        this.requestTimeoutInMillis = requestTimeoutInMillis;
    }
    
    private GlobalOptions(){
        
    }
    
    public void acquire(){
        lock.lock();
    }
    
    public void release(){
        lock.unlock();
    }
    
    public static GlobalOptions getInstance(){
        if(me == null){
            me = new GlobalOptions();
        }
        return me;
    }
    
    

}
