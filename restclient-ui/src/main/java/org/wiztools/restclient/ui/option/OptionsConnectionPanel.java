package org.wiztools.restclient.ui.option;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;

/**
 *
 * @author subwiz
 */
public class OptionsConnectionPanel extends JPanel implements IOptionsPanel {
    private static final Logger LOG = Logger.getLogger(OptionsConnectionPanel.class.getName());
    
    private static final String PROP_PREFIX = "conn.options.";

    private static final String MINUTES = "Minutes";
    private static final String SECONDS = "Seconds";
    private static final String MILLISECONDS = "Milli-seconds";
    private final JRadioButton jrb_minutes = new JRadioButton(MINUTES);
    private final JRadioButton jrb_seconds = new JRadioButton(SECONDS);
    private final JRadioButton jrb_millisecs = new JRadioButton(MILLISECONDS);
    private final JFormattedTextField jftf_timeout = new JFormattedTextField(
            Integer.parseInt(
            ServiceLocator.getInstance(IGlobalOptions.class)
            .getProperty("request-timeout-in-millis")));
    
    // Holds the previous selection for convertion between units:
    private String lastSelected;
    
    // Last okyed
    private String ok_type = MILLISECONDS;
    private Integer ok_value = Integer.parseInt(
            ServiceLocator.getInstance(IGlobalOptions.class)
            .getProperty("request-timeout-in-millis"));

    public OptionsConnectionPanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb_minutes);
        bg.add(jrb_seconds);
        bg.add(jrb_millisecs);
        jrb_millisecs.setSelected(true);
        lastSelected = MILLISECONDS;

        ConvertListener al = new ConvertListener();
        jrb_minutes.addActionListener(al);
        jrb_seconds.addActionListener(al);
        jrb_millisecs.addActionListener(al);

        JPanel jp_radio = new JPanel();
        jp_radio.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp_radio.add(jrb_minutes);
        jp_radio.add(jrb_seconds);
        jp_radio.add(jrb_millisecs);

        JPanel jp_timeout = new JPanel();
        jp_timeout.setLayout(new FlowLayout(FlowLayout.LEFT));
        jftf_timeout.setColumns(20);
        jp_timeout.add(jftf_timeout);
        
        JPanel jp_label_grid = new JPanel();
        jp_label_grid.setLayout(new GridLayout(2, 1));
        jp_label_grid.add(new JLabel("Timeout in: "));
        jp_label_grid.add(new JLabel("Value: "));
        
        JPanel jp_input_grid = new JPanel();
        jp_input_grid.setLayout(new GridLayout(2, 1));
        jp_input_grid.add(jp_radio);
        jp_input_grid.add(jp_timeout);
        
        JPanel jp_encp = this;
        jp_encp.setLayout(new BorderLayout());
        jp_encp.add(jp_label_grid, BorderLayout.WEST);
        jp_encp.add(jp_input_grid, BorderLayout.CENTER);
        jp_encp.setBorder(BorderFactory.createTitledBorder("Request Timeout"));
    }
    
    int getTimeoutInMillis(){
        int value = (Integer)jftf_timeout.getValue();
        
        if(jrb_seconds.isSelected()){
            return value * 1000;
        }
        else if(jrb_minutes.isSelected()){
            return value * 60 * 1000;
        }
        // is milli-seconds:
        return value;
    }

    class ConvertListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jrb_millisecs.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    return;
                }
                else if (lastSelected.equals(SECONDS)) {
                    // Convert seconds to millis:
                    int valueInSecs = (Integer) jftf_timeout.getValue();
                    int valueInMillis = valueInSecs * 1000;
                    jftf_timeout.setValue(valueInMillis);
                }
                else if (lastSelected.equals(MINUTES)) {
                    // Convert mins to millis:
                    int valueInMins = (Integer) jftf_timeout.getValue();
                    int valueInMillis = valueInMins * 60 * 1000;
                    jftf_timeout.setValue(valueInMillis);
                }
                // Update the lastSelected:
                lastSelected = MILLISECONDS;
            }
            else if (jrb_seconds.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    // Convert millis to seconds:
                    int valueInMillis = (Integer) jftf_timeout.getValue();
                    int valueInSecs = valueInMillis / 1000;
                    jftf_timeout.setValue(valueInSecs);
                }
                else if (lastSelected.equals(SECONDS)) {
                    return;
                }
                else if (lastSelected.equals(MINUTES)) {
                    // Convert mins to seconds:
                    int valueInMins = (Integer) jftf_timeout.getValue();
                    int valueInSecs = valueInMins * 60;
                    jftf_timeout.setValue(valueInSecs);
                }
                // Update the lastSelected:
                lastSelected = SECONDS;
            }
            else if (jrb_minutes.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    // Convert millis to minutes
                    int valueInMillis = (Integer) jftf_timeout.getValue();
                    int valueInMins = valueInMillis / (1000 * 60);
                    jftf_timeout.setValue(valueInMins);
                }
                else if (lastSelected.equals(SECONDS)) {
                    // Convert seconds to minutes:
                    int valueInSecs = (Integer) jftf_timeout.getValue();
                    int valueInMins = valueInSecs / 60;
                    jftf_timeout.setValue(valueInMins);
                }
                else if (lastSelected.equals(MINUTES)) {
                    return;
                }
                // Update the lastSelected:
                lastSelected = MINUTES;
            }
        }
    }

    @Override
    public List<String> validateInput() {
        return null;
    }
    
    @Override
    public boolean saveOptions(){
        int reqTimeout = (Integer)jftf_timeout.getValue();
        
        ok_type = MILLISECONDS;
        if(jrb_minutes.isSelected()){
            reqTimeout = 60 * 1000 * reqTimeout;
            ok_type = MINUTES;
        }
        else if(jrb_seconds.isSelected()){
            reqTimeout = 1000 * reqTimeout;
            ok_type = SECONDS;
        }
        ok_value = reqTimeout;
        
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        options.setProperty("request-timeout-in-millis", String.valueOf(reqTimeout));
        
        return true;
    }
    
    private void setUIFromCache(){
        if(ok_type.equals(MILLISECONDS)){
            jrb_millisecs.setSelected(true);
        }
        else if(ok_type.equals(SECONDS)){
            jrb_seconds.setSelected(true);
        }
        else if(ok_type.equals(MINUTES)){
            jrb_minutes.setSelected(true);
        }
        jftf_timeout.setValue(ok_value);
    }
    
    @Override
    public boolean revertOptions(){
        setUIFromCache();
        return true;
    }

    @Override
    public void initOptions() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        try{
            String t = options.getProperty(PROP_PREFIX + "type");
            ok_type = t==null? ok_type: t;
            ok_value = Integer.parseInt(options.getProperty(PROP_PREFIX + "value"));
            // ok_value is always stored in milli-secs, so convertion is necessary:
            if(SECONDS.equals(ok_type)){
                lastSelected = SECONDS;
                ok_value = ok_value / 1000;
            }
            else if(MINUTES.equals(ok_type)){
                lastSelected = MINUTES;
                ok_value = (ok_value / 1000) / 60;
            }
            setUIFromCache();
        }
        catch(Exception ex){
            LOG.info("Could not load Connection options from property.");
        }
    }

    @Override
    public void shutdownOptions() {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        options.setProperty(PROP_PREFIX + "type", ok_type);
        options.setProperty(PROP_PREFIX + "value", String.valueOf(ok_value));
    }
}
