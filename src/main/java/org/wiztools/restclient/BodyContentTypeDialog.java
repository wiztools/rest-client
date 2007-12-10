/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author schandran
 */
public class BodyContentTypeDialog extends JDialog {
    
    private static final String[] contentTypeArr;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    
    private static final String[] charSetArr;
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    public static final String PARAM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String PARAM_CHARSET = "UTF-8";

    // Content-type
    static{
        InputStream is = BodyContentTypeDialog.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/mime.types");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        String[] arr = null;
        try{
            List<String> ll = new ArrayList<String>();
            while((line = br.readLine())!=null){
                ll.add(line);
            }
            arr = new String[ll.size()];
            arr = ll.toArray(arr);
        }
        catch(IOException ex){
            arr = 
                new String[]{"text/plain",
                "application/xml",
                "application/json",
                "application/x-www-form-urlencoded"};
        }
        try{
            br.close();
        }
        catch(IOException ex){
            // do nothing!
            assert true: "Jar file does not have mime.types!";
        }
        contentTypeArr = arr;
    }
    
    // Charset
    static{
        Map<String, Charset> charsets = Charset.availableCharsets();
        int size = charsets.size();
        charSetArr = new String[size];
        int i = 0;
        for(String key: charsets.keySet()){
            charSetArr[i] = key;
            i++;
        }
    }
    
    
    private JComboBox jcb_content_type = new JComboBox(contentTypeArr);
    private JComboBox jcb_char_set = new JComboBox(charSetArr);
    
    private final BodyContentTypeDialog me;
    
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String charSet = DEFAULT_CHARSET;
    
    BodyContentTypeDialog(Frame f){
        // true means Modal:
        super(f, true);
        me = this;
        setTitle("Body Content-type");
        init();
        jcb_content_type.setSelectedItem(DEFAULT_CONTENT_TYPE);
        jcb_char_set.setSelectedItem(DEFAULT_CHARSET);
    }
    
    private void init(){
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout());
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(2, 1, 5, 5));
        JLabel jl_content_type = new JLabel("Content-type: ");
        jl_content_type.setLabelFor(jcb_content_type);
        JLabel jl_char_set = new JLabel("Charset: ");
        jl_char_set.setLabelFor(jcb_char_set);
        jp_center_west.add(jl_content_type);
        jp_center_west.add(jl_char_set);
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(2, 1, 5, 5));
        jp_center_center.add(jcb_content_type);
        jp_center_center.add(jcb_char_set);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        jb_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe(true);
            }
        });
        JButton jb_cancel = new JButton("Cancel");
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideMe(false);
            }
        });
        jp_south.add(jb_ok);
        jp_south.add(jb_cancel);
        jp.add(jp_south, BorderLayout.SOUTH);
        
        JPanel jp_encp = new JPanel();
        jp_encp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jp_encp.add(jp);
        
        this.setContentPane(jp_encp);
        this.pack();
    }
    
    void showMe(){
        this.setLocationRelativeTo(this.getParent());
        this.setVisible(true);
    }
    
    void hideMe(final boolean isOk){   
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(isOk){
                    contentType = (String)jcb_content_type.getSelectedItem();
                    charSet = (String)jcb_char_set.getSelectedItem();
                }
                else{
                    jcb_content_type.setSelectedItem(me.contentType);
                    jcb_char_set.setSelectedItem(me.charSet);
                }
                me.setVisible(false);
            }
        });
    }
    
    String getContentType(){
        return this.contentType;
    }
    
    void setContentType(String contentType){
        this.contentType = contentType;
        jcb_content_type.setSelectedItem(contentType);
    }
    
    String getCharSet(){
        return this.charSet;
    }
    
    void setCharSet(String charSet){
        this.charSet = charSet;
        jcb_char_set.setSelectedItem(charSet);
    }
}
