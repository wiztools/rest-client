package org.wiztools.restclient.ui.resheader;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.HttpCookie;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subwiz
 */
public class ResHeaderPanelImpl extends JPanel implements ResHeaderPanel {
    
    @Inject
    private RESTView view;
    
    private JTable jt_res_headers = new JTable();
    private ResponseHeaderTableModel resHeaderTableModel = new ResponseHeaderTableModel();
    
    @PostConstruct
    protected void init() {
        setLayout(new GridLayout());
        
        // Header Tab: Other Headers
        JPanel jp_headers = new JPanel();
        jp_headers.setLayout(new GridLayout(1, 1));
        jt_res_headers.addMouseListener(new MouseAdapter() {
            private JPopupMenu popup = new JPopupMenu();
            private JMenuItem jmi_copy = new JMenuItem("Copy Selected Header(s)");
            private JMenuItem jmi_copy_all = new JMenuItem("Copy All Headers");
            private JMenuItem jmi_copy_cookies = new JMenuItem("Copy Cookies");
            {
                jmi_copy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int[] rows = jt_res_headers.getSelectedRows();
                        if(rows.length == 0) {
                            view.setStatusMessage("No header(s) selected");
                            return;
                        }
                        Arrays.sort(rows);
                        StringBuilder sb = new StringBuilder();
                        for(final int row: rows) {
                            final String key = (String) jt_res_headers.getValueAt(row, 0);
                            final String value = (String) jt_res_headers.getValueAt(row, 1);
                            sb.append(key).append(": ").append(value).append("\r\n");
                        }
                        UIUtil.clipboardCopy(sb.toString());
                        view.setStatusMessage("Copied selected headers to clipboard");
                    }
                });
                popup.add(jmi_copy);
            }
            {
                jmi_copy_all.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int totalRows = jt_res_headers.getRowCount();

                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<totalRows; i++) {
                            final String key = (String) jt_res_headers.getValueAt(i, 0);
                            final String value = (String) jt_res_headers.getValueAt(i, 1);

                            sb.append(key).append(": ").append(value).append("\r\n");
                        }
                        UIUtil.clipboardCopy(sb.toString());
                        view.setStatusMessage("Copied ALL headers to clipboard");
                    }
                });
                popup.add(jmi_copy_all);
            }
            {
                jmi_copy_cookies.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int totalRows = jt_res_headers.getRowCount();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<totalRows; i++) {
                            final String key = (String) jt_res_headers.getValueAt(i, 0);
                            final String value = (String) jt_res_headers.getValueAt(i, 1);
                            
                            final String headerLine = key + ": " + value;
                            
                            // Verify if Cookie header:
                            if(headerLine.toLowerCase().startsWith("set-cookie")) {
                                List<HttpCookie> cookies = HttpCookie.parse(headerLine);
                                for(HttpCookie cookie: cookies) {
                                    sb.append(cookie.getName())
                                            .append(": ")
                                            .append(cookie.getValue())
                                            .append("\r\n");
                                }
                            }
                        }
                        if(sb.length() > 0) {
                            UIUtil.clipboardCopy(sb.toString());
                            view.setStatusMessage("Cookie(s) copied to clipboard");
                        }
                        else {
                            view.setStatusMessage("No cookie header(s) found");
                        }
                    }
                });
                popup.add(jmi_copy_cookies);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if(jt_res_headers.getSelectedRowCount() == 0) {
                    jmi_copy.setEnabled(false);
                }
                else {
                    jmi_copy.setEnabled(true);
                }
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        jt_res_headers.setModel(resHeaderTableModel);
        JScrollPane jsp = new JScrollPane(jt_res_headers);
        Dimension d = jsp.getPreferredSize();
        d.height = d.height / 2;
        jsp.setPreferredSize(d);
        jp_headers.add(jsp);
        
        add(jp_headers);
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        final String[][] arr = ((ResponseHeaderTableModel)jt_res_headers.getModel()).getHeaders();
        if(arr == null) {
            return CollectionsUtil.EMPTY_MULTI_VALUE_MAP;
        }
        MultiValueMap<String, String> out = new MultiValueMapArrayList<String, String>();
        for(int i=0; i<arr.length; i++) {
            out.put(arr[i][0], arr[i][1]);
        }
        return out;
    }

    @Override
    public void setHeaders(MultiValueMap<String, String> headers) {
        resHeaderTableModel.setHeaders(headers);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_res_headers.getModel();
        model.setHeaders(null);
    }
    
}
