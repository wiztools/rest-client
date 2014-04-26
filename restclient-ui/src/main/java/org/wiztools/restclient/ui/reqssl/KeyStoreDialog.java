package org.wiztools.restclient.ui.reqssl;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.KeyStoreType;
import org.wiztools.restclient.bean.SSLKeyStore;
import org.wiztools.restclient.bean.SSLKeyStoreBean;
import org.wiztools.restclient.ui.EscapableDialog;
import org.wiztools.restclient.ui.FileChooserType;
import org.wiztools.restclient.ui.RCFileView;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class KeyStoreDialog extends EscapableDialog {
    
    @Inject private RESTUserInterface rest_ui;
    @Inject private RESTView view;
    
    private final List<KeyStoreListener> listeners = new ArrayList<>();
    
    private static final int auth_text_size = 20;
    
    private final StoreTypePanel jp_type = new StoreTypePanel();
    private final JTextField jtf_file = new JTextField(auth_text_size);
    private final JButton jb_browse = new JButton(
            UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private final JPasswordField jpf_pwd = new JPasswordField(auth_text_size);
    
    private final JButton jb_ok = new JButton("Ok");
    private final JButton jb_cancel = new JButton("Cancel");

    @Inject
    public KeyStoreDialog(RESTUserInterface rest_ui) {
        super(rest_ui.getFrame(), true);
        getRootPane().setDefaultButton(jb_ok);
        setLocationRelativeTo(rest_ui.getFrame());
    }
    
    @PostConstruct
    protected void init() {
        jb_browse.setToolTipText("Open file");
        jb_browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File f = rest_ui.getOpenFile(FileChooserType.OPEN_GENERIC);
                if(f == null) {
                    // do nothing--cancel pressed
                }
                else if(f.canRead()){
                    jtf_file.setText(f.getAbsolutePath());
                }
                else{
                    view.setStatusMessage("File cannot be read.");
                }
            }
        });
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
        
        // Layout:
        JPanel jp = new JPanel(new BorderLayout(RESTView.BORDER_WIDTH, 2));

        // Label column:
        JPanel jp_label = new JPanel(new GridLayout(3, 1));
        // 1, 2, 3:
        jp_label.add(new JLabel("Type: "));
        jp_label.add(new JLabel("File: "));
        jp_label.add(new JLabel("Password: "));
        
        jp.add(jp_label, BorderLayout.WEST);
        
        // Input column:
        JPanel jp_input = new JPanel(new GridLayout(3, 1));
        
        // 1:
        jp_input.add(jp_type);
        
        // 2:
        JPanel jp_file = UIUtil.getFlowLayoutPanelLeftAligned(jtf_file);
        jp_file.add(jb_browse);
        jp_input.add(jp_file);
        
        // 3:
        jp_input.add(UIUtil.getFlowLayoutPanelLeftAligned(jpf_pwd));
        
        jp.add(jp_input, BorderLayout.CENTER);
        
        // Ok button:
        JPanel jp_okCancel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jp_okCancel.add(jb_ok);
        jp_okCancel.add(jb_cancel);
        
        jp.add(jp_okCancel, BorderLayout.SOUTH);
        
        jp.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        setContentPane(jp);
        
        pack();
    }
    
    public void setKeyStore(SSLKeyStore store) {
        if(store != null) {
            jp_type.setSelectedKeyStoreType(store.getType());
            jtf_file.setText(store.getFile().getAbsolutePath());
            jpf_pwd.setText(new String(store.getPassword()));
        }
        else {
            clear();
        }
    }
    
    public SSLKeyStore getKeyStore() {
        final String filePath = jtf_file.getText();
        if(StringUtil.isNotEmpty(filePath)) {
            SSLKeyStoreBean out = new SSLKeyStoreBean();
            out.setType(jp_type.getSelectedKeyStoreType());
            out.setFile(new File(filePath));
            out.setPassword(jpf_pwd.getPassword());
            return out;
        }
        return null;
    }
    
    public void clear() {
        jp_type.setSelectedKeyStoreType(KeyStoreType.JKS);
        jtf_file.setText("");
        jpf_pwd.setText("");
    }
    
    public void addKeyStoreListener(KeyStoreListener listener) {
        listeners.add(listener);
    }
    
    private void ok() {
        final SSLKeyStore store = getKeyStore();
        for(KeyStoreListener listener: listeners) {
            listener.ok(store);
        }
        setVisible(false);
    }
    
    private void cancel() {
        for(KeyStoreListener listener: listeners) {
            listener.cancel();
        }
        setVisible(false);
    }

    @Override
    public void doEscape(AWTEvent event) {
        cancel();
    }
}
