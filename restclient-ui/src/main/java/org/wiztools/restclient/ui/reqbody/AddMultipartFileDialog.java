package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ReqEntityFileBean;
import org.wiztools.restclient.bean.ReqEntityFilePart;
import org.wiztools.restclient.bean.ReqEntityFilePartBean;
import org.wiztools.restclient.ui.RESTUserInterface;

/**
 *
 * @author subwiz
 */
public class AddMultipartFileDialog extends AddMultipartBaseDialog {
    
    @Inject
    ContentTypeCharsetComponent jp_contentType;
    
    private JTextField jtf = new JTextField();
    private JButton jb_ok = new JButton("Ok");
    private JButton jb_cancel = new JButton("Cancel");

    @Inject
    public AddMultipartFileDialog(RESTUserInterface rest_ui) {
        super(rest_ui);
    }
    
    @PostConstruct
    protected void init() {
        // Button listeners:
        jb_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(AddMultipartPartListener l: listeners) {
                    ContentType ct = jp_contentType.getContentType();
                    File file = new File(jtf.getText());
                    ReqEntityFilePart part = new ReqEntityFilePartBean(file);
                    l.addPart(part);
                }
            }
        });
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        // Default button:
        getRootPane().setDefaultButton(jb_ok);
        
        // Layout:
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        c.add(jp_contentType.getComponent(), BorderLayout.NORTH);
        
        { // Center
            JScrollPane jsp = new JScrollPane(jtf);
            c.add(jsp, BorderLayout.CENTER);
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
}
