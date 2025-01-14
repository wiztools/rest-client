package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelMultipart extends JPanel implements ReqBodyPanel {
    
    @Inject
    private RESTView view;
    
    @Inject
    private AddMultipartFileDialog jd_addFileDialog;
    
    @Inject
    private AddMultipartStringDialog jd_addStringDialog;
    
    @Inject
    private MultipartOptionsDialog jd_options;
    
    private final JButton jb_string = new JButton("String");
    private final JButton jb_file = new JButton("File");
    private final JButton jb_config = new JButton(UIUtil.getIconFromClasspath("org/wiztools/restclient/cog.png"));
    
    private final MultipartTableModel model = new MultipartTableModel();
    private final JTable jt = new JTable(model);
    
    private class MultipartTableModel extends AbstractTableModel {
        
        private final String[] columnNames = new String[]{"Type", "Content-type", "Name", "Part"};
        private final LinkedList<ReqEntityPart> list = new LinkedList<>();

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
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
                if(part instanceof ReqEntityStringPart) {
                    return "String";
                }
                else if(part instanceof ReqEntityFilePart) {
                    return "File";
                }
            }
            else if(columnIndex == 1) {
                return part.getContentType();
            }
            else if(columnIndex == 2) {
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
        
        public void addPartFirst(ReqEntityPart part) {
            list.addFirst(part);
            fireTableDataChanged();
        }
        
        public void addPartLast(ReqEntityPart part) {
            list.addLast(part);
            fireTableDataChanged();
        }
        
        public ReqEntityPart getEntityInRow(int row) {
            return list.get(row);
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
        // Listeners:
        final AddMultipartPartListener listener = new AddMultipartPartListener() {
            @Override
            public void addPart(ReqEntityPart part) {
                model.addPartFirst(part);
            }
        };
        jd_addStringDialog.addMultipartPartListener(listener);
        jd_addFileDialog.addMultipartPartListener(listener);
        
        // Table popup:
        JPopupMenu menu = new JPopupMenu();
        JMenuItem jmi_rm = new JMenuItem("Delete selected");
        jmi_rm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int[] rows = jt.getSelectedRows();
                Arrays.sort(rows);
                if(rows != null && rows.length > 0) {
                    int i = 0;
                    for(int row: rows) {
                        row = row - i; // the number of rows previously deleted should be accounted for!
                        model.removeRow(row);
                        i++;
                    }
                    view.setStatusMessage(MessageFormat.format("Deleted {0} row(s)", i));
                }
                else {
                    view.setStatusMessage("No row(s) selected!");
                }
            }
        });
        menu.add(jmi_rm);
        JMenuItem jmi_view = new JMenuItem("Quick view");
        jmi_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quickView();
            }
        });
        menu.add(jmi_view);
        jt.setComponentPopupMenu(menu);
        
        // Layouts:
        setLayout(new BorderLayout());
        
        { // North:
            JPanel jp_border = new JPanel(new BorderLayout(0, 0));
            
            JPanel jp_center = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jp_center.add(new JLabel("Add Part: "));
            { // String button:
                jb_string.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jd_addStringDialog.setVisible(true);
                    }
                });
                jp_center.add(jb_string);
            }
            { // file button:
                jb_file.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jd_addFileDialog.setVisible(true);
                    }
                });
                jp_center.add(jb_file);
            }
            jp_border.add(jp_center, BorderLayout.CENTER);
            
            JPanel jp_east = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            { // config button:
                jb_config.setToolTipText("Set multipart mode");
                jb_config.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jd_options.setVisible(true);
                    }
                });
                jp_east.add(jb_config);
            }
            jp_border.add(jp_east, BorderLayout.EAST);
            
            add(jp_border, BorderLayout.NORTH);
        }
        
        // Center:
        JScrollPane jsp = new JScrollPane(jt);
        add(jsp, BorderLayout.CENTER);
    }
    
    private void quickView() {
        final int row = jt.getSelectedRow();
        if(row != -1) {
            ReqEntityPart entity = model.getEntityInRow(row);
            view.showMessage("Quick View", entity.toString());
        }
    }
    
    @Override
    public void enableBody() {
        jb_string.setEnabled(true);
        jb_file.setEnabled(true);
        jb_config.setEnabled(true);
        jt.setEnabled(true);
        jd_options.setEnabled(true);
    }
    
    @Override
    public void disableBody() {
        jb_string.setEnabled(false);
        jb_file.setEnabled(false);
        jb_config.setEnabled(false);
        jt.setEnabled(false);
        jd_options.setEnabled(false);
    }
    
    @Override
    public void clear() {
        jd_options.clear();
        model.clear();
    }

    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityMultipart) {
            ReqEntityMultipart e = (ReqEntityMultipart) entity;
            
            MultipartSubtype type = e.getSubtype();
            jd_options.setSelectedSubtype(type);
            
            MultipartMode mode = e.getMode();
            jd_options.setSelectedMode(mode);
            
            List<ReqEntityPart> parts = e.getBody();
            for(ReqEntityPart part: parts) {
                model.addPartLast(part);
            }
        }
    }
    
    @Override
    public ReqEntity getEntity() {
        MultipartSubtype type = jd_options.getSelectedSubtype();
        MultipartMode mode = jd_options.getSelectedMode();
        
        ReqEntity entity = new ReqEntityMultipartBean(
                (LinkedList<ReqEntityPart>)model.list.clone(), mode, type);
        return entity;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
}
