/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.wiztools.restclient.FileType;

/**
 *
 * @author Subhash
 */
public class RunTestDialog extends EscapableDialog {
    
    private JButton jb_next = new JButton("Next");
    private JButton jb_cancel = new JButton("Cancel");
    
    private JRadioButton jrb_archive = new JRadioButton("From Request-Response Archive");
    private JRadioButton jrb_last = new JRadioButton("From last Request-Response");
    
    private JTextField jtf_archive = new JTextField();
    private JButton jb_archive_browse = new JButton("Browse");
    
    private RunTestDialog me;
    
    private JFileChooser jfc = UIUtil.getNewJFileChooser();
    
    public RunTestDialog(Frame f){
        super(f, true);
        this.setTitle("Run Test");
        me = this;
        init();
        this.pack();
    }
    
    private void init(){
        jfc.addChoosableFileFilter(new RCFileFilter(FileType.ARCHIVE_EXT));
        
        ButtonGroup group = new ButtonGroup();
        group.add(jrb_archive);
        group.add(jrb_last);
        jrb_archive.setSelected(true);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(jrb_archive.isSelected()){
                            jb_archive_browse.setEnabled(true);
                        }
                        else{
                            jb_archive_browse.setEnabled(false);
                        }
                    }
                });
                
            }
        };
        jrb_archive.addActionListener(al);
        jrb_last.addActionListener(al);
        
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp_north.add(new JLabel("Run Test"));
        c.add(jp_north, BorderLayout.NORTH);
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new GridLayout(3, 1));
        jp_center.add(jrb_archive);
        JPanel jp_center_file = new JPanel();
        jp_center_file.setLayout(new FlowLayout());
        jtf_archive.setColumns(24);
        jtf_archive.setEditable(false);
        jp_center_file.add(jtf_archive);
        jb_archive_browse.setMnemonic('b');
        jb_archive_browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //@TODO
            }
        });
        jp_center_file.add(jb_archive_browse);
        jp_center.add(jp_center_file);
        jp_center.add(jrb_last);
        c.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jb_next.setMnemonic('n');
        jb_next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jb_nextAction();
            }
        });
        jp_south.add(jb_next);
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        jp_south.add(jb_cancel);
        c.add(jp_south, BorderLayout.SOUTH);
    }
    
    private void jb_nextAction(){
        
    }

    @Override
    public void doEscape(AWTEvent event) {
        close();
    }
    
    private void close(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                me.setVisible(false);
            }
        });
    }

}
