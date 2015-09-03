package org.wiztools.restclient.ui.history;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.ui.lifecycle.LifecycleManager;
import org.wiztools.restclient.ui.lifecycle.Shutdown;
import org.wiztools.restclient.ui.reqgo.ReqUrlGoPanel;
import org.wiztools.restclient.persistence.XMLCollectionUtil;

/**
 *
 * @author subwiz
 */
@Singleton
public class HistoryManagerImpl implements HistoryManager {
    private static final Logger LOG = Logger.getLogger(HistoryManagerImpl.class.getName());
    
    private int maxSize = DEFAULT_HISTORY_SIZE; // initialized in @PostConstruct
    private int cursor;
    
    private final LinkedList<Request> data = new LinkedList<>();
    
    @Inject private IGlobalOptions options;
    @Inject private LifecycleManager lifecycle;
    @Inject private ReqUrlGoPanel goPanel;

    @PostConstruct
    protected void init() {
        // Initialize History:
        if(DEFAULT_FILE.exists()) {
            try {
                load(DEFAULT_FILE);
            }
            catch(IOException | XMLException ex) {
                ex.printStackTrace(System.err);
            }
        }
        
        // Add shutdown listener:
        lifecycle.registerShutdownListener(new Shutdown() {
            @Override
            public void onShutdown() {
                try {
                    save(HistoryManager.DEFAULT_FILE);
                }
                catch(IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });
        
        initMaxSize();
    }
    
    // @PostConstruct -- sequence important, so called from init()
    protected void initMaxSize() {
        String tSize = options.getProperty(HISTORY_SIZE_CONFIG_KEY);
        if(tSize != null) {
            try {
                int size = Integer.parseInt(tSize);
                if(size > 0) {
                    if(size > 50) {
                        LOG.info("History size is configured to greater than 50! Ensure you have enough memory!!");
                    }
                    setHistorySize(size);
                    return;
                }
                else {
                    LOG.log(Level.INFO, "History size is less than 1: {0}", size);
                }
            }
            catch(NumberFormatException ex) {
                LOG.info("Expected integer property: " + HISTORY_SIZE_CONFIG_KEY);
            }
        }
        LOG.log(Level.INFO, "Reverting to default value: {0}", DEFAULT_HISTORY_SIZE);
        setHistorySize(DEFAULT_HISTORY_SIZE);
        
        updateOptions();
    }
    
    private void updateOptions() {
        // Update options to persist during shutdown:
        if(options != null) { // should not fail tests!
            options.setProperty(HISTORY_SIZE_CONFIG_KEY, String.valueOf(maxSize));
        }
    }
    
    @Override
    public int getHistorySize() {
        return maxSize;
    }

    @Override
    public void setHistorySize(int size) {
        if(size < 1) {
            throw new IllegalArgumentException("History max size value invalid: " + size);
        }
        
        if(size > 50) { // warning log
            LOG.info("History size is configured to greater than 50! Ensure you have enough memory!!");
        }
        
        if(maxSize == size) {
            return;
        }
        
        if(maxSize > size) { // new size is smaller than existing
            // reset cursor:
            if(cursor >= size) {
                cursor = size - 1;
            }
            
            // Need to trim data?
            if(data.size() > size) {
                final int diff = data.size() - size;
                for(int i=0; i<diff; i++) {
                    data.removeLast();
                }
            }
        }
        
        // Nothing to do if the size is greater than existing!
        
        // Finally, set the size:
        maxSize = size;
        
        updateOptions();
    }
    
    @Override
    public void add(Request request) {
        if(cursor != 0) { // discard everything newer than cursor position
            for(int i=0; i<cursor; i++) {
                data.removeFirst();
            }
        }
        
        // Add:
        data.addFirst(request);
        
        // Verify if threshold reached:
        if(data.size() > maxSize) {
            data.removeLast();
        }
        
        // reset cursor:
        cursor = 0;
    }
    
    @Override
    public boolean isOldest() {
        if(data.isEmpty()) {
            return true;
        }
        return cursor == (data.size() - 1);
    }
    
    @Override
    public boolean isMostRecent() {
        return cursor == 0;
    }
    
    @Override
    public Request back() {
        if(isOldest()) {
            return null;
        }
        return data.get(++cursor);
    }
    
    @Override
    public Request forward() {
        if(isMostRecent()) {
            return null;
        }
        return data.get(--cursor);
    }

    @Override
    public Request current() {
        if(data.isEmpty()) {
            return null;
        }
        return data.get(cursor);
    }
    
    @Override
    public Request lastRequest() {
        if(data.isEmpty()) {
            return null;
        }
        return data.getFirst();
    }
    
    @Override
    public void clear() {
        data.clear();
        cursor = 0;
        
        // remove combo history too:
        goPanel.clearHistory();
    }
    
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public int size() {
        return data.size();
    }
    
    public int cursor() {
        return cursor;
    }
    
    @Override
    public void save(File file) throws IOException {
        if(!data.isEmpty()) {
            XMLCollectionUtil.writeRequestCollectionXML(data, file);
        }
        else {
            LOG.info("No data to store in history");
        }
    }
    
    @Override
    public void load(File file) throws IOException, XMLException, IllegalStateException {
        if(data.isEmpty()) {
            List<Request> requests = XMLCollectionUtil.getRequestCollectionFromXMLFile(file);
            data.addAll(requests);
        }
        else {
            throw new IllegalStateException("History is already initialized. Cannot initialize now.");
        }
    }

    @Override
    public String toString() {
        return "HistoryManagerImpl{" + "maxCount=" + maxSize + ", cursor=" + cursor + ", data=" + data + '}';
    }
}
