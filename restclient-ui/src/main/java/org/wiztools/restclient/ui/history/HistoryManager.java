package org.wiztools.restclient.ui.history;

import com.google.inject.ImplementedBy;
import java.io.File;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.bean.Request;

/**
 *
 * @author subwiz
 */
@ImplementedBy(HistoryManagerImpl.class)
public interface HistoryManager {
    
    int DEFAULT_HISTORY_SIZE = 30;
    File DEFAULT_FILE = new File(IGlobalOptions.CONF_DIR, "history.xml");
    
    void setHistorySize(int size);
    int getHistorySize();
    
    void add(Request request);

    Request back();
    Request forward();
    Request current();
    Request lastRequest();
    
    boolean isMostRecent();
    boolean isOldest();

    void clear();
}
