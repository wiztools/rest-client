package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ReqEntityFilePart;
import org.wiztools.restclient.bean.ReqEntityFilePartBean;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class AddMultipartFileDialog extends AddMultipartBaseDialog {
    
    @Inject
    private ContentTypeCharsetComponent jp_contentType;
    
    private final JTextField jtf_name = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    private final JTextField jtf_fileName = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    private final JTextField jtf_file = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    
    private final JButton jb_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    
    private final JButton jb_add = new JButton("Add");
    private final JButton jb_addAndClose = new JButton("Add & close");
    private final JButton jb_cancel = new JButton("Cancel");

    @Inject
    public AddMultipartFileDialog(RESTUserInterface rest_ui) {
        super(rest_ui);
        
        setTitle("Add Multipart File");
    }
    
    @PostConstruct
    protected void init() {
        // Button listeners:
        jb_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        jb_addAndClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAndClose();
            }
        });
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        
        jb_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        
        // Default button:
        getRootPane().setDefaultButton(jb_add);
        
        // Layout:
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        { // Center
            JPanel jp = new JPanel(new BorderLayout());
            
            JPanel jp_west = new JPanel(new GridLayout(4, 2));
            jp_west.add(new JLabel(" Content type: "));
            jp_west.add(new JLabel(" Name: "));
            jp_west.add(new JLabel(" File name: "));
            jp_west.add(new JLabel(" File: "));
            jp.add(jp_west, BorderLayout.WEST);
            
            JPanel jp_center = new JPanel(new GridLayout(4, 2));
            jp_center.add(jp_contentType.getComponent());
            jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_name));
            jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_fileName));
            JPanel jp_file = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp_file.add(jtf_file);
            jp_file.add(jb_file);
            jp_center.add(jp_file);
            jp.add(jp_center, BorderLayout.CENTER);
            
            c.add(jp, BorderLayout.CENTER);
        }
        
        { // South
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout(FlowLayout.RIGHT));
            jp.add(jb_cancel);
            jp.add(jb_add);
            jp.add(jb_addAndClose);
            c.add(jp, BorderLayout.SOUTH);
        }
        
        pack();
    }
    
    private void selectFile() {
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_REQUEST_BODY);
        if(f == null){ // Pressed cancel?
            return;
        }
        if(!f.canRead()){
            JOptionPane.showMessageDialog(rest_ui.getFrame(),
                    "File not readable: " + f.getAbsolutePath(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Content type charset correction:
        ContentTypeSelectorOnFile.select(jp_contentType, f, this);
        
        // Set filename:
        if(StringUtil.isEmpty(jtf_fileName.getText())) {
            jtf_fileName.setText(f.getName());
        }
        
        // Set file:
        jtf_file.setText(f.getAbsolutePath());
    }
    
    private boolean add() {
        // Validation:
        if(StringUtil.isEmpty(jtf_name.getText())) {
            JOptionPane.showMessageDialog(this,
                    "Name must be present!",
                    "Validation: name empty!",
                    JOptionPane.ERROR_MESSAGE);
            jtf_name.requestFocus();
            return false;
        }
        if(StringUtil.isEmpty(jtf_fileName.getText())) {
            JOptionPane.showMessageDialog(this,
                    "File name must be present!",
                    "Validation: filename empty!",
                    JOptionPane.ERROR_MESSAGE);
            jtf_fileName.requestFocus();
            return false;
        }
        
        // Read values:
        final String name = jtf_name.getText();
        final String fileName = jtf_fileName.getText();
        final ContentType ct = jp_contentType.getContentType();
        final File file = new File(jtf_file.getText());
        final ReqEntityFilePart part = new ReqEntityFilePartBean(name, fileName, ct, file);
        
        // Trigger all listeners:
        for(AddMultipartPartListener l: listeners) {
            l.addPart(part);
        }
        
        // Clear:
        clear();
        
        // Focus:
        jb_file.requestFocus();
        
        return true;
    }
    
    private void addAndClose() {
        if(add()) {
            setVisible(false);
        }
    }
    
    private void cancel() {
        clear();
        setVisible(false);
    }
    
    @Override
    public void clear() {
        jp_contentType.clear();
        jtf_fileName.setText("");
        jtf_file.setText("");
    }

    @Override
    public void setVisible(boolean boo) {
        jp_contentType.requestFocus();
        super.setVisible(boo);
    }
}
