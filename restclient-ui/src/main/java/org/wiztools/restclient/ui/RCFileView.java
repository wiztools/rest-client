package org.wiztools.restclient.ui;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;
import org.wiztools.restclient.FileType;

/**
 *
 * @author Subhash
 */
class RCFileView extends FileView {
    @Override
    public String getTypeDescription(final File f){
        String path = f.getAbsolutePath();
        path = path.toLowerCase();
        String type = FileType.getType(f);
        if(FileType.REQUEST_EXT.equals(type)){
            return "Request";
        }
        else if(FileType.RESPONSE_EXT.equals(type)){
            return "Response";
        }
        else if(FileType.ARCHIVE_EXT.equals(type)){
            return "Archive";
        }
        return null;
    }
    
    public static final String iconBasePath = "org/wiztools/restclient/";
    public static final Icon FOLDER_ICON = UIUtil.getIconFromClasspath(iconBasePath + "fv_folder.png");
    public static final Icon FILE_ICON = UIUtil.getIconFromClasspath(iconBasePath + "fv_file.png");
    public static final Icon REQUEST_ICON = UIUtil.getIconFromClasspath(iconBasePath + "fv_request.png");
    public static final Icon RESPONSE_ICON = UIUtil.getIconFromClasspath(iconBasePath + "fv_response.png");
    public static final Icon ARCHIVE_ICON = UIUtil.getIconFromClasspath(iconBasePath + "fv_archive.png");
    
    @Override
    public Icon getIcon(final File f){
        Icon icon = null;
        
        String type = FileType.getType(f);
        if(f.isDirectory()){
            icon = FOLDER_ICON;
        }
        else if(FileType.REQUEST_EXT.equals(type)){
            icon = REQUEST_ICON;
        }
        else if(FileType.RESPONSE_EXT.equals(type)){
            icon = RESPONSE_ICON;
        }
        else if(FileType.ARCHIVE_EXT.equals(type)){
            icon = ARCHIVE_ICON;
        }
        else{
            icon = FILE_ICON;
        }
        return icon;
    }
}
