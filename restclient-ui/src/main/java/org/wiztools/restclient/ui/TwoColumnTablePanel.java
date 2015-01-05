package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapLinkedHashSet;
import org.wiztools.commons.StringUtil;

/**
 *
 * @author schandran
 */
public final class TwoColumnTablePanel extends JPanel {

    private RESTUserInterface rest_ui;
    
    private TwoColumnTableModel model;
    private Dimension tableDimension;
    private KeyValMultiEntryDialog jd_multi;
    
    private JMenuItem jmi_rm_selected = new JMenuItem("Remove Selected");

    private void initMultiEntryDialog(){
        // Initialize the Multi-entry dialog:
        MultiEntryAdd callback = new MultiEntryAdd() {
            @Override
            public void add(Map<String, String> keyValuePair, List<String> invalidLines) {
                int successCount = 0;
                for(String key: keyValuePair.keySet()){
                    String value = keyValuePair.get(key);
                    model.insertRow(key, value);
                    successCount++;
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("Added ").append(successCount).append(" key/value pairs.\n\n");

                sb.append("\n**Lines Skipped Due To Pattern Mis-match**\n\n");
                if(invalidLines.isEmpty()){
                    sb.append("- None -\n");
                }
                else{
                    for(String line: invalidLines){
                        sb.append(line).append("\n");
                    }
                }

                rest_ui.getView().showMessage("Multi-insert Result", sb.toString());
            }
        };
        jd_multi = new KeyValMultiEntryDialog(rest_ui, callback);
    }
    
    public MultiValueMap<String, String> getData() {
        Object[][] d = model.getData();
        if(d.length == 0) {
            return CollectionsUtil.EMPTY_MULTI_VALUE_MAP;
        }
        
        MultiValueMap<String, String> out = new MultiValueMapLinkedHashSet<>();
        for (Object[] d1 : d) {
            String key = (String) d1[0];
            String value = (String) d1[1];
            out.put(key, value);
        }
        
        return out;
    }
    
    public void setData(MultiValueMap<String, String> data) {
        model.setData(data);
    }
    
    public TwoColumnTablePanel(final String[] title, final RESTUserInterface ui) {
        this(title, Collections.EMPTY_LIST, ui);
    }

    public TwoColumnTablePanel(final String[] title, List<String> keys, final RESTUserInterface ui) {

        this.rest_ui = ui;
        
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
        {
            jmi_rm_selected.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
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
            popupMenu.add(jmi_rm_selected);
        }
        
        {
            JMenuItem jmi_rm_all = new JMenuItem("Remove All");
            jmi_rm_all.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.setData(CollectionsUtil.EMPTY_MULTI_VALUE_MAP);
                }
            });
            popupMenu.add(jmi_rm_all);
        }
        
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
                if(jt.getSelectedRowCount() == 0) { // No table row selected
                    jmi_rm_selected.setEnabled(false);
                }
                else {
                    jmi_rm_selected.setEnabled(true);
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
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel jl_key = new JLabel("Key: ");
        JLabel jl_value = new JLabel("Value: ");
        final int TEXT_FIELD_SIZE = 12;
        final JTextField jtf_key = new JTextField(TEXT_FIELD_SIZE);
        if(!keys.isEmpty()) {AutoCompleteDecorator.decorate(jtf_key, keys, false);}
        final JTextField jtf_value = new JTextField(TEXT_FIELD_SIZE);
        jl_key.setDisplayedMnemonic('k');
        jl_key.setLabelFor(jtf_key);
        JButton jb_add = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "add.png"));
        jb_add.setToolTipText("Add");
        jb_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String key = jtf_key.getText();
                String value = jtf_value.getText();
                List<String> errors = null;
                if(StringUtil.isEmpty(key)){
                    errors = new ArrayList<String>();
                    errors.add("Key is empty.");
                }
                if(StringUtil.isEmpty(value)){
                    errors = errors==null?new ArrayList<String>():errors;
                    errors.add("Value is empty.");
                }
                Object[][] data = model.getData();
                
                if(errors != null){
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><ul>");
                    for(String error: errors){
                        sb.append("<li>");
                        sb.append(error);
                        sb.append("</li>");
                    }
                    sb.append("</ul></html>");
                    JOptionPane.showMessageDialog(ui.getFrame(), sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                model.insertRow(key, value);
                jtf_key.setText("");
                jtf_value.setText("");
                jtf_key.requestFocus();
            }
        });
        JButton jb_multi_insert = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "insert_parameters.png"));
        jb_multi_insert.setToolTipText("Multi-insert");
        jb_multi_insert.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(jd_multi == null){
                    initMultiEntryDialog();
                }
                jd_multi.setVisible(true);
            }
        });
        jp_north.add(jl_key);
        jp_north.add(jtf_key);
        jp_north.add(jl_value);
        jp_north.add(jtf_value);
        jp_north.add(jb_add);
        jp_north.add(jb_multi_insert);
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
