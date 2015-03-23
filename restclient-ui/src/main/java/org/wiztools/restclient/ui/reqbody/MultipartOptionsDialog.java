package org.wiztools.restclient.ui.reqbody;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.wiztools.restclient.bean.MultipartMode;
import org.wiztools.restclient.bean.MultipartSubtype;
import org.wiztools.restclient.ui.EscapableDialog;
import org.wiztools.restclient.ui.RESTUserInterface;

/**
 *
 * @author subwiz
 */
public class MultipartOptionsDialog extends EscapableDialog {
    
    protected final RESTUserInterface rest_ui;
    
    private static final MultipartMode DEFAULT_MODE = MultipartMode.BROWSER_COMPATIBLE;
    private static final MultipartSubtype DEFAULT_SUBTYPE = MultipartSubtype.FORM_DATA;
    
    private final JComboBox<MultipartSubtype> jcb_subtype = new JComboBox<>(MultipartSubtype.values());
    private final JComboBox<MultipartMode> jcb_mode = new JComboBox<>(MultipartMode.values());
    
    private final JButton jb_ok = new JButton("Ok");
    private final JButton jb_cancel = new JButton("Cancel");
    
    private MultipartSubtype selectedSubtype = DEFAULT_SUBTYPE;
    private MultipartMode selectedMode = DEFAULT_MODE;

    @Inject
    public MultipartOptionsDialog(RESTUserInterface ui) {
        super(ui.getFrame(), true);
        rest_ui = ui;
        
        setTitle("Multipart Options");
        
        initComponents();
        initLayout();
        
        this.pack();
    }
    
    private void initComponents() {
        jcb_subtype.setSelectedItem(DEFAULT_SUBTYPE);
        jcb_mode.setSelectedItem(DEFAULT_MODE);
        
        this.getRootPane().setDefaultButton(jb_ok);
        
        jb_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOk();
            }
        });
        
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
    }
    
    private void initLayout() {
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout(5, 5));
        
        // West:
        {
            JPanel jp = new JPanel(new GridLayout(2, 1));
            jp.add(new JLabel(" Subtype (multipart/?): "));
            jp.add(new JLabel(" Mode: "));
            
            c.add(jp, BorderLayout.WEST);
        }
        
        // Center:
        {   
            JPanel jp = new JPanel(new GridLayout(2, 1));
            jp.add(jcb_subtype);
            jp.add(jcb_mode);
            
            c.add(jp, BorderLayout.CENTER);
        }
        
        // South:
        {
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            jp.add(jb_cancel);
            jp.add(jb_ok);
            
            c.add(jp, BorderLayout.SOUTH);
        }
    }

    @Override
    public void doEscape(AWTEvent event) {
        onCancel();
    }
    
    private void onOk() {
        selectedMode = (MultipartMode) jcb_mode.getSelectedItem();
        selectedSubtype = (MultipartSubtype) jcb_subtype.getSelectedItem();
        
        this.setVisible(false);
    }
    
    private void onCancel() {
        jcb_mode.setSelectedItem(selectedMode);
        jcb_subtype.setSelectedItem(selectedSubtype);
        
        this.setVisible(false);
    }
    
    // clear:
    public void clear() {
        selectedMode = DEFAULT_MODE;
        selectedSubtype = DEFAULT_SUBTYPE;
        
        jcb_mode.setSelectedItem(DEFAULT_MODE);
        jcb_subtype.setSelectedItem(DEFAULT_SUBTYPE);
    }

    // Bean methods:
    public MultipartSubtype getSelectedSubtype() {
        return selectedSubtype;
    }

    public void setSelectedSubtype(MultipartSubtype selectedSubtype) {
        this.selectedSubtype = selectedSubtype;
        jcb_subtype.setSelectedItem(selectedSubtype);
    }

    public MultipartMode getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(MultipartMode selectedMode) {
        this.selectedMode = selectedMode;
        jcb_mode.setSelectedItem(selectedMode);
    }
}
