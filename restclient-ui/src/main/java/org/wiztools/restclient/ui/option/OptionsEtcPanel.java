package org.wiztools.restclient.ui.option;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;

import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.ui.RESTViewImpl;

/**
 *
 * @author subwiz
 */
public class OptionsEtcPanel extends JPanel implements IOptionsPanel{

    private static final Logger LOG = Logger.getLogger(OptionsEtcPanel.class.getName());
    private static final String INDENT_KEY = "response.body.indent";
    private static final String SYNTAX_COLOR_RESPONSE = "response.body.syntax.color";
    private static final String SYNTAX_COLOR_REQUEST = "request.body.syntax.color";
    private static final String SCROLL_SPEED = "textarea.scrollspeed";

    private JCheckBox jcb_indentResponse = new JCheckBox("Auto-indent Response Body");
    private JCheckBox jcb_syntaxResponse = new JCheckBox("Default enable Response Body syntax coloring?");
    private JCheckBox jcb_syntaxRequest = new JCheckBox("Default enable Request Body syntax coloring?");
    private JSpinner js_scrollSpeed = new JSpinner();

    OptionsEtcPanel(){
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        jcb_indentResponse.setMnemonic('a');

        jcb_syntaxRequest.setToolTipText("Requires RESTClient restart!");
        jcb_syntaxResponse.setToolTipText("Requires RESTClient restart!");
        
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(4, 1));
        
        jp.add(jcb_indentResponse);
        jp.add(jcb_syntaxRequest);
        jp.add(jcb_syntaxResponse);
        JPanel jp_scrollSpeed = new JPanel(new BorderLayout());
        JPanel jp_scrollSpeed_inner = new JPanel(new FlowLayout());
        jp_scrollSpeed_inner.add(new JLabel("Text areas scroll speed"));
        jp_scrollSpeed_inner.add(js_scrollSpeed);
        jp_scrollSpeed.add(BorderLayout.WEST, jp_scrollSpeed_inner);
        jp.add(jp_scrollSpeed);
        ((JSpinner.DefaultEditor)js_scrollSpeed.getEditor()).getTextField().setColumns(2);
        
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

        int scrollSpeed = 1;
        String scrollSpeedStr = ServiceLocator.getInstance(IGlobalOptions.class).getProperty(SCROLL_SPEED);
        if (scrollSpeedStr != null) {
            try {
                scrollSpeed = Integer.parseInt(scrollSpeedStr);
            } catch(NumberFormatException ex) {
                // leave the default font size of 12
            }
        }
        js_scrollSpeed.setValue(scrollSpeed);

        ServiceLocator.getInstance(RESTViewImpl.class).setTextAreaScrollSpeed(getScrollSpeed());
    }

    @Override
    public void shutdownOptions() {
        ServiceLocator.getInstance(IGlobalOptions.class).setProperty(SCROLL_SPEED, String.valueOf(js_scrollSpeed.getValue()));
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

        options.setProperty(SCROLL_SPEED, String.valueOf(js_scrollSpeed.getValue()));
        ServiceLocator.getInstance(RESTViewImpl.class).setTextAreaScrollSpeed(getScrollSpeed());
        return true;
    }

    @Override
    public boolean revertOptions() {
        initOptions();
        return true;
    }

    private int getScrollSpeed() {
        return (Integer) js_scrollSpeed.getValue();
    }
}
