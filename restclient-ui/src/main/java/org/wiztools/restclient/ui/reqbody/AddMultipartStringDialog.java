package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ReqEntityStringPart;
import org.wiztools.restclient.bean.ReqEntityStringPartBean;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class AddMultipartStringDialog extends AddMultipartBaseDialog {
    
    @Inject
    private ContentTypeCharsetComponent jp_contentType;
    
    private JTextField jtf_name = new JTextField(ContentTypeCharsetComponent.TEXT_FIELD_LENGTH);
    private RSyntaxTextArea jta_part = new RSyntaxTextArea(25, 60);
    private JButton jb_add = new JButton("Add");
    private JButton jb_addAndClose = new JButton("Add & close");
    private JButton jb_cancel = new JButton("Cancel");

    @Inject
    public AddMultipartStringDialog(RESTUserInterface rest_ui) {
        super(rest_ui);
        
        setTitle("Add Multipart String");
    }
    
    @PostConstruct
    protected void init() {
        // Text area:
        jta_part.setAntiAliasingEnabled(true);
        
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
        
        // Default button:
        getRootPane().setDefaultButton(jb_add);
        
        // Layout:
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        { // North:
            JPanel jp = new JPanel(new BorderLayout());
            { // West:
                JPanel jp_west = new JPanel(new GridLayout(2, 1));
                jp_west.add(new JLabel(" Content type: "));
                jp_west.add(new JLabel(" Name: "));
                jp.add(jp_west, BorderLayout.WEST);
            }
            { // Center:
                JPanel jp_center = new JPanel(new GridLayout(2, 1));
                jp_center.add(jp_contentType.getComponent());
                jp_center.add(UIUtil.getFlowLayoutPanelLeftAligned(jtf_name));
                jp.add(jp_center, BorderLayout.CENTER);
            }
            
            c.add(jp, BorderLayout.NORTH);
        }
        
        { // Center
            JScrollPane jsp = new JScrollPane(jta_part);
            c.add(jsp, BorderLayout.CENTER);
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
        
        // Fetch all values:
        final String name = jtf_name.getText();
        final ContentType ct = jp_contentType.getContentType();
        final String body = jta_part.getText();
        ReqEntityStringPart part = new ReqEntityStringPartBean(
                name, ct, body);
        
        // Call all listeners:
        for(AddMultipartPartListener l: listeners) {
            l.addPart(part);
        }

        // Clear:
        clear();
        
        // Focus:
        jtf_name.requestFocus();
        
        return true;
    }
    
    private void addAndClose() {
        if(add()) {
            // Set visible:
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
        jtf_name.setText("");
        jta_part.setText("");
    }
}
