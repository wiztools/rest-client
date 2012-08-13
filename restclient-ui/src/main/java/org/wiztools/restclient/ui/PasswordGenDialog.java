package org.wiztools.restclient.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.*;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.Base64Exception;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author Subhash
 */
class PasswordGenDialog extends EscapableDialog {
    
    private static final String helpText;
    
    static{
        String tmp = null;
        InputStream is = RESTMain.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/PasswordHelp.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try{
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            tmp = sb.toString();
        }
        catch(IOException ex){
            tmp = "Help loading failed.";
        }
        finally{
            if(br != null){
                try{
                    br.close();
                }
                catch(IOException ex){
                    // Do nothing!
                }
            }
        }
        helpText = tmp;
    }
    
    private JRadioButton jrb_encode = new JRadioButton("Encode");
    private JRadioButton jrb_decode = new JRadioButton("Decode");
    
    private JTextField jtf_in = new JTextField(25);
    private JTextField jtf_out = new JTextField(25);
    
    private final PasswordGenDialog me;
    
    public PasswordGenDialog(){
        super(ServiceLocator.getInstance(RESTUserInterface.class).getFrame(), true);
        me = this;
        this.setTitle("Password Encoder/Decoder");
        init();
    }
    
    private void init(){
        ButtonGroup bg = new ButtonGroup();
        
        jrb_encode.setMnemonic('e');
        jrb_encode.setSelected(true);
        jrb_decode.setMnemonic('d');
        
        bg.add(jrb_encode);
        bg.add(jrb_decode);
        
        jtf_out.setEditable(false);
        
        // Button Action
        ActionListener closeAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                me.setVisible(false);
            }
        };
        
        // Layout
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createEmptyBorder(
                RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        jp.setLayout(new BorderLayout());
        
        // North
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp_north.add(jrb_encode);
        jp_north.add(jrb_decode);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout());
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(2, 1, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        JLabel jl_in = new JLabel("Input: ");
        jl_in.setDisplayedMnemonic('i');
        jl_in.setLabelFor(jtf_in);
        JLabel jl_out = new JLabel("Output: ");
        jp_center_west.add(jl_in);
        jp_center_west.add(jl_out);
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(2, 1, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        jp_center_center.add(jtf_in);
        jp_center_center.add(jtf_out);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);
        
        // South
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_generate = new JButton("Generate");
        jb_generate.setMnemonic('g');
        getRootPane().setDefaultButton(jb_generate);
        jb_generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generate();
            }
        });
        jp_south.add(jb_generate);
        JButton jb_gen_close = new JButton("Close");
        jb_gen_close.addActionListener(closeAction);
        jp_south.add(jb_gen_close);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        // Help Window
        JPanel jp_help = new JPanel();
        jp_help.setBorder(BorderFactory.createEmptyBorder(
                RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        jp_help.setLayout(new BorderLayout());
        
        JTextPane jtp_help_center = new JTextPane();
        jtp_help_center.setEditable(false);
        Dimension d = jtp_help_center.getPreferredSize();
        jtp_help_center.setText(helpText);
        jtp_help_center.setPreferredSize(d);
        jtp_help_center.setCaretPosition(0);
        JScrollPane jsp_center = new JScrollPane(jtp_help_center);
        jp_help.add(jsp_center, BorderLayout.CENTER);
        
        JPanel jp_help_south = new JPanel();
        jp_help_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_close = new JButton("Close");
        jb_close.addActionListener(closeAction);
        jp_help_south.add(jb_close);
        jp_help.add(jp_help_south, BorderLayout.SOUTH);
        
        JTabbedPane jtp = new JTabbedPane();
        jtp.setBorder(BorderFactory.createEmptyBorder(
                RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        jtp.addTab("Encoder/Decoder", jp);
        jtp.addTab("Help", jp_help);
        
        this.setContentPane(jtp);
        this.pack();
    }
    
    private void generate(){
        // Check for null String in jtf_in
        final String inStr = jtf_in.getText();
        if(StringUtil.isEmpty(inStr)){
            JOptionPane.showMessageDialog(me,
                "No input entered.",
                "Error in input.",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Get if it is encode or decode
        final String result;
        if(jrb_encode.isSelected()){
            result = Util.base64encode(inStr);
        }
        else{
            try {
                result = Util.base64decode(inStr);
            }
            catch(Base64Exception ex) {
                JOptionPane.showMessageDialog(me,
                    ex.getMessage(),
                    "Error in input.",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        jtf_out.setText(result);
    }

    @Override
    public void doEscape(AWTEvent event) {
        me.setVisible(false);
    }
}
