/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public final class TwoColumnTablePanel extends JPanel {
    
    private TwoColumnTableModel model;
    private Dimension tableDimension;
    
    public TwoColumnTableModel getTableModel(){
        return model;
    }

    public TwoColumnTablePanel(final String[] title, final Frame frame) {
        
        // Create JTable
        final JTable jt = new JTable();
        
        // Set the size
        Dimension d = jt.getPreferredSize();
        d.height = d.height / 2;
        jt.setPreferredScrollableViewportSize(d);
        tableDimension = d;
        
        // Create and set the table model
        model = new TwoColumnTableModel(title);
        jt.setModel(model);
        
        // Create Popupmenu
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem jmi_delete = new JMenuItem("Delete");
        jmi_delete.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        int selectionCount = jt.getSelectedRowCount();
                        if(selectionCount > 0){
                            int[] rows = jt.getSelectedRows();
                            Arrays.sort(rows);
                            for(int i=rows.length-1; i>=0; i--){
                                model.deleteRow(rows[i]);
                            }
                        }
                    }
                });
            }
        });
        popupMenu.add(jmi_delete);
        
        // Attach popup menu
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            private void showPopup(MouseEvent e) {
                if(jt.getSelectedRowCount() == 0){
                    // No table row selected
                    return;
                }
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // Create the interface
        JPanel jp = this;
        jp.setLayout(new BorderLayout());
        
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_key = new JLabel("Key: ");
        JLabel jl_value = new JLabel("Value: ");
        final int TEXT_FIELD_SIZE = 12;
        final JTextField jtf_key = new JTextField(TEXT_FIELD_SIZE);
        final JTextField jtf_value = new JTextField(TEXT_FIELD_SIZE);
        jl_key.setDisplayedMnemonic('k');
        jl_key.setLabelFor(jtf_key);
        JButton jb_add = new JButton("Add");
        jb_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String key = jtf_key.getText();
                String value = jtf_value.getText();
                List<String> errors = null;
                if(Util.isStrEmpty(key)){
                    errors = new ArrayList<String>();
                    errors.add("Key is empty.");
                }
                if(Util.isStrEmpty(value)){
                    errors = errors==null?new ArrayList<String>():errors;
                    errors.add("Value is empty.");
                }
                if(errors != null){
                    StringBuffer sb = new StringBuffer();
                    sb.append("<html><ul>");
                    for(String error: errors){
                        sb.append("<li>");
                        sb.append(error);
                        sb.append("</li>");
                    }
                    sb.append("</ul></html>");
                    JOptionPane.showMessageDialog(frame, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                model.insertRow(key, value);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jtf_key.setText("");
                        jtf_value.setText("");
                        jtf_key.requestFocus();
                    }
                });
            }
        });
        jp_north.add(jl_key);
        jp_north.add(jtf_key);
        jp_north.add(jl_value);
        jp_north.add(jtf_value);
        jp_north.add(jb_add);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new GridLayout(1, 1));
        JScrollPane jsp = new JScrollPane(jt);
        jp_center.add(jsp);
        jp.add(jp_center, BorderLayout.CENTER);
        
    }
    
    public Dimension getTableDimension(){
        return tableDimension;
    }
    
}
