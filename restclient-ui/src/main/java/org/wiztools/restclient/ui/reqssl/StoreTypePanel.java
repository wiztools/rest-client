package org.wiztools.restclient.ui.reqssl;

import java.awt.FlowLayout;
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

    public StoreTypePanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        ButtonGroup grp = new ButtonGroup();
        grp.add(jrb_jks);
        grp.add(jrb_pkcs12);
        
        // JKS to be selected by default:
        jrb_jks.setSelected(true);
        
        add(jrb_jks);
        add(jrb_pkcs12);
    }
    
    public KeyStoreType getSelectedKeyStoreType() {
        return jrb_jks.isSelected()? KeyStoreType.JKS: KeyStoreType.PKCS12;
    }
    
    public void setSelectedKeyStoreType(KeyStoreType type) {
        switch(type) {
            case JKS:
                jrb_jks.setSelected(true);
                break;
            case PKCS12:
                jrb_pkcs12.setSelected(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown keystore-type: " + type);
        }
    }
}
