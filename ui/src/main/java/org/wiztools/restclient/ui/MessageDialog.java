package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author  schandran
 */
public class MessageDialog extends EscapableDialog {

    private final MessageDialog messageDialog;
    private RESTUserInterface ui;

    /** Creates new form ErrorDialog */
    @Inject
    public MessageDialog(RESTUserInterface ui) {
        super(ui.getFrame(), true);
        this.ui = ui;
        this.setTitle("Error!");
        initComponents();
        this.messageDialog = this;
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
        getRootPane().setDefaultButton(jb_ok);
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
        messageDialog.setVisible(false);
    }

    void showError(final String error){
        showMessage("Error", error);
    }

    void showMessage(final String title, final String message){
        messageDialog.setTitle(title);
        jta_error.setText(message);
        jta_error.setCaretPosition(0);
        messageDialog.setLocationRelativeTo(ui.getFrame());
        jb_ok.requestFocus();
        messageDialog.setVisible(true);
    }

    private javax.swing.JTextArea jta_error;
    private JButton jb_ok;
}
