package org.wiztools.restclient.ui.reqgo;

import com.jidesoft.swing.AutoCompletion;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.wiztools.commons.StringUtil;

/**
 *
 * @author subwiz
 */
public class UrlComboBox extends JComboBox<String> {
    
    private static final Logger LOG = Logger.getLogger(UrlComboBox.class.getName());
    
    private final int URL_COUNT_SIZE = 20;

    @Inject
    private ReqUrlGoPanel reqGo;

    public UrlComboBox() {
        setToolTipText("URL");
        setEditable(true);
        final JTextField editorComponent = (JTextField) getEditor().getEditorComponent();
        editorComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                editorComponent.selectAll();
            }
        });
        
        AutoCompletion ac = new AutoCompletion(this);
        ac.setStrict(false);
        ac.setStrictCompletion(false);
    }
    
    @PostConstruct
    protected void loadComboHistory() {
        try {
            List<String> urls = UrlListPersistUtil.load();
            if(!urls.isEmpty()) {
                // We need dimension for Issue 196:
                final Dimension d = this.getPreferredSize();
                
                for(String url: urls) {
                    this.addItem(url);
                }
                
                // Set the dimension for Issue 196:
                this.setPreferredSize(d);
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
                List<String> urls = new ArrayList<>();
                for(int i=0; i < getItemCount(); i++) {
                    String url = (String) getItemAt(i);
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
    
    public void push() {
        final String item = (String) getSelectedItem();

        final int count = getItemCount();
        final LinkedList<String> l = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            l.add(getItemAt(i));
        }
        if (l.contains(item)) { // Item already present
            // Remove and add to bring it to the top
            l.remove(item);
        }

        l.add(0, item);
        // do not need trim events, to motify like this is to
		// avoid trim twice IntemChange Event!
        reqGo.setTrimFlag(false);
        removeAllItems();
        for (int i = 0; i < Math.min(l.size(), URL_COUNT_SIZE); i++) {
            reqGo.setTrimFlag(false);
            addItem(l.get(i));
        }

        // make the selected item is the item we want
        reqGo.setTrimFlag(false);
        setSelectedItem(item);
    }
}
