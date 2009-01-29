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
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        if(type.equals(FileType.REQUEST_EXT)){
            if(path.endsWith(FileType.REQUEST_EXT)){
                return true;
            }
        }
        else if(type.equals(FileType.RESPONSE_EXT)){
            if(path.endsWith(FileType.RESPONSE_EXT)){
                return true;
            }
        }
        else if(type.equals(FileType.ARCHIVE_EXT)){
            if(path.endsWith(FileType.ARCHIVE_EXT)){
                return true;
            }
        }
        return false;
    }

    @Override
    public final String getDescription() {
        String description = null;
        if(type.equals(FileType.REQUEST_EXT)){
            description = "Request";
        }
        else if(type.equals(FileType.RESPONSE_EXT)){
            description = "Response";
        }
        else if(type.equals(FileType.ARCHIVE_EXT)){
            description = "Archive";
        }
        return description;
    }

    public String getFileTypeExt(){
        return this.type;
    }
}
