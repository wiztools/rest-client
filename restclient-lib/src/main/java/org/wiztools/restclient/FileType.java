package org.wiztools.restclient;

import java.io.File;

/**
 *
 * @author Subhash
 */
public class FileType {
    public static final String REQUEST_EXT = ".rcq";
    public static final String RESPONSE_EXT = ".rcs";
    public static final String ARCHIVE_EXT = ".rcr";
    public static final String HISTORY_EXT = ".rch";
    
    public static boolean isRequest(final File f){
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        if(path.endsWith(REQUEST_EXT)){
            return true;
        }
        return false;
    }
    
    public static boolean isResponse(final File f){
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        if(path.endsWith(RESPONSE_EXT)){
            return true;
        }
        return false;
    }
    
    public static boolean isArchive(final File f){
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        if(path.endsWith(ARCHIVE_EXT)){
            return true;
        }
        return false;
    }
    
    public static boolean isHistory(final File f) {
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        if(path.endsWith(HISTORY_EXT)){
            return true;
        }
        return false;
    }
    
    public static String getType(final File f){
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        String type = null;
        if(path.endsWith(REQUEST_EXT)){
            type = REQUEST_EXT;
        }
        else if(path.endsWith(RESPONSE_EXT)){
            type = RESPONSE_EXT;
        }
        else if(path.endsWith(ARCHIVE_EXT)){
            type = ARCHIVE_EXT;
        }
        else if(path.endsWith(HISTORY_EXT)) {
            type = HISTORY_EXT;
        }
        return type;
    }
}
