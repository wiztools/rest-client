package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.io.UnsupportedEncodingException;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.apache.commons.codec.binary.Base64;
import org.wiztools.commons.CommonCharset;

/**
 *
 * @author subwiz
 */
public class BodyPanel extends JPanel {

    private JLabel jl = new JLabel();
    private ScriptEditor se = ScriptEditorFactory.getXMLScriptEditor();

    private byte[] data;

    public BodyPanel(){
        setLayout(new BorderLayout(0, 0));
    }

    public JComponent getEditorView(){
        return se.getEditorView();
    }

    public ScriptEditor getScriptEditor(){
        return se;
    }

    public void setScriptEditor(ScriptEditor se){
        this.se = se;
    }

    public void setText(String str, boolean editable){
        try{
            setData(str.getBytes(CommonCharset.UTF_8),
                "text/plain",
                CommonCharset.UTF_8.name(),
                editable);
        }
        catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
            // Will never come here!
        }
    }

    public void setData(byte[] data, String contentType, String charset, boolean editable)
            throws UnsupportedEncodingException{
        this.data = data;
        removeAll();
        // Check if text:
        if(contentType.startsWith("text/")
                || contentType.equals("application/xml")
                || contentType.equals("application/json")){
            if(charset == null){
                se.setText(new String(data, CommonCharset.UTF_8));
            }
            else{
                se.setText(new String(data, charset));
            }
            se.setEditable(editable);
            add(se.getEditorView(), BorderLayout.CENTER);
        }
        // check if supported image:
        else if(contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/gif")){
            jl.setIcon(new ImageIcon(data));
            add(jl, BorderLayout.CENTER);
        }
        // Base64Encode and display as text:
        else{
            String encodedData = Base64.encodeBase64String(data);
            se.setText(encodedData);
            se.setEditable(false);
            add(se.getEditorView(), BorderLayout.CENTER);
        }
        validate();
        repaint();
    }

    public byte[] getData(){
        return data;
    }

    public void clearData(){
        data = null;
        jl.setIcon(null);
        se.setText("");
        removeAll();
        validate();
    }
}
