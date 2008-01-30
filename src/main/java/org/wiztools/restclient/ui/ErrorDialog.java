package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author  schandran
 */
public class ErrorDialog extends EscapableDialog {
    
    private ErrorDialog errorDialog;
    private java.awt.Frame parent;
    
    /** Creates new form ErrorDialog */
    public ErrorDialog(java.awt.Frame parent) {
        super(parent, true);
        this.parent = parent;
        this.setTitle("Error!");
        initComponents();
        this.errorDialog = this;
    }
    
    private void initComponents() {

        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jta_error = new JTextArea(10, 40);
        jta_error.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta_error);
        jp.add(jsp, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jb_okActionPerformed(event);
            }
        });
        jp_south.add(jb_ok);
        
        jp.add(jp_south, BorderLayout.SOUTH);

        this.setContentPane(jp);

        pack();
    }
    
    @Override
    public void doEscape(AWTEvent event){
        hideDialog();
    }

    private void jb_okActionPerformed(java.awt.event.ActionEvent evt) {
        hideDialog();
    }
    
    private void hideDialog(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                errorDialog.setVisible(false);
            }
        });
    }
    
    void showError(final String error){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                jta_error.setText(error);
                errorDialog.setLocationRelativeTo(parent);
                jb_ok.requestFocus();
                errorDialog.setVisible(true);
            }
        });
    }
    
    private javax.swing.JTextArea jta_error;
    private JButton jb_ok;
}
