package org.wiztools.restclient.ui.history;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.ui.lifecycle.LifecycleManager;
import org.wiztools.restclient.ui.lifecycle.Shutdown;
import org.wiztools.restclient.util.XMLUtil;

/**
 *
 * @author subwiz
 */
@Singleton
public class HistoryManagerImpl implements HistoryManager {
    private int maxSize = DEFAULT_HISTORY_SIZE;
    private int cursor;
    
    private LinkedList<Request> data = new LinkedList<Request>();
    
    @Inject private LifecycleManager lifecycle;

    @PostConstruct
    protected void init() {
        // Initialize History:
        if(DEFAULT_FILE.exists()) {
            try {
                load(DEFAULT_FILE);
            }
            catch(IOException ex) {
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
        if(cursor == (data.size() - 1)) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isMostRecent() {
        if(cursor == 0) {
            return true;
        }
        return false;
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
    }
    
    public int size() {
        return data.size();
    }
    
    public int cursor() {
        return cursor;
    }
    
    private void save(File file) throws IOException {
        XMLUtil.writeRequestCollectionXML(data, file);
    }
    
    private void load(File file) throws IOException {
        if(data.isEmpty()) {
            List<Request> requests = XMLUtil.getRequestCollectionFromXMLFile(file);
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
