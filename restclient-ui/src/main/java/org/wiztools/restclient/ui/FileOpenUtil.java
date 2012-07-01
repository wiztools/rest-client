package org.wiztools.restclient.ui;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.wiztools.restclient.*;

/**
 *
 * @author subwiz
 */
class FileOpenUtil {
    private FileOpenUtil() {}
    
    static void openRequest(RESTView view, File f) {
        Exception e = null;
        try{
            Request request = XMLUtil.getRequestFromXMLFile(f);
            view.setUIFromRequest(request);
        }
        catch(IOException ex){
            e = ex;
        }
        catch(XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void openResponse(final RESTView view, final File f) {
        Exception e = null;
        try{
            Response response = XMLUtil.getResponseFromXMLFile(f);
            view.setUIFromResponse(response);
        }
        catch(IOException ex){
            e = ex;
        }
        catch(XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void openArchive(final RESTView view, final File f) {
        Exception e = null;
        try{
            ReqResBean encp = Util.getReqResArchive(f);
            Request request = encp.getRequestBean();
            Response response = encp.getResponseBean();
            if(request != null && response != null){
                view.setUIFromRequest(request);
                view.setUIFromResponse(response);
            }
            else{
                view.showError("Unable to load archive! Check if valid archive!");
            }
        }
        catch(IOException ex){
            e = ex;
        }
        catch(XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void open(final RESTView view, final File f) {
        final String fileName = f.getName().toLowerCase();
        if(fileName.endsWith(".rcq")) { // Request
            openRequest(view, f);
        }
        else if(fileName.endsWith(".rcs")) { // Response
            openResponse(view, f);
        }
        else if(fileName.endsWith(".rcr")) { // Archive
            openArchive(view, f);
        }
        else {
            JOptionPane.showMessageDialog(view,
                    "File in unrecognized format!",
                    "File in unrecognized format!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
