package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
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
    
    private JButton jb_string = new JButton("String");
    private JButton jb_file = new JButton("File");
    
    private final MultipartTableModel model = new MultipartTableModel();
    private JTable jt = new JTable(model);
    
    private class MultipartTableModel extends AbstractTableModel {
        
        private final String[] columnNames = new String[]{"Type", "Name", "Part"};
        private final LinkedList<ReqEntityPart> list = new LinkedList<ReqEntityPart>();

        @Override
        public int getRowCount() {
            return list.size();
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
            ReqEntityPart part = list.get(rowIndex);
            if(columnIndex == 0) {
                return part.getContentType();
            }
            else if(columnIndex == 1) {
                return part.getName();
            }
            else {
                if(part instanceof ReqEntityStringPart) {
                    return ((ReqEntityStringPart)part).getPart();
                }
                else if(part instanceof ReqEntityFilePart) {
                    return ((ReqEntityFilePart)part).getPart();
                }
            }
            throw new IllegalArgumentException("Should never come here!");
        }

        /*@Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ReqEntityPart part = (ReqEntityPart) aValue;
        }*/
        
        public void addPartFirst(ReqEntityPart part) {
            list.addFirst(part);
            fireTableDataChanged();
        }
        
        public void addPartLast(ReqEntityPart part) {
            list.addLast(part);
            fireTableDataChanged();
        }
        
        public void removeRow(int row) {
            list.remove(row);
            fireTableDataChanged();
        }
        
        public void clear() {
            list.clear();
            fireTableDataChanged();
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
        model.clear();
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityMultipart) {
            ReqEntityMultipart e = (ReqEntityMultipart) entity;
            List<ReqEntityPart> parts = e.getBody();
            for(ReqEntityPart part: parts) {
                model.addPartLast(part);
            }
        }
    }
    
    @Override
    public ReqEntity getEntity() {
        ReqEntity entity = new ReqEntityMultipartBean(model.list);
        return entity;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
}
