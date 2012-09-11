package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ReqEntityStringPart;
import org.wiztools.restclient.bean.ReqEntityStringPartBean;
import org.wiztools.restclient.ui.RESTUserInterface;

/**
 *
 * @author subwiz
 */
public class AddMultipartStringDialog extends AddMultipartBaseDialog {
    
    @Inject
    ContentTypeCharsetComponent jp_contentType;
    
    private RSyntaxTextArea jta = new RSyntaxTextArea();
    private JButton jb_ok = new JButton("Ok");
    private JButton jb_cancel = new JButton("Cancel");

    @Inject
    public AddMultipartStringDialog(RESTUserInterface rest_ui) {
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
                    ReqEntityStringPart part = new ReqEntityStringPartBean(
                            "subhash", ct, jta.getText()); // TODO
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
            JScrollPane jsp = new JScrollPane(jta);
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
