package org.wiztools.restclient;

import java.io.File;
import java.nio.charset.Charset;
import org.wiztools.restclient.util.HttpUtil;

/**
 *
 * @author subwiz
 */
public class ReqEntityFileBean implements ReqEntityFile {
    
    private File body;
    private Charset charset;
    private String contentType;

    public void setBody(File body) {
        this.body = body;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public File getBody() {
        return body;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityFileBean other = (ReqEntityFileBean) obj;
        if (this.body != other.body && (this.body == null || !this.body.equals(other.body))) {
            return false;
        }
        if (this.charset != other.charset && (this.charset == null || !this.charset.equals(other.charset))) {
            return false;
        }
        if ((this.contentType == null) ? (other.contentType != null) : !this.contentType.equals(other.contentType)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 79 * hash + (this.charset != null ? this.charset.hashCode() : 0);
        hash = 79 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        return hash;
    }
}
