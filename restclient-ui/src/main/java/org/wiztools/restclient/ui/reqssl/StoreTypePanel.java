package org.wiztools.restclient.ui.reqssl;

import java.awt.FlowLayout;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.wiztools.restclient.bean.KeyStoreType;

/**
 *
 * @author subwiz
 */
public class StoreTypePanel extends JPanel {
    
    private final JRadioButton jrb_jks = new JRadioButton(KeyStoreType.JKS.name());
    private final JRadioButton jrb_pkcs12 = new JRadioButton(KeyStoreType.PKCS12.name());
    private final JRadioButton jrb_pem = new JRadioButton(KeyStoreType.PEM.name());

    public StoreTypePanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        ButtonGroup grp = new ButtonGroup();
        grp.add(jrb_jks);
        grp.add(jrb_pkcs12);
        grp.add(jrb_pem);
        
        // JKS to be selected by default:
        jrb_jks.setSelected(true);
        
        add(jrb_jks);
        add(jrb_pkcs12);
        add(jrb_pem);
    }
    
    public KeyStoreType getSelectedKeyStoreType() {
        if(jrb_jks.isSelected()) {
            return KeyStoreType.JKS;
        }
        else if(jrb_pkcs12.isSelected()) {
            return KeyStoreType.PKCS12;
        }
        else if(jrb_pem.isSelected()) {
            return KeyStoreType.PEM;
        }
        return KeyStoreType.PEM;
    }
    
    public void setSelectedKeyStoreType(KeyStoreType type) {
        switch(type) {
            case JKS:
                jrb_jks.setSelected(true);
                break;
            case PKCS12:
                jrb_pkcs12.setSelected(true);
                break;
            case PEM:
                jrb_pem.setSelected(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown keystore-type: " + type);
        }
    }
    
    public void addItemListener(ItemListener listener, KeyStoreType ... types) {
        for(KeyStoreType type: types) {
            switch(type) {
                case JKS:
                    jrb_jks.addItemListener(listener);
                    break;
                case PKCS12:
                    jrb_pkcs12.addItemListener(listener);
                    break;
                case PEM:
                    jrb_pem.addItemListener(listener);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown keystore-type: " + type);
            }
        }
    }
}
