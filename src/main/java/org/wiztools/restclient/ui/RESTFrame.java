package org.wiztools.restclient.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.wiztools.restclient.FileType;
import org.wiztools.restclient.MessageI18N;
import org.wiztools.restclient.ReqResBean;
import org.wiztools.restclient.RequestBean;
import org.wiztools.restclient.ResponseBean;
import org.wiztools.restclient.Util;
import org.wiztools.restclient.xml.Base64;
import org.wiztools.restclient.xml.XMLException;
import org.wiztools.restclient.xml.XMLUtil;

/**
 *
 * @author schandran
 */
public class RESTFrame extends JFrame {
    
    private RESTView view;
    private AboutDialog aboutDialog;
    private OptionsDialog optionsDialog;
    private PasswordGenDialog passwordGenDialog;
    
    // Requests and responses are generally saved in different dirs
    private JFileChooser jfc_request = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_response = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_generic = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_archive = UIUtil.getNewJFileChooser();
    
    private final RESTFrame me;
    
    public RESTFrame(final String title){
        super(title);
        me = this;
        
        init();
    }
    
    RESTView getView(){
        return view;
    }
    
    private void createMenu(){
        JMenuBar jmb = new JMenuBar();
        
        // File menu
        JMenu jm_file = new JMenu("File");
        jm_file.setMnemonic('f');
        
        JMenuItem jmi_open_req = new JMenuItem("Open Request", RCFileView.REQUEST_ICON);
        jmi_open_req.setMnemonic('o');
        jmi_open_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        jmi_open_req.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                jmi_open_reqAction();
            }
        });
        jm_file.add(jmi_open_req);
        
        JMenuItem jmi_open_res = new JMenuItem("Open Response", RCFileView.RESPONSE_ICON);
        jmi_open_res.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jmi_open_resAction();
            }
        });
        jm_file.add(jmi_open_res);
        
        JMenuItem jmi_open_archive = new JMenuItem("Open Req-Res Archive", RCFileView.ARCHIVE_ICON);
        jmi_open_archive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jmi_open_archiveAction();
            }
        });
        jm_file.add(jmi_open_archive);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_save_req = new JMenuItem("Save Request", RCFileView.REQUEST_ICON);
        jmi_save_req.setMnemonic('q');
        jmi_save_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        jmi_save_req.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionSave(FileChooserType.SAVE_REQUEST);
            }
        });
        jm_file.add(jmi_save_req);
        
        JMenuItem jmi_save_res = new JMenuItem("Save Response", RCFileView.RESPONSE_ICON);
        jmi_save_res.setMnemonic('s');
        jmi_save_res.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionSave(FileChooserType.SAVE_RESPONSE);
            }
        });
        jm_file.add(jmi_save_res);
        
        JMenuItem jmi_save_res_body = new JMenuItem("Save Response Body", RCFileView.FILE_ICON);
        // jmi_save_res_body.setMnemonic(' ');
        jmi_save_res_body.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSave(FileChooserType.SAVE_RESPONSE_BODY);
            }
        });
        jm_file.add(jmi_save_res_body);
        
        JMenuItem jmi_save_archive = new JMenuItem("Save Req-Res Archive", RCFileView.ARCHIVE_ICON);
        jmi_save_archive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSave(FileChooserType.SAVE_ARCHIVE);
            }
        });
        jm_file.add(jmi_save_archive);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_exit = new JMenuItem("Exit", UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "fv_exit.png"));
        jmi_exit.setMnemonic('x');
        jmi_exit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        jmi_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                shutdownCall();
            }
        });
        jm_file.add(jmi_exit);
        
        // Edit menu
        JMenu jm_edit = new JMenu("Edit");
        
        JMenuItem jmi_clear_res = new JMenuItem("Clear Response");
        jmi_clear_res.setMnemonic('c');
        jmi_clear_res.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        view.clearUIResponse();
                    }
                });
            }
        });
        jm_edit.add(jmi_clear_res);
        JMenuItem jmi_reset_all = new JMenuItem("Reset All");
        jmi_reset_all.setMnemonic('a');
        jmi_reset_all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        view.clearUIResponse();
                        view.clearUIRequest();
                    }
                });
            }
        });
        jm_edit.add(jmi_reset_all);
        
        jm_edit.addSeparator();
        
        JMenuItem jmi_reset_to_last = new JMenuItem("Reset to Last Request-Response");
        jmi_reset_to_last.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(view.getLastRequest() != null && view.getLastResponse() != null){
                            view.setUIToLastRequestResponse();
                        }
                        else{
                            JOptionPane.showMessageDialog(me,
                                    "No Last Request-Response Available",
                                    "No Last Request-Response Available",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
            }
        });
        jm_edit.add(jmi_reset_to_last);
        
        // Tools menu
        JMenu jm_tools = new JMenu("Tools");
        
        JMenuItem jmi_pwd_gen = new JMenuItem("Password Encoder/Decoder");
        jmi_pwd_gen.setMnemonic('p');
        jmi_pwd_gen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(passwordGenDialog == null){
                    passwordGenDialog = new PasswordGenDialog(me);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        passwordGenDialog.setVisible(true);
                    }
                });
            }
        });
        jm_tools.add(jmi_pwd_gen);
        
        jm_tools.addSeparator();
        
        JMenuItem jmi_options = new JMenuItem("Options");
        jmi_options.setMnemonic('o');
        jmi_options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionOpenOptionsDialog(event);
            }
        });
        jm_tools.add(jmi_options);
        
        // Help menu
        JMenu jm_help = new JMenu("Help");
        
        JMenuItem jmi_about = new JMenuItem("About");
        jmi_about.setMnemonic('a');
        jmi_about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        aboutDialog.setVisible(true);
                    }
                });
            }
        });
        jm_help.add(jmi_about);
        
        // Add menus to menu-bar
        jmb.add(jm_file);
        jmb.add(jm_edit);
        jmb.add(jm_tools);
        jmb.add(jm_help);
        
        this.setJMenuBar(jmb);
    }
    
    private void init(){
        // JFileChooser: Initialize
        jfc_request.addChoosableFileFilter(new RCFileFilter(FileType.REQUEST_EXT));
        jfc_response.addChoosableFileFilter(new RCFileFilter(FileType.RESPONSE_EXT));
        jfc_archive.addChoosableFileFilter(new RCFileFilter(FileType.ARCHIVE_EXT));
        
        // Create AboutDialog
        aboutDialog = new AboutDialog(this);
        
        createMenu();
        
        ImageIcon icon = 
                new ImageIcon(this.getClass().getClassLoader()
                .getResource("org/wiztools/restclient/WizLogo.png"));
        setIconImage(icon.getImage());
        view = new RESTView(this);
        setContentPane(view);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                shutdownCall();
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void actionOpenOptionsDialog(ActionEvent event){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(optionsDialog == null){
                    optionsDialog = new OptionsDialog(me);
                }
                optionsDialog.setVisible(true);
            }
        });
    }
    
    File getOpenFile(final FileChooserType type){
        return getOpenFile(type, me);
    }
    
    File getOpenFile(final FileChooserType type, Component parent){
        String title = null;
        JFileChooser jfc = null;
        if(type == FileChooserType.OPEN_REQUEST){
            jfc = jfc_request;
            title = "Open Request";
        }
        else if(type == FileChooserType.OPEN_RESPONSE){
            jfc = jfc_response;
            title = "Open Response";
        }
        else if(type == FileChooserType.OPEN_ARCHIVE){
            jfc = jfc_archive;
            title = "Open Req-Res Archive";
        }
        else if(type == FileChooserType.OPEN_REQUEST_BODY){
            jfc = jfc_generic;
            title = "Open Request Body";
        }
        else if(type == FileChooserType.OPEN_TEST_SCRIPT){
            jfc = jfc_generic;
            title = "Open Test Script";
        }
        jfc.setDialogTitle(title);
        int status = jfc.showOpenDialog(parent);
        if(status == JFileChooser.APPROVE_OPTION){
            File f = jfc.getSelectedFile();
            return f;
        }
        return null;
    }
    
    
    private void jmi_open_reqAction(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File f = getOpenFile(FileChooserType.OPEN_REQUEST);
                if(f != null){
                    Exception e = null;
                    try{
                        RequestBean request = XMLUtil.getRequestFromXMLFile(f);
                        view.setUIFromRequest(request);
                    }
                    catch(IOException ex){
                        e = ex;
                    }
                    catch(XMLException ex){
                        e = ex;
                    }
                    catch(Base64.Base64Exception ex){
                        e = ex;
                    }
                    if(e != null){
                        view.doError(Util.getStackTrace(e));
                    }
                }
        }});
    }
    
    private void jmi_open_resAction(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File f = getOpenFile(FileChooserType.OPEN_RESPONSE);
                if(f != null){
                    Exception e = null;
                    try{
                        ResponseBean response = XMLUtil.getResponseFromXMLFile(f);
                        view.setUIFromResponse(response);
                    }
                    catch(IOException ex){
                        e = ex;
                    }
                    catch(XMLException ex){
                        e = ex;
                    }
                    if(e != null){
                        view.doError(Util.getStackTrace(e));
                    }
                }
            }
        });
    }
    
    private void jmi_open_archiveAction(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File f = getOpenFile(FileChooserType.OPEN_ARCHIVE);
                if(f != null){
                    Exception e = null;
                    try{
                        ReqResBean encp = Util.getReqResArchive(f);
                        RequestBean request = encp.getRequestBean();
                        ResponseBean response = encp.getResponseBean();
                        if(request != null && response != null){
                            view.setUIFromRequest(request);
                            view.setUIFromResponse(response);
                        }
                        else{
                            view.doError("Unable to load archive! Check if valid archive!");
                        }
                    }
                    catch(IOException ex){
                        e = ex;
                    }
                    catch(XMLException ex){
                        e = ex;
                    }
                    if(e != null){
                        view.doError(Util.getStackTrace(e));
                    }
                }
            }
        });
    }
    
    // This method is invoked from SU.invokeLater
    File getSaveFile(final FileChooserType type){
        JFileChooser jfc = null;
        String title = null;
        if(type == FileChooserType.SAVE_REQUEST){
            jfc = jfc_request;
            title = "Save Request";
        }
        else if(type == FileChooserType.SAVE_RESPONSE){
            jfc = jfc_response;
            title = "Save Response";
        }
        else if(type == FileChooserType.SAVE_RESPONSE_BODY){
            jfc = jfc_generic;
            title = "Save Response Body";
        }
        else if(type == FileChooserType.SAVE_ARCHIVE){
            jfc = jfc_archive;
            title = "Save Req-Res Archive";
        }
        jfc.setDialogTitle(title);
        int status = jfc.showSaveDialog(this);
        if(status == JFileChooser.APPROVE_OPTION){
            File f = jfc.getSelectedFile();
            
            if(f == null){
                return null;
            }
            
            String ext = null;
            switch(type){
                case SAVE_REQUEST: 
                    ext = FileType.REQUEST_EXT;
                    break;
                case SAVE_RESPONSE:
                    ext = FileType.RESPONSE_EXT;
                    break;
                case SAVE_ARCHIVE:
                    ext = FileType.ARCHIVE_EXT;
                    break;
                default:
                    break;
            }
            if(ext != null){
                String path = f.getAbsolutePath();
                path = path.toLowerCase();
                // Add our extension only if the selected filter is ours
                FileFilter ff = jfc.getFileFilter();
                RCFileFilter rcFileFilter = null;
                if(ff instanceof RCFileFilter){
                    rcFileFilter = (RCFileFilter)ff;
                }
                if((rcFileFilter != null) &&
                        (rcFileFilter.getFileTypeExt().equals(ext)) &&
                        !path.endsWith(ext)){
                    f = new File(f.getAbsolutePath() + ext);
                    jfc.setSelectedFile(f);
                    view.setStatusMessage("Adding default extension: " + ext);
                }
            }
            if(f.exists()){
                int yesNo = JOptionPane.showConfirmDialog(me,
                        "File exists. Overwrite?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);
                if(yesNo == JOptionPane.YES_OPTION){
                    return f;
                }
                else{
                    JOptionPane.showMessageDialog(me,
                            "File not saved!",
                            "Not saved",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else{ // If the file is new one
                return f;
            }
        }
        return null;
    }
    
    private static final String[] DO_SAVE_UI_REQUEST = new String[]{"Request", "completed Request"};
    private static final String[] DO_SAVE_UI_RESPONSE = new String[]{"Response", "received Response"};
    private static final String[] DO_SAVE_UI_ARCHIVE = new String[]{"Request/Response", "completed Request-Response"};
    
    private boolean doSaveEvenIfUIChanged(final String[] parameters){
        final String message = MessageI18N.getMessage(
                "yes-no.cant.save.req-res", parameters);
        int optionChoosen = JOptionPane.showConfirmDialog(view,
                message,
                "UI Parameters Changed!",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if(optionChoosen != JOptionPane.OK_OPTION){
            return false;
        }
        return true;
    }
    
    private void actionSave(final FileChooserType type){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(type == FileChooserType.SAVE_REQUEST){
                    RequestBean request = view.getLastRequest();
                    
                    if(request == null){
                        JOptionPane.showMessageDialog(view,
                                "No last request available.",
                                "No Request",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    RequestBean uiRequest = view.getRequestFromUI();
                    if(!request.equals(uiRequest)){
                        if(!doSaveEvenIfUIChanged(DO_SAVE_UI_REQUEST)){
                            return;
                        }
                        
                    }
                    
                    File f = getSaveFile(FileChooserType.SAVE_REQUEST);
                    if(f != null){
                        try{
                            XMLUtil.writeRequestXML(request, f);
                        }
                        catch(IOException ex){
                            view.doError(Util.getStackTrace(ex));
                        }
                        catch(XMLException ex){
                            view.doError(Util.getStackTrace(ex));
                        }
                    }
                }
                else if(type == FileChooserType.SAVE_RESPONSE){
                    ResponseBean response = view.getLastResponse();
                    if(response == null){
                        JOptionPane.showMessageDialog(view,
                                "No last response available.",
                                "No Response",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    ResponseBean uiResponse = view.getResponseFromUI();
                    if(!response.equals(uiResponse)){
                        if(!doSaveEvenIfUIChanged(DO_SAVE_UI_RESPONSE)){
                            return;
                        }
                    }
                    File f = getSaveFile(FileChooserType.SAVE_RESPONSE);
                    if(f != null){
                        try{
                            XMLUtil.writeResponseXML(response, f);
                        }
                        catch(IOException ex){
                            view.doError(Util.getStackTrace(ex));
                        }
                        catch(XMLException ex){
                            view.doError(Util.getStackTrace(ex));
                        }
                    }
                }
                else if(type == FileChooserType.SAVE_RESPONSE_BODY){
                    ResponseBean response = view.getLastResponse();
                    if(response == null){
                        JOptionPane.showMessageDialog(view,
                                "No last response available.",
                                "No Response",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    File f = getSaveFile(FileChooserType.SAVE_RESPONSE_BODY);
                    if(f != null){
                        PrintWriter pw = null;
                        try{
                            pw = new PrintWriter(new FileWriter(f));
                            pw.print(response.getResponseBody());
                        }
                        catch(IOException ex){
                            view.doError(Util.getStackTrace(ex));
                        }
                        finally{
                            if(pw != null){
                                pw.close();
                            }
                        }
                    }
                }
                else if(type == FileChooserType.SAVE_ARCHIVE){
                    RequestBean request = view.getLastRequest();
                    ResponseBean response = view.getLastResponse();
                    if(request == null || response == null){
                        JOptionPane.showMessageDialog(view,
                                "No last request/response available.",
                                "No Request/Response",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    RequestBean uiRequest = view.getRequestFromUI();
                    ResponseBean uiResponse = view.getResponseFromUI();
                    if((!request.equals(uiRequest)) || (!response.equals(uiResponse))){
                        if(!doSaveEvenIfUIChanged(DO_SAVE_UI_ARCHIVE)){
                            return;
                        }
                    }
                    File f = getSaveFile(FileChooserType.SAVE_ARCHIVE);
                    if(f != null){
                        Exception e = null;
                        try{
                            Util.createReqResArchive(request, response, f);
                        }
                        catch(IOException ex){
                            e = ex;
                        }
                        catch(XMLException ex){
                            e = ex;
                        }
                        if(e != null){
                            view.doError(Util.getStackTrace(e));
                        }
                    }
                }
            }
        });
    }
    
    private void shutdownCall(){
        System.out.println("Exiting...");
        System.exit(0);
    }
}
