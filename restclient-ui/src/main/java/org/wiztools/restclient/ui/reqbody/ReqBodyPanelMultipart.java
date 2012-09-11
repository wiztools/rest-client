package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.wiztools.restclient.bean.*;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelMultipart extends JPanel implements ReqBodyPanel {
    
    private static final String PART_FILE = "File";
    
    private JButton jb_string = new JButton("String");
    private JButton jb_file = new JButton("File");
    
    private final MultipartTableModel model = new MultipartTableModel();
    private JTable jt = new JTable(model);
    
    private class MultipartTableModel extends AbstractTableModel {
        
        private final String[] columnNames = new String[]{"Type", "Name", "Part"};

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        /*@Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }*/
        
        public void addPart(ReqEntityPart part) {
            if(part instanceof ReqEntityStringPart) {
                ReqEntityStringPart p = (ReqEntityStringPart) part;
                ContentType ct = p.getContentType();
                String name = p.getName();
                String body = p.getPart();
            }
            else if(part instanceof ReqEntityFilePart) {
                ReqEntityFilePart p = (ReqEntityFilePart) part;
                String fileName = p.getName();
                File file = p.getPart();
            }
        }
    }
    
    @PostConstruct
    protected void init() {
        setLayout(new BorderLayout());
        
        { // North:
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp.add(new JLabel("Add Part: "));
            { // String button:
                jb_string.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO
                    }
                });
                jp.add(jb_string);
            }
            { // file button:
                jb_file.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO
                    }
                });
                jp.add(jb_file);
            }
            add(jp, BorderLayout.NORTH);
        }
        
        // Center:
        JScrollPane jsp = new JScrollPane(jt);
        add(jsp, BorderLayout.CENTER);
    }
    
    @Override
    public void enableBody() {
        jb_string.setEnabled(false);
        jb_file.setEnabled(false);
        jt.setEnabled(false);
    }
    
    @Override
    public void disableBody() {
        jb_string.setEnabled(true);
        jb_file.setEnabled(true);
        jt.setEnabled(true);
    }
    
    @Override
    public void clear() {
        // 
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityMultipart) {
            ReqEntityMultipart e = (ReqEntityMultipart) entity;
            List<ReqEntityPart> parts = e.getBody();
            for(ReqEntityPart part: parts) {
                model.addPart(part);
            }
        }
    }
    
    @Override
    public ReqEntity getEntity() {
        return null;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
}
