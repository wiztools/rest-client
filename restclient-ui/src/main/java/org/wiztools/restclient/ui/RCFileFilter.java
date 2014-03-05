package org.wiztools.restclient.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.wiztools.restclient.FileType;

/**
 *
 * @author Subhash
 */
public class RCFileFilter extends FileFilter {
    
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
        
        switch (type) {
            case FileType.REQUEST_EXT:
                if(path.endsWith(FileType.REQUEST_EXT)){
                    return true;
                }
                break;
            case FileType.RESPONSE_EXT:
                if(path.endsWith(FileType.RESPONSE_EXT)){
                    return true;
                }
                break;
            case FileType.ARCHIVE_EXT:
                if(path.endsWith(FileType.ARCHIVE_EXT)){
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public final String getDescription() {
        String description = null;
        switch (type) {
            case FileType.REQUEST_EXT:
                description = "Request";
                break;
            case FileType.RESPONSE_EXT:
                description = "Response";
                break;
            case FileType.ARCHIVE_EXT:
                description = "Archive";
                break;
        }
        return description;
    }

    public String getFileTypeExt(){
        return this.type;
    }
}
