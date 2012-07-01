package org.wiztools.restclient.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.wiztools.commons.Charsets;

/**
 *
 * @author subwiz
 */
class UIPreferenceRepo {
    private static final Preferences prefs = Preferences.userNodeForPackage(
            UIPreferenceRepo.class);
    private static final String KEY_RECENT_FILES = "recent.opened.files";
    private static final String SPLIT_KEY = ";";
    
    private final LinkedList<File> recentFiles = new LinkedList<File>();

    UIPreferenceRepo() {
        final String recentOpenedFilesStr = prefs.get(KEY_RECENT_FILES, null);
        if(recentOpenedFilesStr != null) {
            LinkedList<File> l = getListRepresentation(recentOpenedFilesStr);
            recentFiles.addAll(l);
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
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    protected final LinkedList<File> getListRepresentation(String recentFilesStr) {
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
    
    void store() {
        prefs.put(KEY_RECENT_FILES, getStringRepresentation(recentFiles));
        try {
            prefs.flush();
        }
        catch(BackingStoreException ex) {
            throw new RuntimeException(ex);
        }
    }
}
