package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import org.wiztools.restclient.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class ParameterDialog extends EscapableDialog {
    
    private final Frame frame;
    private final ParameterView view;
    private TwoColumnTablePanel jp_2col_center;
    
    private JButton jb_generate = new JButton("Generate");
    private JButton jb_cancel = new JButton("Cancel");
    
    private ParameterDialog me;
    
    ParameterDialog(Frame f, ParameterView view){
        // true means modal:
        super(f, true);
        this.setTitle("Insert Parameter");
        this.frame = f;
        this.view = view;
        this.me = this;
        init();
    }
    
    private void init(){
        JPanel jp = new JPanel();
        
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH));
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
        
        // Adding 100px (arbitary number) to show
        // decent view of empty table
        Dimension d = jp_2col_center.getPreferredSize();
        d.height = d.height + 100;
        jp_2col_center.setPreferredSize(d);
        
        this.setContentPane(jp);
        pack();
    }
    
    @Override
    public void doEscape(AWTEvent event){
        hideMe();
    }
    
    private void actionGenerate(ActionEvent e){
        TwoColumnTableModel model = jp_2col_center.getTableModel();
        Object[][] data = model.getData();
        if(data == null || data.length < 1){
            JOptionPane.showMessageDialog(me,
                    "Please add data!",
                    "Error: No data present!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Map<String, String> m = new LinkedHashMap<String, String>();
        for(int i=0; i<data.length; i++){
            m.put((String)data[i][0], (String)data[i][1]);
        }
        String generatedParam = Util.parameterEncode(m);
        view.setParameter(generatedParam);
        setVisible(false);
    }
    
    private void actionCancel(ActionEvent e){
        hideMe();
    }
    
    private void hideMe(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(false);
            }
        });
    }
}
