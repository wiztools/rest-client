/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Subhash
 */
public class OptionsConnectionPanel extends JPanel {

    private static final Integer DEFAULT_TIMEOUT_MILLIS = new Integer(60000);
    private static final String MINUTES = "Minutes";
    private static final String SECONDS = "Seconds";
    private static final String MILLISECONDS = "Milli-seconds";
    private JRadioButton jrb_minutes = new JRadioButton(MINUTES);
    private JRadioButton jrb_seconds = new JRadioButton(SECONDS);
    private JRadioButton jrb_millisecs = new JRadioButton(MILLISECONDS);
    private JFormattedTextField jftf_timeout = new JFormattedTextField(DEFAULT_TIMEOUT_MILLIS);
    private String lastSelected;

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

        public void actionPerformed(ActionEvent e) {
            if (jrb_millisecs.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    return;
                } else if (lastSelected.equals(SECONDS)) {
                    // Convert seconds to millis:
                    int valueInSecs = (Integer) jftf_timeout.getValue();
                    int valueInMillis = valueInSecs * 1000;
                    jftf_timeout.setValue(valueInMillis);
                } else if (lastSelected.equals(MINUTES)) {
                    // Convert mins to millis:
                    int valueInMins = (Integer) jftf_timeout.getValue();
                    int valueInMillis = valueInMins * 60 * 1000;
                    jftf_timeout.setValue(valueInMillis);
                }
                // Update the lastSelected:
                lastSelected = MILLISECONDS;
            } else if (jrb_seconds.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    // Convert millis to seconds:
                    int valueInMillis = (Integer) jftf_timeout.getValue();
                    int valueInSecs = valueInMillis / 1000;
                    jftf_timeout.setValue(valueInSecs);
                } else if (lastSelected.equals(SECONDS)) {
                    return;
                } else if (lastSelected.equals(MINUTES)) {
                    // Convert mins to seconds:
                    int valueInMins = (Integer) jftf_timeout.getValue();
                    int valueInSecs = valueInMins * 60;
                    jftf_timeout.setValue(valueInSecs);
                }
                // Update the lastSelected:
                lastSelected = SECONDS;
            } else if (jrb_minutes.isSelected()) {
                if (lastSelected.equals(MILLISECONDS)) {
                    // Convert millis to minutes
                    int valueInMillis = (Integer) jftf_timeout.getValue();
                    int valueInMins = valueInMillis / (1000 * 60);
                    jftf_timeout.setValue(valueInMins);
                } else if (lastSelected.equals(SECONDS)) {
                    // Convert seconds to minutes:
                    int valueInSecs = (Integer) jftf_timeout.getValue();
                    int valueInMins = valueInSecs / 60;
                    jftf_timeout.setValue(valueInMins);
                } else if (lastSelected.equals(MINUTES)) {
                    return;
                }
                // Update the lastSelected:
                lastSelected = MINUTES;
            }
        }
    }
}
