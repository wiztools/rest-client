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
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class AddMultipartFileDialog extends AddMultipartBaseDialog {
    
    @Inject
    private ContentTypeCharsetComponent jp_contentType;
    
    private JTextField jtf_fileName = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    private JTextField jtf_file = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    private JButton jb_ok = new JButton("Ok");
    private JButton jb_cancel = new JButton("Cancel");

    @Inject
    public AddMultipartFileDialog(RESTUserInterface rest_ui) {
        super(rest_ui);
        
        setTitle("Add Multipart File");
    }
    
    @PostConstruct
    protected void init() {
        // Button listeners:
        jb_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        
        // Default button:
        getRootPane().setDefaultButton(jb_ok);
        
        // Layout:
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        { // Center
            JPanel jp = new JPanel(new BorderLayout());
            
            JPanel jp_west = new JPanel(new GridLayout(3, 2));
            jp_west.add(new JLabel(" Content type: "));
            jp_west.add(new JLabel(" File name: "));
            jp_west.add(new JLabel(" File: "));
            jp.add(jp_west, BorderLayout.WEST);
            
            JPanel jp_center = new JPanel(new GridLayout(3, 2));
            jp_center.add(jp_contentType.getComponent());
            jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_fileName));
            jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_file));
            jp.add(jp_center, BorderLayout.CENTER);
            
            c.add(jp, BorderLayout.CENTER);
        }
        
        { // South
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout(FlowLayout.RIGHT));
            jp.add(jb_cancel);
            jp.add(jb_ok);
            c.add(jp, BorderLayout.SOUTH);
        }
        
        pack();
    }
    
    private void ok() {
        // Validation:
        if(StringUtil.isEmpty(jtf_fileName.getText())) {
            JOptionPane.showMessageDialog(this,
                    "Name must be present!",
                    "Validation: name empty!",
                    JOptionPane.ERROR_MESSAGE);
            jtf_fileName.requestFocus();
            return;
        }
        
        // Read values:
        final String fileName = jtf_fileName.getText();
        final ContentType ct = jp_contentType.getContentType();
        final File file = new File(jtf_file.getText());
        
        // Trigger all listeners:
        for(AddMultipartPartListener l: listeners) {
            ReqEntityFilePart part = new ReqEntityFilePartBean(fileName, ct, file);
            l.addPart(part);
        }
        
        // Clear:
        clear();
        
        // Visibility:
        setVisible(false);
    }
    
    private void cancel() {
        setVisible(false);
    }
    
    @Override
    public void clear() {
        jp_contentType.clear();
        jtf_fileName.setText("");
        jtf_file.setText("");
    }
}
