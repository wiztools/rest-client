package org.wiztools.restclient.ui;

import java.awt.FlowLayout;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.commons.Implementation;

/**
 *
 * @author subwiz
 */
class OptionsEtcPanel extends JPanel implements IOptionsPanel{

    private static final Logger LOG = Logger.getLogger(OptionsEtcPanel.class.getName());
    private static final String INDENT_KEY = "response.body.indent";

    private JCheckBox jcb = new JCheckBox("Auto-indent Response Body");

    OptionsEtcPanel(){
        JPanel jp = this;
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        jcb.setMnemonic('a');
        jp.add(jcb);
    }

    private boolean isIndentSetInGlobalOptions(){
        IGlobalOptions options = Implementation.of(IGlobalOptions.class);
        String indentStr = options.getProperty(INDENT_KEY);
        return indentStr==null? false: (indentStr.equals("true")? true: false);
    }

    public void initOptions() {
        if(isIndentSetInGlobalOptions()){
            jcb.setSelected(true);
        }
        else{
            jcb.setSelected(false);
        }
    }

    public void shutdownOptions() {
        // Not needed
    }

    public List<String> validateInput() {
        return null;
    }

    public boolean saveOptions() {
        IGlobalOptions options = Implementation.of(IGlobalOptions.class);
        if(jcb.isSelected()){
            options.setProperty(INDENT_KEY, String.valueOf(true));
        }
        else{
            options.setProperty(INDENT_KEY, String.valueOf(false));
        }
        return true;
    }

    public boolean revertOptions() {
        if(isIndentSetInGlobalOptions()){
            jcb.setSelected(true);
        }
        else{
            jcb.setSelected(false);
        }
        return true;
    }

}
