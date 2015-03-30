package org.wiztools.restclient.bean;

import org.wiztools.restclient.util.HttpUtil;

/**
 * This class is declared abstract because we do not want anyone to instantiate
 * this class. The only purpose of this class is extension.
 * @author subwiz
 */
public abstract class AbstractReqEntitySimpleBean implements ReqEntitySimple {
    
    protected final ContentType contentType;
    
    public AbstractReqEntitySimpleBean(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String getContentTypeCharsetFormatted() {
        return HttpUtil.getFormattedContentType(contentType);
    }
    
    @Override
    public Object clone() {
        return null;
    }
}
