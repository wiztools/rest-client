package org.wiztools.restclient.ui;

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
    private JFileChooser jfc_request = new JFileChooser();
    private JFileChooser jfc_response = new JFileChooser();
    
    private final RESTFrame me;
    
    public RESTFrame(final String title){
        super(title);
        me = this;
        
        init();
    }
    
    private void createMenu(){
        JMenuBar jmb = new JMenuBar();
        
        // File menu
        JMenu jm_file = new JMenu("File");
        jm_file.setMnemonic('f');
        
        JMenuItem jmi_open_req = new JMenuItem("Open Request");
        jmi_open_req.setMnemonic('o');
        jmi_open_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        jmi_open_req.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                jmi_open_reqAction();
            }
        });
        jm_file.add(jmi_open_req);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_save_req = new JMenuItem("Save Request");
        jmi_save_req.setMnemonic('q');
        jmi_save_req.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        jmi_save_req.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionSave(SAVE_REQUEST);
            }
        });
        jm_file.add(jmi_save_req);
        
        JMenuItem jmi_save_res = new JMenuItem("Save Response");
        jmi_save_res.setMnemonic('s');
        jmi_save_res.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionSave(SAVE_RESPONSE);
            }
        });
        jm_file.add(jmi_save_res);
        
        JMenuItem jmi_save_res_body = new JMenuItem("Save Response Body");
        // jmi_save_res_body.setMnemonic(' ');
        jmi_save_res_body.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSave(SAVE_RESPONSE_BODY);
            }
        });
        jm_file.add(jmi_save_res_body);
        
        jm_file.addSeparator();
        
        JMenuItem jmi_exit = new JMenuItem("Exit");
        jmi_exit.setMnemonic('x');
        jmi_exit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        jmi_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                shutdownCall();
            }
        });
        jm_file.add(jmi_exit);
        
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
        jmb.add(jm_tools);
        jmb.add(jm_help);
        
        this.setJMenuBar(jmb);
    }
    
    private void init(){
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
    
    private void jmi_open_reqAction(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jfc_request.setDialogTitle("Open Request");
                int status = jfc_request.showOpenDialog(me);
                if(status == JFileChooser.APPROVE_OPTION){
                    File f = jfc_request.getSelectedFile();
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
            }
        });
    }
    
    private static final int SAVE_REQUEST = 0;
    private static final int SAVE_RESPONSE = 1;
    private static final int SAVE_RESPONSE_BODY = 2;
    
    // This method is invoked from SU.invokeLater
    private File getSaveFile(final int type){
        JFileChooser jfc = null;
        final String title;
        if(type == SAVE_REQUEST){
            jfc = jfc_request;
            title = "Save Request";
        }
        else if(type == SAVE_RESPONSE){
            jfc = jfc_response;
            title = "Save Response";
        }
        else if(type == SAVE_RESPONSE_BODY){
            jfc = jfc_response;
            title = "Save Response Body";
        }
        else{
            title = "NULL";
        }
        jfc.setDialogTitle(title);
        int status = jfc.showSaveDialog(this);
        if(status == JFileChooser.APPROVE_OPTION){
            File f = jfc.getSelectedFile();
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
    
    private void actionSave(final int type){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(type == SAVE_REQUEST){
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
                        final String message = "<html>The Request details in the UI has changed from the last Request.<br>" +
                                "RESTClient saves only the last completed Request. If you want to save the<br> last completed Request, press <b>Ok</b>.<br><br>" +
                                "Else, if you want to save the request with changed parameters, press<br> <b>Cancel</b> and complete the request before attempting to save.</html>";
                        int optionChoosen = JOptionPane.showConfirmDialog(view,
                                message,
                                "Request Parameters Changed!",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE);
                        if(optionChoosen != JOptionPane.OK_OPTION){
                            return;
                        }
                        
                    }
                    
                    File f = getSaveFile(SAVE_REQUEST);
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
                else if(type == SAVE_RESPONSE){
                    ResponseBean response = view.getLastResponse();
                    if(response == null){
                        JOptionPane.showMessageDialog(view,
                                "No last response available.",
                                "No Response",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    File f = getSaveFile(SAVE_RESPONSE);
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
                else if(type == SAVE_RESPONSE_BODY){
                    ResponseBean response = view.getLastResponse();
                    if(response == null){
                        JOptionPane.showMessageDialog(view,
                                "No last response available.",
                                "No Response",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    File f = getSaveFile(SAVE_RESPONSE_BODY);
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
            }
        });
    }
    
    private void shutdownCall(){
        System.out.println("Exiting...");
        System.exit(0);
    }
}
