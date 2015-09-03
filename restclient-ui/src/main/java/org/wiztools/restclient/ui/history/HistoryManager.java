package org.wiztools.restclient.ui.history;

import com.google.inject.ImplementedBy;
import java.io.File;
import java.io.IOException;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.util.ConfigUtil;

/**
 *
 * @author subwiz
 */
@ImplementedBy(HistoryManagerImpl.class)
public interface HistoryManager {
    
    String HISTORY_SIZE_CONFIG_KEY = "ui.history.size";
    
    int DEFAULT_HISTORY_SIZE = 15;
    File DEFAULT_FILE = ConfigUtil.getConfigFile("history.xml");
    
    void setHistorySize(int size);
    int getHistorySize();
    
    void add(Request request);

    Request back();
    Request forward();
    Request current();
    Request lastRequest();
    
    boolean isMostRecent();
    boolean isOldest();
    
    boolean isEmpty();

    void clear();
    
    void save(File file) throws IOException;
    void load(File file) throws IOException;
}
