package org.wiztools.restclient.ui.option;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;

/**
 *
 * @author subwiz
 */
public class OptionsEtcPanel extends JPanel implements IOptionsPanel{

    private static final Logger LOG = Logger.getLogger(OptionsEtcPanel.class.getName());
    private static final String INDENT_KEY = "response.body.indent";
    private static final String SYNTAX_COLOR_RESPONSE = "response.body.syntax.color";
    private static final String SYNTAX_COLOR_REQUEST = "request.body.syntax.color";

    private JCheckBox jcb_indentResponse = new JCheckBox("Auto-indent Response Body");
    private JCheckBox jcb_syntaxResponse = new JCheckBox("Default enable Response Body syntax coloring?");
    private JCheckBox jcb_syntaxRequest = new JCheckBox("Default enable Request Body syntax coloring?");

    OptionsEtcPanel(){
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        jcb_indentResponse.setMnemonic('a');

        jcb_syntaxRequest.setToolTipText("Requires RESTClient restart!");
        jcb_syntaxResponse.setToolTipText("Requires RESTClient restart!");
        
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(3, 1));
        
        jp.add(jcb_indentResponse);
        jp.add(jcb_syntaxRequest);
        jp.add(jcb_syntaxResponse);

        this.add(jp);
    }

    private boolean isIndentSetInGlobalOptions(){
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        String indentStr = options.getProperty(INDENT_KEY);
        return indentStr==null? false: (indentStr.equals("true")? true: false);
    }

    private boolean isResponseSyntaxSetInGlobalOptions() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        String syntaxResponse = options.getProperty(SYNTAX_COLOR_RESPONSE);
        return syntaxResponse==null? true: Boolean.valueOf(syntaxResponse);
    }

    private boolean isRequestSyntaxSetInGlobalOptions() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        String syntaxRequest = options.getProperty(SYNTAX_COLOR_REQUEST);
        return syntaxRequest==null? true: Boolean.valueOf(syntaxRequest);
    }

    @Override
    public void initOptions() {
        if(isIndentSetInGlobalOptions()){
            jcb_indentResponse.setSelected(true);
        }
        else{
            jcb_indentResponse.setSelected(false);
        }

        if(isResponseSyntaxSetInGlobalOptions()) {
            jcb_syntaxResponse.setSelected(true);
        }
        else {
            jcb_syntaxResponse.setSelected(false);
        }

        if(isRequestSyntaxSetInGlobalOptions()) {
            jcb_syntaxRequest.setSelected(true);
        }
        else {
            jcb_syntaxRequest.setSelected(false);
        }
    }

    @Override
    public void shutdownOptions() {
        // Not needed
    }

    @Override
    public List<String> validateInput() {
        return null;
    }

    @Override
    public boolean saveOptions() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        options.setProperty(INDENT_KEY, String.valueOf(jcb_indentResponse.isSelected()));

        options.setProperty(SYNTAX_COLOR_REQUEST, String.valueOf(jcb_syntaxRequest.isSelected()));
        options.setProperty(SYNTAX_COLOR_RESPONSE, String.valueOf(jcb_syntaxResponse.isSelected()));
        return true;
    }

    @Override
    public boolean revertOptions() {
        initOptions();
        return true;
    }

}
