package org.wiztools.restclient.ui.reqgo;

import com.jidesoft.swing.AutoCompletion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ReqUrlGoPanelImpl extends JPanel implements ReqUrlGoPanel {
    
    private static final Logger LOG = Logger.getLogger(ReqUrlGoPanelImpl.class.getName());
    
    @Inject private RESTUserInterface rest_ui;
    
    private ImageIcon icon_go = UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png");
    private ImageIcon icon_stop = UIUtil.getIconFromClasspath("org/wiztools/restclient/stop.png");
    
    private static final String TEXT_GO = "Go!";
    private static final String TEXT_STOP = "Stop!";
    
    private final JComboBox jcb_url = new JComboBox();
    
    private final JButton jb_request = new JButton(icon_go);
    
    private final List<ActionListener> listeners = new ArrayList<ActionListener>();
    
    @PostConstruct
    protected void init() {
        { // Keystroke for focusing on the address bar:
            final KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_L,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            final String actionName = "org.wiztools.restclient:ADDRESS_FOCUS";
            jcb_url.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(ks, actionName);
            jcb_url.getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    jcb_url.requestFocus();
                }
            });
        }
        
        // Layout follows:
        
        // West:
        JLabel jl_url = new JLabel("URL: ");
        jl_url.setLabelFor(jcb_url);
        jl_url.setDisplayedMnemonic('u');
        rest_ui.getFrame().getRootPane().setDefaultButton(jb_request);
        
        setLayout(new BorderLayout(RESTView.BORDER_WIDTH, 0));
        
        add(jl_url, BorderLayout.WEST);
        
        // Center:
        jcb_url.setToolTipText("URL");
        jcb_url.setEditable(true);
        jcb_url.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ((JTextField) jcb_url.getEditor().getEditorComponent()).selectAll();
            }
        });
        AutoCompletion ac = new AutoCompletion(jcb_url);
        ac.setStrict(false);
        ac.setStrictCompletion(false);
        add(jcb_url, BorderLayout.CENTER);
        
        // East:
        jb_request.setToolTipText(TEXT_GO);
        rest_ui.getFrame().getRootPane().setDefaultButton(jb_request);
        jb_request.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                jb_requestActionPerformed();
            }
        });
        add(jb_request, BorderLayout.EAST);
    }
    
    @PostConstruct
    protected void loadComboHistory() {
        try {
            List<String> urls = UrlListPersistUtil.load();
            if(!urls.isEmpty()) {
                for(String url: urls) {
                    jcb_url.addItem(url);
                }
            }
        }
        catch(IOException ex) {
            LOG.log(Level.WARNING, null, ex);
        }
    }
    
    @PostConstruct
    protected void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                List<String> urls = new ArrayList<String>();
                for(int i=0; i<jcb_url.getItemCount(); i++) {
                    String url = (String) jcb_url.getItemAt(i);
                    urls.add(url);
                }
                try {
                    UrlListPersistUtil.persist(urls);
                }
                catch(IOException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
            }
        });
    }

    @Override
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public ACTION_TYPE getActionType() {
        if(jb_request.getIcon() == icon_go){
            return ACTION_TYPE.GO;
        }
        else {
            return ACTION_TYPE.CANCEL;
        }
    }
    
    private void jb_requestActionPerformed() {
        final Object item = jcb_url.getSelectedItem();
        final int count = jcb_url.getItemCount();
        final LinkedList l = new LinkedList();
        for(int i=0; i<count; i++){
            l.add(jcb_url.getItemAt(i));
        }
        if(l.contains(item)){ // Item already present
            // Remove and add to bring it to the top
            // l.remove(item);
            // l.addFirst(item);
            // System.out.println("Removing and inserting at top");
            jcb_url.removeItem(item);
            jcb_url.insertItemAt(item, 0);
        }
        else{ // Add new item
            if(((String)item).trim().length() != 0 ) {
                // The total number of items should not exceed 20
                if(count > 19){
                    // Remove last item to give place
                    // to new one
                    //l.removeLast();
                    jcb_url.removeItemAt(count - 1);
                }
                //l.addFirst(item);
                jcb_url.insertItemAt(item, 0);
            }
        }
        // make the selected item is the item we want
        jcb_url.setSelectedItem(item);
        for(ActionListener listener: listeners) {
            listener.actionPerformed(null);
        }
    }
    
    @Override
    public String getUrlString() {
        return (String) jcb_url.getSelectedItem();
    }

    @Override
    public void setUrlString(String url) {
        jcb_url.setSelectedItem(url);
    }

    @Override
    public boolean isSslUrl() {
        try {
            URL url = new URL((String) jcb_url.getSelectedItem());
            if(url.getProtocol().equalsIgnoreCase("https")) {
                return true;
            }
        }
        catch(MalformedURLException ex) {
            // return default value!
        }
        return false;
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        
        jcb_url.requestFocus();
    }

    @Override
    public void setAsRunning() {
        jb_request.setIcon(icon_stop);
        jb_request.setToolTipText(TEXT_STOP);
    }

    @Override
    public void setAsIdle() {
        jb_request.setIcon(icon_go);
        jb_request.setToolTipText(TEXT_GO);
    }

    @Override
    public boolean isIdle() {
        return jb_request.getIcon() == icon_go;
    }

    @Override
    public boolean isRunning() {
        return jb_request.getIcon() == icon_stop;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        jcb_url.setSelectedItem(null);
    }
}
