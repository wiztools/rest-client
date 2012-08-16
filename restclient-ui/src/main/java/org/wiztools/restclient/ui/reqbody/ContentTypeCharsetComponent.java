package org.wiztools.restclient.ui.reqbody;

import com.google.inject.ImplementedBy;
import java.nio.charset.Charset;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ContentTypeCharsetComponentImpl.class)
public interface ContentTypeCharsetComponent extends ViewPanel {

    int TEXT_FIELD_LENGTH = 26;
    
    void disableComponent();

    void enableComponent();

    Charset getCharset();

    String getCharsetString();

    ContentType getContentType();

    String getContentTypeCharsetString();

    String getContentTypeString();

    void requestFocus();

    void setCharset(Charset charset);

    void setContentType(String contentType);

    void setContentTypeCharset(ContentType contentType);

    void setContentTypeCharset(String contentType, Charset charset);
    
}
