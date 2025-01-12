package org.wiztools.restclient.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author subwiz
 */
public class ReqEntityUrlStreamBean extends AbstractReqEntitySimpleBean implements ReqEntityUrlStream {
    
    private final URL url;
    private InputStream is;
    private long length;

    public ReqEntityUrlStreamBean(ContentType contentType, URL url) {
        super(contentType);
        this.url = url;
    }
    
    private void init() throws IOException {
        if(is == null && length == 0) {
            URLConnection con = url.openConnection();
            is = con.getInputStream();
            length = con.getContentLength();
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }
    
    @Override
    public InputStream getBody() throws IOException {
        init();
        return is;
    }

    @Override
    public long getLength() throws IOException {
        init();
        return length;
    }
}
