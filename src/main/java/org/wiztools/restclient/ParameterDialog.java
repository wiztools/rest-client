/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author schandran
 */
public class ParameterDialog extends JDialog {
    
    private final Frame frame;
    private final ParameterView view;
    private TwoColumnTablePanel jp_2col_center;
    
    private JButton jb_generate = new JButton("Generate");
    private JButton jb_cancel = new JButton("Cancel");
    
    ParameterDialog(Frame f, ParameterView view){
        // true means modal:
        super(f, true);
        this.frame = f;
        this.view = view;
        init();
    }
    
    private void init(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp_2col_center = new TwoColumnTablePanel(
                new String[]{"Key", "Value"}, frame);
        jp.add(jp_2col_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        jb_generate.setMnemonic('g');
        jb_generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionGenerate(event);
            }
        });
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionCancel(event);
            }
        });
        jp_south.add(jb_generate);
        jp_south.add(jb_cancel);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        this.setContentPane(jp);
        pack();
    }
    
    private void actionGenerate(ActionEvent e){
        TwoColumnTableModel model = jp_2col_center.getTableModel();
        Object[][] data = model.getData();
        Map<String, String> m = new LinkedHashMap<String, String>();
        for(int i=0; i<data.length; i++){
            m.put((String)data[i][0], (String)data[i][1]);
        }
        String generatedParam = Util.parameterEncode(m);
        view.setParameter(generatedParam);
        setVisible(false);
    }
    
    private void actionCancel(ActionEvent e){
        setVisible(false);
    }
}
