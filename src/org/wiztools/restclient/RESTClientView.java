/*
 * RESTClientView.java
 */

package org.wiztools.restclient;

import java.awt.Component;
import java.awt.event.MouseEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * The application's main frame.
 */
public class RESTClientView extends FrameView {
    
    private RequestHeaderTableModel reqHeaderTableModel;
    private JMenuItem jmi_req_delete;
    private JPopupMenu popupMenu = new JPopupMenu();
    private RESTClientView view;
    private Component glassPanel;
    private final Component glassPanelBlank = new JPanel();

    public RESTClientView(SingleFrameApplication app) {
        super(app);

        reqHeaderTableModel = new RequestHeaderTableModel();
        
        jmi_req_delete = new JMenuItem("Delete");
        jmi_req_delete.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jmi_req_deleteActionPerformed(e);
            }
        });
        popupMenu.add(jmi_req_delete);
        
        initComponents();
        
        this.view = this;
        
        jt_req_headers.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            private void showPopup(MouseEvent e) {
                if(jt_req_headers.getSelectedRowCount() == 0){
                    // No table row selected
                    return;
                }
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox(ActionEvent e) {
        if (aboutBox == null) {
            JFrame mainFrame = RESTClientApp.getApplication().getMainFrame();
            aboutBox = new RESTClientAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        RESTClientApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jb_request = new javax.swing.JButton();
        jb_clear = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jrb_get = new javax.swing.JRadioButton();
        jrb_head = new javax.swing.JRadioButton();
        jrb_post = new javax.swing.JRadioButton();
        jrb_put = new javax.swing.JRadioButton();
        jrb_delete = new javax.swing.JRadioButton();
        jrb_options = new javax.swing.JRadioButton();
        jrb_trace = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jrb_auth_basic = new javax.swing.JRadioButton();
        jrb_auth_digest = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jcb_auth_enable = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jl_auth_host = new javax.swing.JLabel();
        jl_auth_realm = new javax.swing.JLabel();
        jl_auth_uid = new javax.swing.JLabel();
        jl_auth_pwd = new javax.swing.JLabel();
        jtf_auth_uid = new javax.swing.JTextField();
        jpf_auth_pwd = new javax.swing.JPasswordField();
        jtf_auth_realm = new javax.swing.JTextField();
        jtf_auth_host = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jtf_req_key = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtf_req_value = new javax.swing.JTextField();
        jb_req_add = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jt_req_headers = new javax.swing.JTable();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jt_headers = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jtf_res_status = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtp_response = new javax.swing.JTextPane();
        jcb_url = new javax.swing.JComboBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        bg_httpMethods = new javax.swing.ButtonGroup();
        bg_auth = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.wiztools.restclient.RESTClientApp.class).getContext().getResourceMap(RESTClientView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jb_request.setMnemonic('q');
        jb_request.setText(resourceMap.getString("jb_request.text")); // NOI18N
        jb_request.setName("jb_request"); // NOI18N
        jb_request.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_requestActionPerformed(evt);
            }
        });

        jb_clear.setMnemonic('c');
        jb_clear.setText(resourceMap.getString("jb_clear.text")); // NOI18N
        jb_clear.setName("jb_clear"); // NOI18N
        jb_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_clearActionPerformed(evt);
            }
        });

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel8.border.title"))); // NOI18N
        jPanel8.setName("jPanel8"); // NOI18N

        bg_httpMethods.add(jrb_get);
        jrb_get.setSelected(true);
        jrb_get.setText(resourceMap.getString("jrb_get.text")); // NOI18N
        jrb_get.setName("jrb_get"); // NOI18N

        bg_httpMethods.add(jrb_head);
        jrb_head.setText(resourceMap.getString("jrb_head.text")); // NOI18N
        jrb_head.setName("jrb_head"); // NOI18N

        bg_httpMethods.add(jrb_post);
        jrb_post.setText(resourceMap.getString("jrb_post.text")); // NOI18N
        jrb_post.setName("jrb_post"); // NOI18N

        bg_httpMethods.add(jrb_put);
        jrb_put.setText(resourceMap.getString("jrb_put.text")); // NOI18N
        jrb_put.setName("jrb_put"); // NOI18N

        bg_httpMethods.add(jrb_delete);
        jrb_delete.setText(resourceMap.getString("jrb_delete.text")); // NOI18N
        jrb_delete.setName("jrb_delete"); // NOI18N

        bg_httpMethods.add(jrb_options);
        jrb_options.setText(resourceMap.getString("jrb_options.text")); // NOI18N
        jrb_options.setName("jrb_options"); // NOI18N

        bg_httpMethods.add(jrb_trace);
        jrb_trace.setText(resourceMap.getString("jrb_trace.text")); // NOI18N
        jrb_trace.setName("jrb_trace"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jrb_get)
                    .add(jrb_head)
                    .add(jrb_delete)
                    .add(jrb_put)
                    .add(jrb_post))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 43, Short.MAX_VALUE)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jrb_options)
                    .add(jrb_trace))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrb_get)
                    .add(jrb_options))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrb_head)
                    .add(jrb_trace))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrb_post)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrb_put)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrb_delete)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        bg_auth.add(jrb_auth_basic);
        jrb_auth_basic.setSelected(true);
        jrb_auth_basic.setText(resourceMap.getString("jrb_auth_basic.text")); // NOI18N
        jrb_auth_basic.setEnabled(false);
        jrb_auth_basic.setName("jrb_auth_basic"); // NOI18N

        bg_auth.add(jrb_auth_digest);
        jrb_auth_digest.setText(resourceMap.getString("jrb_auth_digest.text")); // NOI18N
        jrb_auth_digest.setEnabled(false);
        jrb_auth_digest.setName("jrb_auth_digest"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jrb_auth_basic)
                    .add(jrb_auth_digest))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jrb_auth_basic)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jrb_auth_digest)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("jPanel6.border.lineColor"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jcb_auth_enable.setText(resourceMap.getString("jcb_auth_enable.text")); // NOI18N
        jcb_auth_enable.setName("jcb_auth_enable"); // NOI18N
        jcb_auth_enable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_auth_enableActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jcb_auth_enable)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jcb_auth_enable)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel7.border.title"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N

        jl_auth_host.setText(resourceMap.getString("jl_auth_host.text")); // NOI18N
        jl_auth_host.setEnabled(false);
        jl_auth_host.setName("jl_auth_host"); // NOI18N

        jl_auth_realm.setText(resourceMap.getString("jl_auth_realm.text")); // NOI18N
        jl_auth_realm.setEnabled(false);
        jl_auth_realm.setName("jl_auth_realm"); // NOI18N

        jl_auth_uid.setText(resourceMap.getString("jl_auth_uid.text")); // NOI18N
        jl_auth_uid.setEnabled(false);
        jl_auth_uid.setName("jl_auth_uid"); // NOI18N

        jl_auth_pwd.setText(resourceMap.getString("jl_auth_pwd.text")); // NOI18N
        jl_auth_pwd.setEnabled(false);
        jl_auth_pwd.setName("jl_auth_pwd"); // NOI18N

        jtf_auth_uid.setText(resourceMap.getString("jtf_auth_uid.text")); // NOI18N
        jtf_auth_uid.setEnabled(false);
        jtf_auth_uid.setName("jtf_auth_uid"); // NOI18N

        jpf_auth_pwd.setText(resourceMap.getString("jpf_auth_pwd.text")); // NOI18N
        jpf_auth_pwd.setEnabled(false);
        jpf_auth_pwd.setName("jpf_auth_pwd"); // NOI18N

        jtf_auth_realm.setText(resourceMap.getString("jtf_auth_realm.text")); // NOI18N
        jtf_auth_realm.setEnabled(false);
        jtf_auth_realm.setName("jtf_auth_realm"); // NOI18N

        jtf_auth_host.setText(resourceMap.getString("jtf_auth_host.text")); // NOI18N
        jtf_auth_host.setEnabled(false);
        jtf_auth_host.setName("jtf_auth_host"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jl_auth_pwd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jpf_auth_pwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jl_auth_uid)
                            .add(jl_auth_realm)
                            .add(jl_auth_host))
                        .add(6, 6, 6)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jtf_auth_host, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                            .add(jtf_auth_uid, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                            .add(jtf_auth_realm, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jl_auth_host)
                    .add(jtf_auth_host, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jl_auth_realm)
                    .add(jtf_auth_realm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jl_auth_uid)
                    .add(jtf_auth_uid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jl_auth_pwd)
                    .add(jpf_auth_pwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(98, 98, 98))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jtf_req_key.setText(resourceMap.getString("jtf_req_key.text")); // NOI18N
        jtf_req_key.setName("jtf_req_key"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jtf_req_value.setText(resourceMap.getString("jtf_req_value.text")); // NOI18N
        jtf_req_value.setName("jtf_req_value"); // NOI18N

        jb_req_add.setMnemonic('a');
        jb_req_add.setText(resourceMap.getString("jb_req_add.text")); // NOI18N
        jb_req_add.setName("jb_req_add"); // NOI18N
        jb_req_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_req_addActionPerformed(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jt_req_headers.setModel(reqHeaderTableModel);
        jt_req_headers.setName("jt_req_headers"); // NOI18N
        jScrollPane3.setViewportView(jt_req_headers);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtf_req_key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtf_req_value, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                        .add(jb_req_add)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jb_req_add)
                    .add(jLabel3)
                    .add(jtf_req_key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jtf_req_value, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel10.TabConstraints.tabTitle"), jPanel10); // NOI18N

        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jt_headers.setModel(new org.wiztools.restclient.ResponseHeaderTableModel());
        jt_headers.setName("jt_headers"); // NOI18N
        jScrollPane2.setViewportView(jt_headers);

        jPanel9.setName("jPanel9"); // NOI18N

        jtf_res_status.setEditable(false);
        jtf_res_status.setText(resourceMap.getString("jtf_res_status.text")); // NOI18N
        jtf_res_status.setName("jtf_res_status"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jtf_res_status, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jtf_res_status, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jtp_response.setEditable(false);
        jtp_response.setName("jtp_response"); // NOI18N
        jScrollPane1.setViewportView(jtp_response);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jcb_url.setEditable(true);
        jcb_url.setModel(new javax.swing.DefaultComboBoxModel());
        jcb_url.setName("jcb_url"); // NOI18N
        jcb_url.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_urlActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcb_url, 0, 389, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .add(jb_request, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 268, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jb_clear, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
                    .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jcb_url, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jb_request)
                    .add(jb_clear))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 260, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(285, 285, 285))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.wiztools.restclient.RESTClientApp.class).getContext().getActionMap(RESTClientView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 268, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jmi_req_deleteActionPerformed(java.awt.event.ActionEvent evt){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                int selectionCount = jt_req_headers.getSelectedRowCount();
                if(selectionCount > 0){
                    int[] rows = jt_req_headers.getSelectedRows();
                    Arrays.sort(rows);
                    for(int i=rows.length-1; i>=0; i--){
                        reqHeaderTableModel.deleteRow(rows[i]);
                    }
                }
            }
        });
    }
        
    private void jb_requestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_requestActionPerformed
        
        List<String> errors = validate();
        if(errors.size()!=0){
            StringBuffer sb = new StringBuffer();
            sb.append("<html><ul>");
            for(String error: errors){
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul></html>");
            JOptionPane.showMessageDialog(RESTClientApp.getApplication().getMainFrame(),
                sb.toString(),
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        RequestBean request = new RequestBean();
        boolean authEnabled = jcb_auth_enable.isSelected();
        request.setIsAuthEnabled(authEnabled);
        
        if(authEnabled){
            if(jrb_auth_basic.isSelected()){
                request.setAuthMethod("BASIC");
            }
            else if(jrb_auth_digest.isSelected()){
                request.setAuthMethod("DIGEST");
            }
            
            // Pass the credentials
            String uid = jtf_auth_uid.getText();
            char[] pwd = jpf_auth_pwd.getPassword();
            
            request.setAuthUsername(uid);
            request.setAuthPassword(pwd);
        }
        
        String url = (String)jcb_url.getSelectedItem();
        request.setUrl(url);
        if(jrb_get.isSelected()){
            request.setMethod("GET");
        }
        else if(jrb_head.isSelected()){
            request.setMethod("HEAD");
        }
        else if(jrb_post.isSelected()){
            request.setMethod("POST");
        }
        else if(jrb_put.isSelected()){
            request.setMethod("PUT");
        }
        else if(jrb_delete.isSelected()){
            request.setMethod("DELETE");
        }
        else if(jrb_options.isSelected()){
            request.setMethod("OPTIONS");
        }
        else if(jrb_trace.isSelected()){
            request.setMethod("TRACE");
        }
        
        // Get request headers
        Object[][] data = reqHeaderTableModel.getData();
        if(data.length > 0){
            for(int i=0; i<data.length; i++){
                String key = (String)data[i][0];
                String value = (String)data[i][1];
                request.addHeader(key, value);
            }
        }

        new HTTPRequestThread(request, view).start();
    }//GEN-LAST:event_jb_requestActionPerformed

    // This is accessed by the Thread. Don't make it private.
    void ui_update_response(final ResponseBean response){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jtf_res_status.setText(response.getStatusLine());
                String responseBody = response.getResponseBody();
                if(responseBody != null){
                    jtp_response.setText(responseBody);
                }
                else{
                    jtp_response.setText("");
                }
                ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_headers.getModel();
                model.setHeader(response.getHeaders());
            }
        });
    }
    
    // This is accessed by the Thread. Don't make it private.
    void freeze(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                JFrame jf = view.getFrame();
                glassPanel = jf.getGlassPane();
                glassPanelBlank.setVisible(true);
                jf.setGlassPane(glassPanelBlank);
                progressBar.setIndeterminate(true);
                jb_request.setEnabled(false);
            }
        });
    }
    
    // This is accessed by the Thread. Don't make it private.
    void unfreeze(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                view.getFrame().setGlassPane(glassPanel);
                progressBar.setIndeterminate(false);
                jb_request.setEnabled(true);
            }
        });
    }
    
    private void jb_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_clearActionPerformed
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jtf_res_status.setText("");
                jtp_response.setText("");
                ResponseHeaderTableModel model = (ResponseHeaderTableModel)jt_headers.getModel();
                model.setHeader(null);
            }
        });
    }//GEN-LAST:event_jb_clearActionPerformed

    private void authToggle(final boolean boo){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jrb_auth_basic.setEnabled(boo);
                jrb_auth_digest.setEnabled(boo);
                jtf_auth_host.setEnabled(boo);
                jtf_auth_realm.setEnabled(boo);
                jtf_auth_uid.setEnabled(boo);
                jpf_auth_pwd.setEnabled(boo);

                // Disable/enable labels:
                jl_auth_host.setEnabled(boo);
                jl_auth_realm.setEnabled(boo);
                jl_auth_uid.setEnabled(boo);
                jl_auth_pwd.setEnabled(boo);
            }
        });
    }
    
    private void jcb_auth_enableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcb_auth_enableActionPerformed
        if(jcb_auth_enable.isSelected()){
            authToggle(true);
        }
        else{
            authToggle(false);
        }
}//GEN-LAST:event_jcb_auth_enableActionPerformed

    private void jcb_urlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcb_urlActionPerformed
        if("comboBoxChanged".equals(evt.getActionCommand())){
            return;
        }
        final Object item = jcb_url.getSelectedItem();
        final int count = jcb_url.getItemCount();
        final LinkedList l = new LinkedList();
        for(int i=0; i<count; i++){
            l.add(jcb_url.getItemAt(i));
        }
        if(l.contains(item)){ // Item already present
            // Remove and add to bring it to the top
            // l.remove(item);
            // l.addFirst(item);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    // System.out.println("Removing and inserting at top");
                    jcb_url.removeItem(item);
                    jcb_url.insertItemAt(item, 0);
                }
            });
        }
        else{ // Add new item
            // The total number of items should not exceed 20
            if(count > 19){
                // Remove last item to give place
                // to new one
                //l.removeLast();
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        jcb_url.removeItemAt(count - 1);
                    }
                });
            }
            //l.addFirst(item);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    // System.out.println("Inserting at top");
                    jcb_url.insertItemAt(item, 0);
                }
            });
        }
    }//GEN-LAST:event_jcb_urlActionPerformed

    private void jb_req_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_req_addActionPerformed
        List<String> errors = validateReqHeaders();
        if(errors != null){
            StringBuffer sb = new StringBuffer();
            sb.append("<html><ul>");
            for(String error: errors){
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul></html>");
            JOptionPane.showMessageDialog(RESTClientApp.getApplication().getMainFrame(),
                sb.toString(),
                "Validation error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                String key = jtf_req_key.getText();
                String value = jtf_req_value.getText();
                reqHeaderTableModel.insertRow(key, value);
                jtf_req_key.setText("");
                jtf_req_value.setText("");
                jtf_req_key.requestFocus();
            }
        });
    }//GEN-LAST:event_jb_req_addActionPerformed

    private List<String> validate(){
        List<String> errors = new ArrayList<String>();
        Object o = null;
        String str = null;
        str = (String)jcb_url.getSelectedItem();
        if(isStrEmpty(str)){
            errors.add("URL field is empty.");
        }
        else{
            try{
                new URL(str);
            }
            catch(MalformedURLException ex){
                errors.add("URL is malformed.");
            }
        }
        if(jcb_auth_enable.isSelected()){
            if(isStrEmpty(jtf_auth_host.getText())){
                errors.add("Host is empty.");
            }
            if(isStrEmpty(jtf_auth_uid.getText())){
                errors.add("Username is empty.");
            }
            if(isStrEmpty(new String(jpf_auth_pwd.getPassword()))){
                errors.add("Password is empty.");
            }
        }
        return errors;
    }
    
    private List<String> validateReqHeaders(){
        List<String> errors = null;
        String key = jtf_req_key.getText();
        String value = jtf_req_value.getText();
        if(isStrEmpty(key)){
            errors = errors==null?new ArrayList<String>():errors;
            errors.add("Header Key is empty.");
        }
        if(isStrEmpty(value)){
            errors = errors==null?new ArrayList<String>():errors;
            errors.add("Header Value is empty.");
        }
        return errors;
    }
    
    private boolean isStrEmpty(final String str){
        if(str == null || "".equals(str.trim())){
            return true;
        }
        return false;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_auth;
    private javax.swing.ButtonGroup bg_httpMethods;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton jb_clear;
    private javax.swing.JButton jb_req_add;
    private javax.swing.JButton jb_request;
    private javax.swing.JCheckBox jcb_auth_enable;
    private javax.swing.JComboBox jcb_url;
    private javax.swing.JLabel jl_auth_host;
    private javax.swing.JLabel jl_auth_pwd;
    private javax.swing.JLabel jl_auth_realm;
    private javax.swing.JLabel jl_auth_uid;
    private javax.swing.JPasswordField jpf_auth_pwd;
    private javax.swing.JRadioButton jrb_auth_basic;
    private javax.swing.JRadioButton jrb_auth_digest;
    private javax.swing.JRadioButton jrb_delete;
    private javax.swing.JRadioButton jrb_get;
    private javax.swing.JRadioButton jrb_head;
    private javax.swing.JRadioButton jrb_options;
    private javax.swing.JRadioButton jrb_post;
    private javax.swing.JRadioButton jrb_put;
    private javax.swing.JRadioButton jrb_trace;
    private javax.swing.JTable jt_headers;
    private javax.swing.JTable jt_req_headers;
    private javax.swing.JTextField jtf_auth_host;
    private javax.swing.JTextField jtf_auth_realm;
    private javax.swing.JTextField jtf_auth_uid;
    private javax.swing.JTextField jtf_req_key;
    private javax.swing.JTextField jtf_req_value;
    private javax.swing.JTextField jtf_res_status;
    private javax.swing.JTextPane jtp_response;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
