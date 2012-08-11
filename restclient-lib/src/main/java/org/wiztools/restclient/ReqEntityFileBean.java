package org.wiztools.restclient;

import java.io.File;
import java.nio.charset.Charset;

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
}
