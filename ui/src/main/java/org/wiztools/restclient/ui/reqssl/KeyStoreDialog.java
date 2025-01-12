package org.wiztools.restclient.ui.reqssl;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import org.wiztools.restclient.ui.dnd.DndAction;
import org.wiztools.restclient.ui.dnd.FileDropTargetListener;

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
        // Init:
        jp_type.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    jpf_pwd.setEnabled(false);
                }
                else {
                    jpf_pwd.setEnabled(true);
                }
            }
        }, KeyStoreType.PEM);
        
        // DnD:
        FileDropTargetListener dndListener = new FileDropTargetListener();
        dndListener.addDndAction(new DndAction() {
            @Override
            public void onDrop(List<File> files) {
                loadFile(files.get(0));
            }
        });
        new DropTarget(jtf_file, dndListener);
        new DropTarget(jb_browse, dndListener);
        
        jb_browse.setToolTipText("Open file");
        jb_browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
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
        jp_label.add(new JLabel("Format: "));
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
    
    private void loadFile() {
        File f = rest_ui.getOpenFile(FileChooserType.OPEN_GENERIC);
        loadFile(f);
    }
    
    private static final String fmtChangeMsg = "Keystore seems to be in {0} format.\nWant to update the format to {0}?";
    private static final String fmtChangeDialogTitle = "Change `Format' to {0}?";
    
    private void storeTypeUsingDetectedExtn(KeyStoreType detectedType) {
        if(jp_type.getSelectedKeyStoreType() != detectedType) {
            final int result = JOptionPane.showConfirmDialog(this,
                    MessageFormat.format(fmtChangeMsg, detectedType),
                    MessageFormat.format(fmtChangeDialogTitle, detectedType),
                    JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION) {
                jp_type.setSelectedKeyStoreType(detectedType);
            }
        }
    }
    
    private void loadFile(File f) {
        if(f == null) {
            // do nothing--cancel pressed
        }
        else if(f.canRead()) {
            final String fileName = f.getName();
            final KeyStoreType detectedStoreType = KeyStoreType.detectByExtn(fileName);
            if(detectedStoreType != null) {
                storeTypeUsingDetectedExtn(detectedStoreType);
            }
            jtf_file.setText(f.getAbsolutePath());
        }
        else {
            view.setStatusMessage("File cannot be read.");
        }
    }
    
    public void setKeyStore(SSLKeyStore store) {
        if(store != null) {
            jp_type.setSelectedKeyStoreType(store.getType());
            jtf_file.setText(store.getFile().getAbsolutePath());
            if(store.getType() != KeyStoreType.PEM)
                jpf_pwd.setText(new String(store.getPassword()));
            else
                jpf_pwd.setText("");
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
        if(store == null) {
            JOptionPane.showMessageDialog(this,
                    "One or more required details not provided.",
                    "Validation error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        for(KeyStoreListener listener: listeners) {
            listener.onOk(store);
        }
        setVisible(false);
    }
    
    private void cancel() {
        for(KeyStoreListener listener: listeners) {
            listener.onCancel();
        }
        setVisible(false);
    }

    @Override
    public void doEscape(AWTEvent event) {
        cancel();
    }
}
