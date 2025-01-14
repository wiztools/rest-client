package org.wiztools.restclient.ui;

import java.io.File;
import org.wiztools.filechooser.FileFilter;
import org.wiztools.restclient.FileType;

/**
 *
 * @author Subhash
 */
public class RCFileFilter implements FileFilter {
    
    private final String type;
    public RCFileFilter(String type){
        this.type = type;
    }
    
    @Override
    public final boolean accept(File f) {
        if(f.isDirectory()){
            return true;
        }
        final String path = f.getAbsolutePath().toLowerCase();
        if(FileType.REQUEST_EXT.equals(type) && path.endsWith(FileType.REQUEST_EXT)) {
            return true;
        }
        else if(FileType.RESPONSE_EXT.equals(type) && path.endsWith(FileType.RESPONSE_EXT)) {
            return true;
        }
        else if(FileType.ARCHIVE_EXT.equals(type) && path.endsWith(FileType.ARCHIVE_EXT)) {
            return true;
        }
        return false;
    }

    @Override
    public final String getDescription() {
        return FileType.getNameFromExt(type);
    }

    public String getFileTypeExt(){
        return this.type;
    }
}
