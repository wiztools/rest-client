package org.wiztools.restclient.ui.reqtest;

import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.bean.ReqResBean;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.filechooser.FileChooser;
import org.wiztools.restclient.*;
import org.wiztools.restclient.ui.*;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author Subhash
 */
public class RunTestDialog extends EscapableDialog {
    
    @Inject private RESTUserInterface ui;
    @Inject private RESTView view;
    
    private JButton jb_next = new JButton("Next");
    private JButton jb_cancel = new JButton("Cancel");
    
    private JRadioButton jrb_archive = new JRadioButton("From Request-Response Archive");
    private JRadioButton jrb_last = new JRadioButton("From last Request-Response");
    
    private JTextField jtf_archive = new JTextField();
    private JButton jb_archive_browse = new JButton("Browse");
    
    private RunTestDialog me;
    
    private FileChooser jfc = UIUtil.getNewFileChooser();
    
    private File archiveFile;
    
    @Inject
    public RunTestDialog(RESTUserInterface ui){
        super(ui.getFrame(), true);
        this.setTitle("Run Test");
        me = this;
    }
    
    @PostConstruct
    protected void init(){
        jfc.addChoosableFileFilter(new RCFileFilter(FileType.ARCHIVE_EXT));
        
        ButtonGroup group = new ButtonGroup();
        group.add(jrb_archive);
        group.add(jrb_last);
        jrb_archive.setSelected(true);
        
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jrb_archive.isSelected()){
                    jb_archive_browse.setEnabled(true);
                }
                else{
                    jb_archive_browse.setEnabled(false);
                }
            }
        };
        jrb_archive.addActionListener(al);
        jrb_last.addActionListener(al);
        
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        
        JPanel jp_center = new JPanel();
        jp_center.setBorder(BorderFactory.createTitledBorder("Run Test"));
        jp_center.setLayout(new GridLayout(3, 1));
        jp_center.add(jrb_archive);
        JPanel jp_center_file = new JPanel();
        jp_center_file.setLayout(new FlowLayout());
        jtf_archive.setColumns(24);
        jtf_archive.setEditable(false);
        jp_center_file.add(jtf_archive);
        jb_archive_browse.setMnemonic('b');
        jb_archive_browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File f = ui.getOpenFile(FileChooserType.OPEN_ARCHIVE, me);
                if(f == null){ // Cancel pressed
                    return;
                }
                archiveFile = f;
                jtf_archive.setText(archiveFile.getAbsolutePath());
            }
        });
        jp_center_file.add(jb_archive_browse);
        jp_center.add(jp_center_file);
        jp_center.add(jrb_last);
        c.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jb_next.setMnemonic('n');
        getRootPane().setDefaultButton(jb_next);
        jb_next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jb_nextAction();
            }
        });
        jp_south.add(jb_next);
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        jp_south.add(jb_cancel);
        c.add(jp_south, BorderLayout.SOUTH);
        
        // pack the dialog:
        this.pack();
    }
    
    private void jb_nextAction(){
        try{
            Request request = null;
            Response response = null;
            if(jrb_archive.isSelected()){
                if(archiveFile == null){
                    JOptionPane.showMessageDialog(ui.getFrame(),
                            "Please select a file!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ReqResBean req_res = Util.getReqResArchive(archiveFile);
                request = req_res.getRequestBean();
                response = req_res.getResponseBean();
            }
            else{
                request = view.getLastRequest();
                response = view.getLastResponse();
                if(request == null || response == null){
                    JOptionPane.showMessageDialog(me,
                            "No last Request/Response available!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            me.setVisible(false);
            view.runClonedRequestTest(request, response);
        }
        catch(IOException ex){
            view.showError(Util.getStackTrace(ex));
        }
        catch(XMLException ex){
            view.showError(Util.getStackTrace(ex));
        }
    }

    @Override
    public void doEscape(AWTEvent event) {
        close();
    }
    
    private void close(){
        me.setVisible(false);
    }

}
