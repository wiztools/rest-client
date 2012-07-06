package org.wiztools.restclient.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Singleton;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;

/**
 *
 * @author subwiz
 */
@Singleton
class RecentFilesHelper {
    private static final String KEY_RECENT_FILES = "recent.files";
    private static final String KEY_RECENT_FILES_COUNT = "recent.files.count";
    private static final String SPLIT_KEY = ";";
    
    private static final int DEFAULT_RECENT_FILES_COUNT = 10;
    private int recentFilesCount = DEFAULT_RECENT_FILES_COUNT;
    
    private final LinkedList<File> recentFiles = new LinkedList<File>();
    
    private final IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
    
    private static final Logger LOG = Logger.getLogger(RecentFilesHelper.class.getName());

    RecentFilesHelper() {
        final String recentOpenedFilesStr = options.getProperty(KEY_RECENT_FILES);
        if(StringUtil.isNotEmpty(recentOpenedFilesStr)) {
            List<File> l = getListRepresentation(recentOpenedFilesStr);
            recentFiles.addAll(l);
        }
        
        // Load recent files count:
        try {
            recentFilesCount = Integer.parseInt(options.getProperty(KEY_RECENT_FILES_COUNT));
            if(recentFilesCount < DEFAULT_RECENT_FILES_COUNT) {
                recentFilesCount = DEFAULT_RECENT_FILES_COUNT;
            }
        }
        catch(NumberFormatException ex) {
            LOG.warning("Property contains non-numeric value: " + KEY_RECENT_FILES_COUNT);
        }
    }
    
    protected final String getStringRepresentation(LinkedList<File> recentFiles) {
        StringBuilder sb = new StringBuilder();
        for(File file: recentFiles) {
            try {
                sb.append(URLEncoder.encode(
                        file.getAbsolutePath(), Charsets.UTF_8.name()));
                sb.append(SPLIT_KEY);
            }
            catch(UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        if(sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    protected final List<File> getListRepresentation(String recentFilesStr) {
        if(StringUtil.isEmpty(recentFilesStr)) {
            return Collections.EMPTY_LIST;
        }
        LinkedList<File> out = new LinkedList<File>();
        String[] arr = recentFilesStr.split(SPLIT_KEY);
        for(String str: arr) {
            try{
                out.addLast(new File(URLDecoder.decode(str, Charsets.UTF_8.name())));
            }
            catch(UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return out;
    }
    
    void openedFile(File f) {
        // Verify and remove if the same file is already in the list:
        recentFiles.remove(f);
        
        // Now, add:
        recentFiles.addFirst(f);
        
        // Remove the least recently used file from list:
        if(recentFiles.size() == 11) { // store only 10 recent files!
            recentFiles.removeLast();
        }
    }
    
    List<File> getRecentFiles() {
        return Collections.unmodifiableList(recentFiles);
    }
    
    boolean isEmpty() {
        return recentFiles.isEmpty();
    }
    
    void clear() {
        recentFiles.clear();
    }
    
    void store() {
        if(recentFiles.isEmpty()) {
            options.removeProperty(KEY_RECENT_FILES);
        }
        else {
            options.setProperty(KEY_RECENT_FILES, getStringRepresentation(recentFiles));
        }
        options.setProperty(KEY_RECENT_FILES_COUNT, String.valueOf(recentFilesCount));
    }
}
