package org.wiztools.restclient.ui.reqgo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
@Singleton
public class ReqUrlGoPanelImpl extends JPanel implements ReqUrlGoPanel {
    
    private static final Logger LOG = Logger.getLogger(ReqUrlGoPanelImpl.class.getName());
    
    @Inject private RESTUserInterface rest_ui;
    @Inject private UrlComboBox jcb_url;
    
    private final ImageIcon icon_go = UIUtil.getIconFromClasspath("org/wiztools/restclient/go.png");
    private final ImageIcon icon_stop = UIUtil.getIconFromClasspath("org/wiztools/restclient/stop.png");
    
    private static final String TEXT_GO = "Go!";
    private static final String TEXT_STOP = "Stop!";
    
    private final JButton jb_request = new JButton(icon_go);
    
    private final List<ActionListener> listeners = new ArrayList<>();
    
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
        if(StringUtil.isNotEmpty((String)jcb_url.getSelectedItem())) {
            jcb_url.push();
        }
        
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
    public void clearHistory() {
        jcb_url.removeAllItems();
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
