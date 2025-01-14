package org.wiztools.restclient.ui.lifecycle;

import org.wiztools.restclient.ImplementedBy;

/**
 *
 * @author subwiz
 */
@ImplementedBy(LifecycleManagerImpl.class)
public interface LifecycleManager {
    void registerStartupListener(Startup startupListener);
    void registerShutdownListener(Shutdown shutdownListener);
    void runStartupListeners();
}
