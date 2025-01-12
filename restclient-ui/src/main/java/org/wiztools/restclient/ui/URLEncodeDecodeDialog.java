package org.wiztools.restclient.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author subwiz
 */
public class URLEncodeDecodeDialog extends EscapableDialog {

    private static final int JTA_ROWS = 8;
    private static final int JTA_COLS = 35;

    private JTextArea jta_in = new JTextArea(JTA_ROWS, JTA_COLS);
    private JTextArea jta_out = new JTextArea(JTA_ROWS, JTA_COLS);

    private JButton jb_encode = new JButton("Encode");
    private JButton jb_decode = new JButton("Decode");
    private JButton jb_clear_result = new JButton("Clear Result");
    private JButton jb_copy_result = new JButton("Copy Result");

    public URLEncodeDecodeDialog(Frame f) {
        super(f, false);

        setTitle("URL Encode/Decode");

        jta_out.setEditable(false);

        // Default button:
        getRootPane().setDefaultButton(jb_encode);

        // Button Actions:
        jb_encode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encode();
            }
        });
        jb_decode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decode();
            }
        });
        jb_clear_result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jta_out.setText("");
            }
        });
        jb_copy_result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIUtil.clipboardCopy(jta_out.getText());
            }
        });

        // Layout:




        JPanel jp_south = new JPanel(new BorderLayout());
        jp_south.add(getButtonPanel(), BorderLayout.NORTH);
        jp_south.add(new JScrollPane(jta_out), BorderLayout.CENTER);

        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.setTopComponent(new JScrollPane(jta_in));
        jsp.setBottomComponent(jp_south);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(jsp, BorderLayout.CENTER);

        pack();
    }

    private JPanel getButtonPanel() {
        JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jp.add(jb_encode);
        jp.add(jb_decode);
        jp.add(jb_clear_result);
        jp.add(jb_copy_result);
        return jp;
    }

    private void encode() {
        jta_out.setText(EncodingUtil.encodeURIComponent(jta_in.getText()));
    }

    private void decode() {
        jta_out.setText(EncodingUtil.decodeURIComponent(jta_in.getText()));
    }

    @Override
    public void doEscape(AWTEvent event) {
        setVisible(false);
    }
}
