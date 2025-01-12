package org.wiztools.restclient.bean;

import java.nio.charset.Charset;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.util.HttpUtil;

/**
 *
 * @author subwiz
 */
public class ContentTypeBean implements ContentType {
    
    private String contentType;
    private Charset charset;

    public ContentTypeBean(String contentType, Charset charset) {
        if(StringUtil.isEmpty(contentType)) {
            throw new IllegalArgumentException("content-type MUST NOT be empty!");
        }
        this.contentType = contentType;
        this.charset = charset;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContentTypeBean other = (ContentTypeBean) obj;
        if ((this.contentType == null) ? (other.contentType != null) : !this.contentType.equals(other.contentType)) {
            return false;
        }
        if (this.charset != other.charset && (this.charset == null || !this.charset.equals(other.charset))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 37 * hash + (this.charset != null ? this.charset.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return HttpUtil.getFormattedContentType(contentType, charset);
    }
}
