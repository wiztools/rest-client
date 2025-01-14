package org.wiztools.restclient.ui.reqbody;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.persistence.PersistenceException;
import org.wiztools.restclient.util.XMLUtil;

/**
 *
 * @author subwiz
 */
final class ContentTypeSelectorOnFile {

    private ContentTypeSelectorOnFile() {}
    
    static void select(ContentTypeCharsetComponent jp_content_type_charset,
            File file, Component parent) {
        final String mime = FileUtil.getMimeType(file);
        if(!mime.equals("content/unknown")) {
            final String origContentType = jp_content_type_charset.getContentType().getContentType();
            if(!mime.equals(origContentType)) {
                final int result = JOptionPane.showConfirmDialog(parent,
                        "The content-type selected (" + origContentType + ") does NOT match\n"
                        + "the computed file mime type (" + mime + ")\n"
                        + "Do you want to update the content-type to `" + mime + "'?",
                        "Mime-type mismatch correction",
                        JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION) {
                    // Set content type
                    jp_content_type_charset.setContentType(mime);
                    
                    // Check if XML content type:
                    if(XMLUtil.XML_MIME.equals(mime)){
                        try {
                            String charset = XMLUtil.getDocumentCharset(file);
                            if(charset != null && !(charset.equals(jp_content_type_charset.getCharsetString()))) {
                                final int charsetYesNo = JOptionPane.showConfirmDialog(parent,
                                        "Change charset to `" + charset + "'?",
                                        "Change charset?",
                                        JOptionPane.YES_NO_OPTION);
                                if(charsetYesNo == JOptionPane.YES_OPTION) {
                                    jp_content_type_charset.setCharset(Charset.forName(charset));
                                }
                            }
                        }
                        catch(IOException | PersistenceException ex) {
                            // do nothing!
                        }
                    }
                }
            }
        }
    }
}
