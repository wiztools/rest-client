package org.wiztools.restclient.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;
import org.simplericity.macify.eawt.DefaultApplication;
import org.wiztools.commons.FileUtil;
import org.wiztools.commons.StringUtil;
import org.wiztools.filechooser.FileChooser;
import org.wiztools.filechooser.FileChooserResponse;
import org.wiztools.filechooser.FileFilter;
import org.wiztools.restclient.FileType;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.MessageI18N;
import org.wiztools.restclient.RCConstants;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.Versions;
import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.persistence.PersistenceException;
import org.wiztools.restclient.persistence.PersistenceWrite;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;
import org.wiztools.restclient.server.TraceServer;
import org.wiztools.restclient.ui.history.HistoryManager;
import org.wiztools.restclient.ui.option.OptionsDialog;
import org.wiztools.restclient.ui.update.AppUpdateRunner;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
@Singleton
class RESTMain implements RESTUserInterface {
    
    private static final Logger LOG = Logger.getLogger(RESTMain.class.getName());
    
    private final Application application = new DefaultApplication();
    
    @Inject private RESTViewImpl view;
    @Inject private AboutDialog aboutDialog;
    @Inject private OptionsDialog optionsDialog;
    @Inject private PasswordGenDialog passwordGenDialog;
    @Inject private IGlobalOptions options;
    
    @Inject private HistoryManager historyManager;
    
    private URLEncodeDecodeDialog urlEncodeDecodeDialog;
    
    // Requests and responses are generally saved in different dirs
    private final FileChooser jfc_request = UIUtil.getNewFileChooser();
    private final FileChooser jfc_response = UIUtil.getNewFileChooser();
    private final FileChooser jfc_generic = UIUtil.getNewFileChooser();
    private final FileChooser jfc_archive = UIUtil.getNewFileChooser();
    private final FileChooser jfc_history = UIUtil.getNewFileChooser();
    
    @Inject private RecentFilesHelper recentFilesHelper;
    
    private static final String URL_BOOK = "http://www.amazon.com/dp/B00KEADQF2";
    private static final String URL_FB = "http://www.facebook.com/wiztools.org";
    private static final String URL_ISSUE = "https://github.com/wiztools/rest-client/issues";
    
    private final JFrame frame;
    
    public RESTMain(){
        // Macify:
        application.addAboutMenuItem();
        application.addApplicationListener(new RCApplicationListener());
        application.addPreferencesMenuItem();
        
        // Application logic:
        frame = new JFrame(RCConstants.TITLE + Versions.CURRENT);
    }
    
    @Override
    public RESTViewImpl getView(){
        return view;
    }
    
    @Override
    public JFrame getFrame(){
        return this.frame;
    }
    
    private void createMenu(){
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
        
        // History Menu
        JMenu jm_history = new JMenu("History");
        
        final JMenuItem jmi_back = new JMenuItem("Back");
        jmi_back.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_LEFT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jmi_back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verify if most recent history needs to be shown:
                if(historyManager.isMostRecent()) {
                    try {
                        Request reqFromUi = view.getRequestFromUI();
                        if(!reqFromUi.equals(historyManager.current())) {
                            view.setUIFromRequest(historyManager.current());
                            return;
                        }
                    }
                    catch(IllegalStateException ex) {
                        if(historyManager.current() != null) {
                            view.setUIFromRequest(historyManager.current());
                            return;
                        }
                    }
                }
                
                // Normal logic, cursor-1 history:
                if(!historyManager.isOldest()) {
                    Request request = historyManager.back();
                    if(request != null) {
                        view.setUIFromRequest(request);
                    }
                }
                else {
                    view.setStatusMessage("Already in oldest");
                }
            }
        });
        jm_history.add(jmi_back);
        
        final JMenuItem jmi_forward = new JMenuItem("Forward");
        jmi_forward.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_RIGHT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jmi_forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!historyManager.isMostRecent()) {
                    Request request = historyManager.forward();
                    if(request != null) {
                        view.setUIFromRequest(request);
                    }
                }
                else {
                    view.setStatusMessage("Already in latest");
                }
            }
        });
        jm_history.add(jmi_forward);
        
        jm_history.addSeparator();
        
        JMenuItem jmi_clear_history = new JMenuItem("Clear History");
        jmi_clear_history.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historyManager.clear();
                view.setStatusMessage("History cleared");
            }
        });
        jm_history.add(jmi_clear_history);
        
        jm_history.addSeparator();
        
        JMenuItem jmi_save_history = new JMenuItem("Save History");
        jmi_save_history.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(historyManager.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "History is empty!");
                    return;
                }
                final File f = FileType.getWithExtension(
                        getSaveFile(FileChooserType.SAVE_HISTORY),
                        FileType.HISTORY);
                if(f != null) {
                    try {
                        historyManager.save(f);
                        view.setStatusMessage("Saved history file: " + f.getName());
                    }
                    catch(IOException ex) {
                        view.showError(Util.getStackTrace(ex));
                    }
                }
            }
        });
        jm_history.add(jmi_save_history);
        
        JMenuItem jmi_load_history = new JMenuItem("Load History");
        jmi_load_history.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!historyManager.isEmpty()) {
                    final int confirm = JOptionPane.showConfirmDialog(null,
                            "Overwrite existing history?",
                            "Existing history will be overwritten. Proceed?", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.NO_OPTION || confirm == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                }
                final File f = getOpenFile(FileChooserType.OPEN_HISTORY);
                if(f != null) {
                    try {
                        historyManager.clear();
                        historyManager.load(f);
                    }
                    catch(IOException ex) {
                        view.showError(ex);
                    }
                    catch(XMLException ex) {
                        view.showError(ex);
                    }
                }
            }
        });
        jm_history.add(jmi_load_history);
        
        // Enable disable history controls based on cursor positions:
        jm_history.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                if(historyManager.isOldest()) {
                    jmi_back.setEnabled(false);
                }
                else {
                    jmi_back.setEnabled(true);
                }
                
                if(historyManager.isMostRecent()) {
                    jmi_forward.setEnabled(false);
                }
                else {
                    jmi_forward.setEnabled(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                //
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                // 
            }
        });
        
        // Tools menu
        JMenu jm_tools = new JMenu("Tools");
        jm_tools.setMnemonic('o');
        
        { // URL Encode Decoder:
            JMenuItem jmi_url_encode = new JMenuItem("URL Encoder/Decoder");
            jmi_url_encode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(urlEncodeDecodeDialog == null) {
                        urlEncodeDecodeDialog = new URLEncodeDecodeDialog(frame);
                    }
                    urlEncodeDecodeDialog.setVisible(true);
                }
            });
            jm_tools.add(jmi_url_encode);
        }
        
        { // Password Encoder Decoder:
            JMenuItem jmi_pwd_gen = new JMenuItem("Password Encoder/Decoder");
            jmi_pwd_gen.setMnemonic('p');
            jmi_pwd_gen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    passwordGenDialog.setVisible(true);
                }
            });
            jm_tools.add(jmi_pwd_gen);
        }
        
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
                String url = view.getUrl();
                if(StringUtil.isNotEmpty(url)){
                    int ret = JOptionPane.showConfirmDialog(frame,
                            "URL field not empty. Overwrite?",
                            "Request URL not empty",
                            JOptionPane.YES_NO_OPTION);
                    if(ret == JOptionPane.NO_OPTION){
                        return;
                    }
                }
                view.setUrl("http://localhost:" + TraceServer.PORT + "/");
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
        
        // Menu-bar
        JMenuBar jmb = new JMenuBar();
        
        jmb.add(jm_file);
        jmb.add(jm_edit);
        jmb.add(jm_history);
        jmb.add(jm_tools);
        
        // Help menu
        JMenu jm_help = new JMenu("Help");
        jm_help.setMnemonic(KeyEvent.VK_H);
        
        { // RESTClient Book
            JMenuItem jmi_url = new JMenuItem("RESTClient Book (Kindle)");
            jmi_url.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openUrl(URL_BOOK);
                }
            });
            jm_help.add(jmi_url);
        }
        
        { // FB
            JMenuItem jmi_url = new JMenuItem("Follow in Facebook");
            jmi_url.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openUrl(URL_FB);
                }
            });
            jm_help.add(jmi_url);
        }
        
        { // Issue
            JMenuItem jmi_url = new JMenuItem("Report Issue / Request Enhancement");
            jmi_url.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openUrl(URL_ISSUE);
                }
            });
            jm_help.add(jmi_url);
        }
        
        // Help > About:
        if(!application.isMac()) { // show About for only non-Mac platform!
            // Add separator before the About menu-item:
            jm_help.addSeparator();
            
            // About menu:
            JMenuItem jmi_about = new JMenuItem("About");
            jmi_about.setMnemonic('a');
            jmi_about.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    showAboutDialog();
                }
            });
            jm_help.add(jmi_about);    
        }
        // Add Help menu to Menubar:
        jmb.add(jm_help);
        
        frame.setJMenuBar(jmb);
    }
    
    private void initJFC() {
        // JFileChooser: Initialize
        jfc_request.addChoosableFileFilter(new RCFileFilter(FileType.REQUEST_EXT));
        jfc_response.addChoosableFileFilter(new RCFileFilter(FileType.RESPONSE_EXT));
        jfc_archive.addChoosableFileFilter(new RCFileFilter(FileType.ARCHIVE_EXT));
        
        // init last dir:
        { // request jfc:
            final String lastDir = options.getProperty(UIUtil.LAST_CWD_REQ);
            if(StringUtil.isNotEmpty(lastDir)) {
                jfc_request.setCurrentDirectory(new File(lastDir));
            }
        }
        { // response jfc:
            final String lastDir = options.getProperty(UIUtil.LAST_CWD_RES);
            if(StringUtil.isNotEmpty(lastDir)) {
                jfc_response.setCurrentDirectory(new File(lastDir));
            }
        }
        { // archive jfc:
            final String lastDir = options.getProperty(UIUtil.LAST_CWD_ARC);
            if(StringUtil.isNotEmpty(lastDir)) {
                jfc_archive.setCurrentDirectory(new File(lastDir));
            }
        }
        { // history jfc:
            final String lastDir = options.getProperty(UIUtil.LAST_CWD_HIS);
            if(StringUtil.isNotEmpty(lastDir)) {
                jfc_history.setCurrentDirectory(new File(lastDir));
            }
        }
    }
    
    @PostConstruct
    public void show() {
        initJFC();
        
        frame.setContentPane(view.getContainer());
        createMenu();
        ImageIcon icon =
                UIUtil.getIconFromClasspath("org/wiztools/restclient/logo_30.png");
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
        
        // Update check:
        new Thread(ServiceLocator.getInstance(AppUpdateRunner.class)).start();
    }
    
    private void openUrl(String url) {
        Desktop dt = Desktop.isDesktopSupported()? Desktop.getDesktop(): null;
        if(dt != null && dt.isSupported(Desktop.Action.BROWSE)) {
            try {
                dt.browse(new URI(url));
            }
            catch (URISyntaxException ex) {
                assert true: "Will never come here!";
            }
            catch(IOException ex) {
                LOG.log(Level.WARNING, null, ex);
                showUrlDialog(url);
            }
        }
        else {
            showUrlDialog(url);
        }
    }
    
    private void showUrlDialog(final String url) {
        JOptionPane.showMessageDialog(frame,
                "Visit this URL: " + url,  // Message
                "Visit URL",               // Title
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showOptionsDialog(){
        optionsDialog.setVisible(true);
    }
    
    private void saveLastDir(final FileChooserType type, final File dir) {
        switch(type) {
            case OPEN_REQUEST:
            case SAVE_REQUEST:
                options.setProperty(UIUtil.LAST_CWD_REQ, dir.getPath());
                break;
            case OPEN_RESPONSE:
            case SAVE_RESPONSE:
                options.setProperty(UIUtil.LAST_CWD_RES, dir.getPath());
                break;
            case OPEN_ARCHIVE:
            case SAVE_ARCHIVE:
                options.setProperty(UIUtil.LAST_CWD_ARC, dir.getPath());
                break;
            case OPEN_HISTORY:
            case SAVE_HISTORY:
                options.setProperty(UIUtil.LAST_CWD_HIS, dir.getPath());
                break;
        }
    }
    
    @Override
    public File getOpenFile(final FileChooserType type){
        return getOpenFile(type, frame);
    }
    
    @Override
    public File getOpenFile(final FileChooserType type, final Component parent){
        String title = null;
        FileChooser jfc = null;
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
        else if(type == FileChooserType.OPEN_HISTORY) {
            jfc = jfc_history;
            title = "Open History";
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
        FileChooserResponse status = jfc.showOpenDialog(parent);
        if(status == FileChooserResponse.APPROVE_OPTION){
            File f = jfc.getSelectedFile();
            saveLastDir(type, f.getParentFile());
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
        FileChooser jfc = null;
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
        else if(type == FileChooserType.SAVE_HISTORY) {
            jfc = jfc_history;
            title = "Save History";
        }
        jfc.setDialogTitle(title);
        FileChooserResponse status = jfc.showSaveDialog(frame);
        if(status == FileChooserResponse.APPROVE_OPTION){
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
                    saveLastDir(type, f.getParentFile());
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
                saveLastDir(type, f.getParentFile());
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
        int optionChoosen = JOptionPane.showConfirmDialog(view.getContainer(),
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
        PersistenceWrite p = new XmlPersistenceWrite();
        if(type == FileChooserType.SAVE_REQUEST){
            Request request = view.getLastRequest();

            if(request == null){
                JOptionPane.showMessageDialog(view.getContainer(),
                        "No last request available.",
                        "No Request",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Request uiRequest = view.getRequestFromUI();
                if(!request.equals(uiRequest)){
                    if(!doSaveEvenIfUIChanged(DO_SAVE_UI_REQUEST)){
                        return;
                    }
                }

                File f = getSaveFile(FileChooserType.SAVE_REQUEST);
                if(f != null){
                    try{
                        p.writeRequest(request, f);
                        recentFilesHelper.openedFile(f);
                    }
                    catch(IOException | PersistenceException ex) {
                        view.showError(Util.getStackTrace(ex));
                    }
                }
            }
            catch(IllegalStateException ex) {
                view.showError(Util.getStackTrace(ex));
            }
        }
        else if(type == FileChooserType.SAVE_RESPONSE){
            Response response = view.getLastResponse();
            if(response == null){
                JOptionPane.showMessageDialog(view.getContainer(),
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
                    p.writeResponse(response, f);
                    recentFilesHelper.openedFile(f);
                }
                catch(IOException | PersistenceException ex) {
                    view.showError(Util.getStackTrace(ex));
                }
            }
        }
        else if(type == FileChooserType.SAVE_RESPONSE_BODY){
            Response response = view.getLastResponse();
            if(response == null){
                JOptionPane.showMessageDialog(view.getContainer(),
                        "No last response available.",
                        "No Response",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(response.getResponseBody() == null) {
                JOptionPane.showMessageDialog(view.getContainer(),
                        "Last response does not have body.",
                        "No Body in Response",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            File f = getSaveFile(FileChooserType.SAVE_RESPONSE_BODY);
            if(f != null){
                try{
                    FileUtil.writeBytes(f, response.getResponseBody());
                }
                catch(IOException ex){
                    view.showError(Util.getStackTrace(ex));
                }
            }
        }
        else if(type == FileChooserType.SAVE_ARCHIVE){
            Request request = view.getLastRequest();
            Response response = view.getLastResponse();
            if(request == null || response == null){
                JOptionPane.showMessageDialog(view.getContainer(),
                        "No last request/response available.",
                        "No Request/Response",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
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
            catch(IllegalStateException ex) {
                view.showError(Util.getStackTrace(ex));
            }
        }
    }
    
    private void shutdownCall() {
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
