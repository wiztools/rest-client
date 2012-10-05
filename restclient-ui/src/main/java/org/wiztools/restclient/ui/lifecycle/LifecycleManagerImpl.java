package org.wiztools.restclient.ui.lifecycle;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

/**
 *
 * @author subwiz
 */
@Singleton
public class LifecycleManagerImpl implements LifecycleManager {
    
    private final List<Startup> startupListeners = new ArrayList<Startup>();
    private final List<Shutdown> shutdownListeners = new ArrayList<Shutdown>();
    
    @PostConstruct
    protected void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                for(Shutdown s: shutdownListeners) {
                    s.onShutdown();
                }
            }
            
        });
    }
    
    @Override
    public void registerStartupListener(Startup startupListener) {
        startupListeners.add(startupListener);
    }
    
    @Override
    public void registerShutdownListener(Shutdown shutdownListener) {
        shutdownListeners.add(shutdownListener);
    }
    
    @Override
    public void runStartupListeners() {
        for(Startup s: startupListeners) {
            s.onStartup();
        }
    }
}
