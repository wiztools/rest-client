package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import org.wiztools.restclient.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 *
 * @author schandran
 */
class AboutDialog extends EscapableDialog {

    private Frame frame;
    private AboutDialog me;
    
    public AboutDialog(Frame f){
        super(f, true);
        frame = f;
        me = this;
        init();
    }
    
    private void init(){
        // Title
        setTitle("About");
        
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH,
                RESTView.BORDER_WIDTH));
        jp.setLayout(new BorderLayout());
        
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_title = new JLabel(
                "<html><h2>" +
                RCConstants.TITLE + RCConstants.VERSION +
                "</h2></html>");
        jp_north.add(jl_title);
        jp.add(jp_north, BorderLayout.NORTH);
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new GridLayout(1, 1));
        JTextPane jtp = new JTextPane();
        jtp.setEditable(false);
        jtp.setContentType("text/html");
        jtp.setText(MessageI18N.getMessage("menu.help.about"));
        jp_center.add(new JScrollPane(jtp));
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe();
            }
        });
        jp_south.add(jb_ok);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        setContentPane(jp);
        
        pack();
    }
    
    @Override
    public void doEscape(AWTEvent event) {
        hideMe();
    }
    
    public void hideMe(){
        me.setVisible(false);
    }

}
