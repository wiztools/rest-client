package org.wiztools.restclient.ui.reqbody;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ContentTypeBean;
import org.wiztools.restclient.ui.EscapableDialog;
import org.wiztools.restclient.ui.RESTUserInterface;

/**
 *
 * @author schandran
 */
public class BodyContentTypeDialog extends EscapableDialog {
    
    private static final String[] contentTypeArr;
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";
    
    private static final String[] charSetArr;
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    public static final String PARAM_CONTENT_TYPE_STR = "application/x-www-form-urlencoded";
    public static final Charset PARAM_CHARSET = Charsets.UTF_8;
    public static final ContentType PARAM_CONTENT_TYPE = new ContentTypeBean(PARAM_CONTENT_TYPE_STR, PARAM_CHARSET);

    // Content-type
    static{
        InputStream is = BodyContentTypeDialog.class.getClassLoader().getResourceAsStream("org/wiztools/restclient/mime.types");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        String[] arr = null;
        try{
            List<String> ll = new ArrayList<>();
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
    
    
    private final JComboBox<String> jcb_content_type = new JComboBox<>(contentTypeArr);
    private final JComboBox<String> jcb_charset = new JComboBox<>(charSetArr);
    
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String charset = DEFAULT_CHARSET;
    
    @Inject
    public BodyContentTypeDialog(RESTUserInterface rest_ui){
        // true means Modal:
        super(rest_ui.getFrame(), true);

        setTitle("Body Content-type");
        jcb_content_type.setSelectedItem(DEFAULT_CONTENT_TYPE);
        jcb_charset.setSelectedItem(DEFAULT_CHARSET);
    }
    
    @PostConstruct
    protected void init(){
        // [Issue: 49]: Making editable:
        jcb_content_type.setEditable(true);
        jcb_charset.setEditable(true);
        
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new BorderLayout());
        JPanel jp_center_west = new JPanel();
        jp_center_west.setLayout(new GridLayout(2, 1, 5, 5));
        JLabel jl_content_type = new JLabel("Content-type: ");
        jl_content_type.setLabelFor(jcb_content_type);
        JLabel jl_char_set = new JLabel("Charset: ");
        jl_char_set.setLabelFor(jcb_charset);
        jp_center_west.add(jl_content_type);
        jp_center_west.add(jl_char_set);
        jp_center.add(jp_center_west, BorderLayout.WEST);
        JPanel jp_center_center = new JPanel();
        jp_center_center.setLayout(new GridLayout(2, 1, 5, 5));
        jp_center_center.add(jcb_content_type);
        jp_center_center.add(jcb_charset);
        jp_center.add(jp_center_center, BorderLayout.CENTER);
        jp.add(jp_center, BorderLayout.CENTER);
        
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton jb_ok = new JButton("Ok");
        jb_ok.setMnemonic('o');
        getRootPane().setDefaultButton(jb_ok);
        jb_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                hideMe(true);
            }
        });
        JButton jb_cancel = new JButton("Cancel");
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            @Override
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

        // Auto-completion decoration
        AutoCompleteDecorator.decorate(jcb_charset);
        AutoCompleteDecorator.decorate(jcb_content_type);
        
        this.setContentPane(jp_encp);
        this.pack();
    }
    
    @Override
    public void doEscape(AWTEvent event){
        hideMe(false);
    }
    
    void hideMe(final boolean isOk){   
        if(isOk){
            contentType = (String)jcb_content_type.getSelectedItem();
            charset = (String)jcb_charset.getSelectedItem();

            // Fire all listeners:
            for(ContentTypeCharsetChangeListener listener: listeners){
                listener.changed(contentType, charset);
            }
        }
        else{
            jcb_content_type.setSelectedItem(contentType);
            jcb_charset.setSelectedItem(charset);
        }
        setVisible(false);
    }
    
    String getContentType(){
        return this.contentType;
    }
    
    void setContentType(final String contentType){
        this.contentType = contentType;
        jcb_content_type.setSelectedItem(contentType);
        for(ContentTypeCharsetChangeListener listener: listeners){
            listener.changed(contentType, charset);
        }
    }
    
    String getCharsetString(){
        return this.charset;
    }
    
    Charset getCharset() {
        if(StringUtil.isEmpty(charset)) {
            return null;
        }
        return Charset.forName(charset);
    }
    
    void setCharset(Charset c) {
        setCharset(c.name());
    }
    
    void setCharset(final String charset){
        this.charset = charset;
        jcb_charset.setSelectedItem(charset);
        for(ContentTypeCharsetChangeListener listener: listeners){
            listener.changed(contentType, charset);
        }
    }
    
    private List<ContentTypeCharsetChangeListener> listeners = new ArrayList<ContentTypeCharsetChangeListener>();
    
    public void addContentTypeCharSetChangeListener(ContentTypeCharsetChangeListener listener){
        listeners.add(listener);
    }
}
