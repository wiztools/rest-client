package org.wiztools.restclient.ui.resbody;

import java.awt.Font;
import java.awt.GridLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JScrollPane;
import org.wiztools.restclient.ContentType;
import org.wiztools.restclient.ui.FontableEditor;
import org.wiztools.restclient.util.HttpUtil;

/**
 *
 * @author subwiz
 */
public class ResBodyPanel extends AbstractResBody implements FontableEditor {
    
    @Inject private ResBodyTextPanel jp_text;
    @Inject private ResBodyImagePanel jp_image;
    @Inject private ResBodyBinaryPanel jp_binary;
    @Inject private ResBodyNonePanel jp_none;
    
    private JScrollPane jsp = new JScrollPane();
    
    @PostConstruct
    protected void init() {
        jsp.setViewportView(jp_none);
        
        setLayout(new GridLayout());
        add(jsp);
    }

    @Override
    public void setBody(byte[] body, ContentType type) {
        // Call super:
        super.setBody(body, type);
        
        // Display the new body:
        if(HttpUtil.isTextContentType(type.getContentType())) {
            jp_text.setBody(body, type);
            jsp.setViewportView(jp_text);
        }
        else if(HttpUtil.isWebImageContentType(type.getContentType())) {
            jp_image.setBody(body, type);
            jsp.setViewportView(jp_image);
        }
        else {
            jp_binary.setBody(body, type);
            jsp.setViewportView(jp_binary);
        }
    }
    
    @Override
    public void setEditorFont(Font font) {
        jp_text.setEditorFont(font);
    }

    @Override
    public Font getEditorFont() {
        return jp_text.getEditorFont();
    }

    @Override
    public void clearUI() {
        jp_text.clearBody();
        jp_image.clearBody();
        jp_binary.clearBody();
        jsp.setViewportView(jp_none);
    }
    
}
