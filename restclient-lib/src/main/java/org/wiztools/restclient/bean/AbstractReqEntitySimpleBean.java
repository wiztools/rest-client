package org.wiztools.restclient.bean;

import java.nio.charset.Charset;
import org.wiztools.restclient.util.HttpUtil;

/**
 * This class is declared abstract because we do not want anyone to instantiate
 * this class. The only purpose of this class is extension.
 * @author subwiz
 */
public abstract class AbstractReqEntitySimpleBean implements ReqEntitySimple {
    
    protected final Charset charset;
    protected final String contentType;
    
    public AbstractReqEntitySimpleBean(String contentType, Charset charset) {
        this.contentType = contentType;
        this.charset = charset;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getContentTypeCharsetFormatted() {
        return HttpUtil.getFormattedContentType(contentType, charset);
    }
    
    @Override
    public Object clone() {
        return null;
    }
}
