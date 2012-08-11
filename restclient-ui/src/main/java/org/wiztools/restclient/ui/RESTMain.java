package org.wiztools.restclient.ui;

import org.wiztools.restclient.util.Util;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;
import org.simplericity.macify.eawt.DefaultApplication;
import org.wiztools.restclient.*;
import org.wiztools.restclient.server.TraceServer;

/**
 *
 * @author schandran
 */
@Singleton
class RESTMain implements RESTUserInterface {
    
    private final Application application = new DefaultApplication();
    
    @Inject private RESTView view;
    @Inject private AboutDialog aboutDialog;
    @Inject private OptionsDialog optionsDialog;
    @Inject private PasswordGenDialog passwordGenDialog;
    
    // Requests and responses are generally saved in different dirs
    private JFileChooser jfc_request = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_response = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_generic = UIUtil.getNewJFileChooser();
    private JFileChooser jfc_archive = UIUtil.getNewJFileChooser();
    
    @Inject private RecentFilesHelper recentFilesHelper;
    
    private final JFrame frame;
    
    public RESTMain(){
        // Macify:
        application.addAboutMenuItem();
        application.addApplicationListener(new RCApplicationListener());
        application.addPreferencesMenuItem();
        
        // Application logic:
        frame = new JFrame(RCConstants.TITLE + RCConstants.VERSION);
        init();
    }
    
    @Override
    public RESTView getView(){
        return view;
    }
    
    @Override
    public JFrame getFrame(){
        return this.frame;
    }
    
    private void createMenu(){
        JMenuBar jmb = new JMenuBar();
        
        // File menu
        JMenu jm_file = new JMenu("File");
        jm_file.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem jmi_open_req = new JMenuItem("Open Request", RCFileView.REQUEST_ICON);
        jmi_open_req.setMnemonic(KeyEvent.VK_O);
        jmi_open_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jmi_open_req.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                jmi_open_reqAction();
            }
        });
        jm_file.add(jmi_open_req);
        
        JMenuItem jmi_open_res = new JMenuItem("Open Response", RCFileView.RESPONSE_ICON);
        jmi_open_res.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jmi_open_resAction();
            }
        });
        jm_file.add(jmi_open_res);
        
        JMenuItem jmi_open_archive = new JMenuItem("Open Req-Res Archive", RCFileView.ARCHIVE_ICON);
        jmi_open_archive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jmi_open_archiveAction();
            }
        });
        jm_file.add(jmi_open_archive);
        
        final JMenu jm_open_recent = new JMenu("Open recent");
        jm_open_recent.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent me) {
                List<File> recentFiles = recentFilesHelper.getRecentFiles();
                jm_open_recent.removeAll();
                for(final File f: recentFiles) {
                    JMenuItem jmi = new JMenuItem(f.getName());
                    jmi.setToolTipText(f.getAbsolutePath());
                    jmi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            FileOpenUtil.open(view, f);
                        }
                    });
                    jm_open_recent.add(jmi);
                }

                // Add clear option:
                if(!recentFilesHelper.isEmpty()) {
                    jm_open_recent.addSeparator();

                    JMenuItem jmi = new JMenuItem("Clear");
                    jmi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            recentFilesHelper.clear();
                        }
                    });
                    jm_open_recent.add(jmi);
                }
            }

            @Override
            public void menuDeselected(MenuEvent me) {
                // do nothing
            }

            @Override
            public void menuCanceled(MenuEvent me) {
                // do nothing
            }
        });
        jm_file.add(jm_open_recent);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_save_req = new JMenuItem("Save Request", RCFileView.REQUEST_ICON);
        jmi_save_req.setMnemonic(KeyEvent.VK_Q);
        jmi_save_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jmi_save_req.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionSave(FileChooserType.SAVE_REQUEST);
            }
        });
        jm_file.add(jmi_save_req);
        
        JMenuItem jmi_save_res = new JMenuItem("Save Response", RCFileView.RESPONSE_ICON);
        jmi_save_res.setMnemonic(KeyEvent.VK_S);
        jmi_save_res.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionSave(FileChooserType.SAVE_RESPONSE);
            }
        });
        jm_file.add(jmi_save_res);
        
        JMenuItem jmi_save_res_body = new JMenuItem("Save Response Body", RCFileView.FILE_ICON);
        // jmi_save_res_body.setMnemonic(' ');
        jmi_save_res_body.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSave(FileChooserType.SAVE_RESPONSE_BODY);
            }
        });
        jm_file.add(jmi_save_res_body);
        
        JMenuItem jmi_save_archive = new JMenuItem("Save Req-Res Archive", RCFileView.ARCHIVE_ICON);
        jmi_save_archive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSave(FileChooserType.SAVE_ARCHIVE);
            }
        });
        jm_file.add(jmi_save_archive);
        
        if(!application.isMac()) { // Shown only for non-Mac platform!
            jm_file.addSeparator();

            JMenuItem jmi_exit = new JMenuItem("Exit", UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "fv_exit.png"));
            jmi_exit.setMnemonic(KeyEvent.VK_X);
            jmi_exit.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            jmi_exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    shutdownCall();
                }
            });
            jm_file.add(jmi_exit);
        }
        
        // Edit menu
        JMenu jm_edit = new JMenu("Edit");
        jm_edit.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem jmi_clear_res = new JMenuItem("Clear Response");
        jmi_clear_res.setMnemonic(KeyEvent.VK_C);
        jmi_clear_res.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.clearUIResponse();
            }
        });
        jm_edit.add(jmi_clear_res);
        JMenuItem jmi_reset_all = new JMenuItem("Reset All");
        jmi_reset_all.setMnemonic('a');
        jmi_reset_all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.clearUIResponse();
                view.clearUIRequest();
            }
        });
        jm_edit.add(jmi_reset_all);
        
        jm_edit.addSeparator();
        
        JMenuItem jmi_reset_to_last = new JMenuItem("Reset to Last Request-Response");
        jmi_reset_to_last.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.getLastRequest() != null && view.getLastResponse() != null){
                    view.setUIToLastRequestResponse();
                }
                else{
                    JOptionPane.showMessageDialog(frame,
                            "No Last Request-Response Available",
                            "No Last Request-Response Available",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        jm_edit.add(jmi_reset_to_last);
        
        // Tools menu
        JMenu jm_tools = new JMenu("Tools");
        jm_tools.setMnemonic('o');
        
        JMenuItem jmi_session = new JMenuItem("Open Session View");
        jmi_session.setMnemonic('s');
        jmi_session.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                view.showSessionFrame();
            }
        });
        // Commenting for 2.x release:
        // jm_tools.add(jmi_session);
        
        JMenuItem jmi_pwd_gen = new JMenuItem("Password Encoder/Decoder");
        jmi_pwd_gen.setMnemonic('p');
        jmi_pwd_gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                passwordGenDialog.setVisible(true);
            }
        });
        jm_tools.add(jmi_pwd_gen);
        
        jm_tools.addSeparator();
        
        // Trace Server
        JMenuItem jmi_server_start = new JMenuItem("Start Trace Server @ port " + TraceServer.PORT);
        jmi_server_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try{
                    TraceServer.start();
                    view.setStatusMessage("Trace Server started.");
                }
                catch(Exception ex){
                    view.showError(Util.getStackTrace(ex));
                }
            }
        });
        jm_tools.add(jmi_server_start);
        
        JMenuItem jmi_server_stop = new JMenuItem("Stop Trace Server");
        jmi_server_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try{
                    if(TraceServer.isRunning()){
                        TraceServer.stop();
                        view.setStatusMessage("Trace Server stopped.");
                    }
                }
                catch(Exception ex){
                    view.showError(Util.getStackTrace(ex));
                }
            }
        });
        jm_tools.add(jmi_server_stop);
        
        JMenuItem jmi_server_fill_url = new JMenuItem("Insert Trace Server URL");
        jmi_server_fill_url.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                RequestBean request = (RequestBean) view.getRequestFromUI();
                if(request.getUrl() != null){
                    int ret = JOptionPane.showConfirmDialog(frame,
                            "URL field not empty. Overwrite?",
                            "Request URL not empty",
                            JOptionPane.YES_NO_OPTION);
                    if(ret == JOptionPane.NO_OPTION){
                        return;
                    }
                }
                try {
                    request.setUrl(new URL("http://localhost:" + TraceServer.PORT + "/"));
                } catch (MalformedURLException ex) {
                    assert true: ex;
                }
                view.setUIFromRequest(request);
            }
        });
        jm_tools.add(jmi_server_fill_url);
        
        if(!application.isMac()) { // Add Options menu only for non-Mac platform!
            jm_tools.addSeparator();

            JMenuItem jmi_options = new JMenuItem("Options");
            jmi_options.setMnemonic('o');
            jmi_options.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    showOptionsDialog();
                }
            });
            jm_tools.add(jmi_options);
        }
        
        // Add menus to menu-bar
        jmb.add(jm_file);
        jmb.add(jm_edit);
        jmb.add(jm_tools);
        
        // Help menu
        if(!application.isMac()) { // show for only non-Mac platform!
            JMenu jm_help = new JMenu("Help");
            jm_help.setMnemonic(KeyEvent.VK_H);

            JMenuItem jmi_about = new JMenuItem("About");
            jmi_about.setMnemonic('a');
            jmi_about.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    showAboutDialog();
                }
            });
            jm_help.add(jmi_about);
            
            // Add Help menu to Menubar:
            jmb.add(jm_help);
        }
        
        frame.setJMenuBar(jmb);
    }
    
    private void init() {
        // JFileChooser: Initialize
        jfc_request.addChoosableFileFilter(new RCFileFilter(FileType.REQUEST_EXT));
        jfc_response.addChoosableFileFilter(new RCFileFilter(FileType.RESPONSE_EXT));
        jfc_archive.addChoosableFileFilter(new RCFileFilter(FileType.ARCHIVE_EXT));
    }
    
    @PostConstruct
    public void show() {
        frame.setContentPane(view);
        createMenu();
        ImageIcon icon =
                UIUtil.getIconFromClasspath("org/wiztools/restclient/WizLogo.png");
        frame.setIconImage(icon.getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                shutdownCall();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void showOptionsDialog(){
        optionsDialog.setVisible(true);
    }
    
    @Override
    public File getOpenFile(final FileChooserType type){
        return getOpenFile(type, frame);
    }
    
    @Override
    public File getOpenFile(final FileChooserType type, final Component parent){
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
        else if(type == FileChooserType.OPEN_GENERIC){
            jfc = jfc_generic;
            title = "Open";
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
        File f = getOpenFile(FileChooserType.OPEN_REQUEST);
        if(f != null){
            FileOpenUtil.openRequest(view, f);
            recentFilesHelper.openedFile(f);
        }
    }
    
    private void jmi_open_resAction(){
        File f = getOpenFile(FileChooserType.OPEN_RESPONSE);
        if(f != null){
            FileOpenUtil.openResponse(view, f);
            recentFilesHelper.openedFile(f);
        }
    }
    
    private void jmi_open_archiveAction(){
        File f = getOpenFile(FileChooserType.OPEN_ARCHIVE);
        if(f != null){
            FileOpenUtil.openArchive(view, f);
            recentFilesHelper.openedFile(f);
        }
    }
    
    // This method is invoked from SU.invokeLater
    @Override
    public File getSaveFile(final FileChooserType type){
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
        int status = jfc.showSaveDialog(frame);
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
                int yesNo = JOptionPane.showConfirmDialog(frame,
                        "File exists. Overwrite?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);
                if(yesNo == JOptionPane.YES_OPTION){
                    return f;
                }
                else{
                    JOptionPane.showMessageDialog(frame,
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
        if(type == FileChooserType.SAVE_REQUEST){
            Request request = view.getLastRequest();

            if(request == null){
                JOptionPane.showMessageDialog(view,
                        "No last request available.",
                        "No Request",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Request uiRequest = view.getRequestFromUI();
            if(!request.equals(uiRequest)){
                if(!doSaveEvenIfUIChanged(DO_SAVE_UI_REQUEST)){
                    return;
                }
            }

            File f = getSaveFile(FileChooserType.SAVE_REQUEST);
            if(f != null){
                try{
                    XMLUtil.writeRequestXML(request, f);
                    recentFilesHelper.openedFile(f);
                }
                catch(IOException ex){
                    view.showError(Util.getStackTrace(ex));
                }
                catch(XMLException ex){
                    view.showError(Util.getStackTrace(ex));
                }
            }
        }
        else if(type == FileChooserType.SAVE_RESPONSE){
            Response response = view.getLastResponse();
            if(response == null){
                JOptionPane.showMessageDialog(view,
                        "No last response available.",
                        "No Response",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Response uiResponse = view.getResponseFromUI();
            if(!response.equals(uiResponse)){
                if(!doSaveEvenIfUIChanged(DO_SAVE_UI_RESPONSE)){
                    return;
                }
            }
            File f = getSaveFile(FileChooserType.SAVE_RESPONSE);
            if(f != null){
                try{
                    XMLUtil.writeResponseXML(response, f);
                    recentFilesHelper.openedFile(f);
                }
                catch(IOException ex){
                    view.showError(Util.getStackTrace(ex));
                }
                catch(XMLException ex){
                    view.showError(Util.getStackTrace(ex));
                }
            }
        }
        else if(type == FileChooserType.SAVE_RESPONSE_BODY){
            Response response = view.getLastResponse();
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
                    view.showError(Util.getStackTrace(ex));
                }
                finally{
                    if(pw != null){
                        pw.close();
                    }
                }
            }
        }
        else if(type == FileChooserType.SAVE_ARCHIVE){
            Request request = view.getLastRequest();
            Response response = view.getLastResponse();
            if(request == null || response == null){
                JOptionPane.showMessageDialog(view,
                        "No last request/response available.",
                        "No Request/Response",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Request uiRequest = view.getRequestFromUI();
            Response uiResponse = view.getResponseFromUI();
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
                    recentFilesHelper.openedFile(f);
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
        }
    }
    
    private void shutdownCall(){
        recentFilesHelper.store();
        System.out.println("Exiting...");
        System.exit(0);
    }

     /**
     * show about dialog
     */
    public void showAboutDialog() {
        aboutDialog.setVisible(true);
    }
    
    public class RCApplicationListener implements ApplicationListener {

        @Override
        public void handleAbout(ApplicationEvent ae) {
            showAboutDialog();
            ae.setHandled(true);
        }

        @Override
        public void handleOpenApplication(ApplicationEvent ae) {
            // do nothing!
        }

        @Override
        public void handleOpenFile(ApplicationEvent ae) {
            final String fileName = ae.getFilename();
            final File f = new File(fileName);
            FileOpenUtil.open(view, f);
            ae.setHandled(true);
        }

        @Override
        public void handlePreferences(ApplicationEvent ae) {
            showOptionsDialog();
            ae.setHandled(true);
        }

        @Override
        public void handlePrintFile(ApplicationEvent ae) {
            JOptionPane.showMessageDialog(frame, "Sorry, printing not implemented");
        }

        @Override
        public void handleQuit(ApplicationEvent ae) {
            shutdownCall();
        }

        @Override
        public void handleReOpenApplication(ApplicationEvent ae) {
            frame.setVisible(true);
            ae.setHandled(true);
        }
    }
}
