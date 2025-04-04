package org.wiztools.restclient.ui;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;
import org.wiztools.restclient.FileType;

/**
 *
 * @author Subhash
 */
public class RCFileView extends FileView {
    @Override
    public String getTypeDescription(final File f) {
        return FileType.getNameFromExt(FileType.getType(f));
    }

    public static final String iconBasePath = "org/wiztools/restclient/";
    public static final Icon FOLDER_ICON = SVGLoad.scaledIcon(iconBasePath + "s_folder.svg");
    public static final Icon FILE_ICON = SVGLoad.scaledIcon(iconBasePath + "s_file.svg");
    public static final Icon REQUEST_ICON = SVGLoad.scaledIcon(iconBasePath + "s_request.svg");
    public static final Icon RESPONSE_ICON = SVGLoad.scaledIcon(iconBasePath + "s_response.svg");
    public static final Icon ARCHIVE_ICON = SVGLoad.scaledIcon(iconBasePath + "s_archive.svg");

    @Override
    public Icon getIcon(final File f){
        Icon icon;

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
