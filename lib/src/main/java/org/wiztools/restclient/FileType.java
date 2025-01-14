package org.wiztools.restclient;

import java.io.File;

/**
 *
 * @author Subhash
 */
public enum FileType {
    REQUEST, RESPONSE, ARCHIVE, HISTORY;
    
    public static final String REQUEST_EXT = ".rcq";
    public static final String RESPONSE_EXT = ".rcs";
    public static final String ARCHIVE_EXT = ".rcr";
    public static final String HISTORY_EXT = ".rch";
    
    public String getExtension() {
        switch(this) {
            case REQUEST:
                return REQUEST_EXT;
            case RESPONSE:
                return RESPONSE_EXT;
            case ARCHIVE:
                return ARCHIVE_EXT;
            case HISTORY:
                return HISTORY_EXT;
        }
        return null;
    }
    
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
    
    public static File getWithExtension(File f, FileType type) {
        if(f == null) {
            return null;
        }
        if(f.getAbsolutePath().toLowerCase().endsWith(type.getExtension())) {
            return f;
        }
        // Add extension:
        return new File(f.getParent(), f.getName() + type.getExtension());
    }
    
    public static String getNameFromExt(String ext) {
        if(FileType.REQUEST_EXT.equals(ext)) {
            return "Request";
        }
        else if(FileType.RESPONSE_EXT.equals(ext)) {
            return "Response";
        }
        else if(FileType.ARCHIVE_EXT.equals(ext)) {
            return "Archive";
        }
        return null;
    }
}
