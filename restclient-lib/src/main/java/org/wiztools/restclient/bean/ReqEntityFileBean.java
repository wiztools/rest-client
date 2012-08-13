package org.wiztools.restclient.bean;

import java.io.File;
import java.nio.charset.Charset;

/**
 *
 * @author subwiz
 */
public class ReqEntityFileBean extends AbstractReqEntitySimpleBean implements ReqEntityFile {
    
    private final File body;

    public ReqEntityFileBean(File body, String contentType, Charset charset) {
        super(contentType, charset);
        this.body = body;
    }

    @Override
    public File getBody() {
        return body;
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
