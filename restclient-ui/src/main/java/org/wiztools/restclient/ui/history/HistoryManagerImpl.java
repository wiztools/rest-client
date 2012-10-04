package org.wiztools.restclient.ui.history;

import java.util.LinkedList;
import javax.inject.Singleton;
import org.wiztools.restclient.bean.Request;

/**
 *
 * @author subwiz
 */
@Singleton
public class HistoryManagerImpl implements HistoryManager {
    private int maxCount = DEFAULT_HISTORY_SIZE;
    private int cursor;
    
    private LinkedList<Request> data = new LinkedList<Request>();
    
    @Override
    public void add(Request request) {
        System.out.println("Add called for url: " + request.getUrl());
        if(cursor != 0) { // discard everything newer than cursor position
            for(int i=0; i<cursor; i++) {
                data.removeFirst();
            }
        }
        
        // Add:
        data.addFirst(request);
        
        // Verify if threshold reached:
        if(data.size() > maxCount) {
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
        System.out.println("Cursor: " + cursor);
        System.out.println("Size: " + data.size());
        if(isOldest()) {
            return null;
        }
        return data.get(++cursor);
    }
    
    @Override
    public Request forward() {
        System.out.println("Cursor: " + cursor);
        System.out.println("Size: " + data.size());
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

    @Override
    public String toString() {
        return "HistoryManagerImpl{" + "maxCount=" + maxCount + ", cursor=" + cursor + ", data=" + data + '}';
    }
}
