package org.wiztools.restclient.ui;

import org.wiztools.restclient.util.XMLUtil;
import org.wiztools.restclient.bean.ReqResBean;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.util.Util;
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
    
    static void openRequest(RESTViewImpl view, File f) {
        Exception e = null;
        try{
            Request request = XMLUtil.getRequestFromXMLFile(f);
            view.setUIFromRequest(request);
        }
        catch(IOException | XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void openResponse(final RESTViewImpl view, final File f) {
        Exception e = null;
        try{
            Response response = XMLUtil.getResponseFromXMLFile(f);
            view.setUIFromResponse(response);
        }
        catch(IOException | XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void openArchive(final RESTViewImpl view, final File f) {
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
        catch(IOException | XMLException ex){
            e = ex;
        }
        if(e != null){
            view.showError(Util.getStackTrace(e));
        }
    }
    
    static void open(final RESTViewImpl view, final File f) {
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
            JOptionPane.showMessageDialog(view.getContainer(),
                    "File in unrecognized format!",
                    "File in unrecognized format!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
