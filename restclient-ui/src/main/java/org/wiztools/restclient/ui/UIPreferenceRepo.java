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
    
    private final LinkedList<String> recentFiles = new LinkedList<String>();

    UIPreferenceRepo() {
        final String recentOpenedFilesStr = prefs.get(KEY_RECENT_FILES, "");
        LinkedList<String> l = getListRepresentation(recentOpenedFilesStr);
        recentFiles.addAll(l);
    }
    
    protected final String getStringRepresentation(LinkedList<String> recentFiles) {
        StringBuilder sb = new StringBuilder();
        for(String file: recentFiles) {
            try {
                sb.append(URLEncoder.encode(file, Charsets.UTF_8.name()));
                sb.append(SPLIT_KEY);
            }
            catch(UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    protected final LinkedList getListRepresentation(String recentFilesStr) {
        LinkedList<String> out = new LinkedList<String>();
        String[] arr = recentFilesStr.split(SPLIT_KEY);
        for(String str: arr) {
            try{
                out.addLast(URLDecoder.decode(str, Charsets.UTF_8.name()));
            }
            catch(UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return out;
    }
    
    void openedFile(File f) {
        recentFiles.addFirst(f.getName() + " -- " + f.getParent());
        if(recentFiles.size() == 11) { // store only 10 recent files!
            recentFiles.removeLast();
        }
    }
    
    List<String> getRecentFiles() {
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
