package org.wiztools.restclient;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.*;
import org.wiztools.restclient.bean.ReqEntityByteArray;
import org.wiztools.restclient.bean.ReqEntitySimple;
import org.wiztools.restclient.bean.ReqEntityFile;
import org.wiztools.restclient.bean.ReqEntityStream;
import org.wiztools.restclient.bean.ReqEntityString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
class HTTPClientUtil {

    private static final Logger LOG = Logger.getLogger(HTTPClientUtil.class.getName());

    private static void appendHttpEntity(StringBuilder sb, HttpEntity e) {
        try {
            InputStream is = e.getContent();
            String encoding = e.getContentEncoding();
            System.out.println(encoding);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName(encoding)));
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
        }
    }

    static String getHTTPRequestTrace(HttpUriRequest request) {
        StringBuilder sb = new StringBuilder();
        // Construct the request line:
        sb.append(request.getMethod())
                .append(" ").append(request.getPath())
                .append(" ").append(request.getScheme())
                .append(" ").append(request.getVersion());
        sb.append('\n');
        for (Header h : request.getHeaders()) {
            sb.append(h.getName()).append(": ").append(h.getValue()).append('\n');
        }
        sb.append('\n');

        // Request has entity:
        HttpEntity e = request.getEntity();
        if (e != null) {
            appendHttpEntity(sb, e);
        }
        return sb.toString();
    }

    static String getHTTPResponseTrace(ClassicHttpResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getCode() + " " + response.getReasonPhrase()).append('\n');
        for (Header h : response.getHeaders()) {
            sb.append(h.getName()).append(": ").append(h.getValue()).append('\n');
        }
        sb.append('\n');
        HttpEntity e = response.getEntity();
        if (e != null) {
            appendHttpEntity(sb, e);
        }
        return sb.toString();
    }
    
    static AbstractHttpEntity getEntity(ReqEntitySimple bean)
            throws UnsupportedEncodingException, IOException {
        AbstractHttpEntity entity = null;
        ContentType contentType = null;
        if (bean.getContentType() != null) {
            org.wiztools.restclient.bean.ContentType ct = bean.getContentType();
            contentType = ContentType.create(ct.getContentType(), ct.getCharset());
        }
        if (bean instanceof ReqEntityString) {
            entity = new StringEntity(((ReqEntityString) bean).getBody(), contentType);
        }
        else if (bean instanceof ReqEntityByteArray) {
            entity = new ByteArrayEntity(((ReqEntityByteArray) bean).getBody(), contentType);
        }
        else if (bean instanceof ReqEntityStream) {
            entity = new InputStreamEntity(((ReqEntityStream) bean).getBody(),
                    ((ReqEntityStream) bean).getLength(), contentType);
        }
        else if (bean instanceof ReqEntityFile) {
            entity = new FileEntity(((ReqEntityFile) bean).getBody(), contentType);
        }
        return entity;
    }
    
    public static ContentType getContentType(
            org.wiztools.restclient.bean.ContentType ct) {
        return ContentType.create(ct.getContentType(), ct.getCharset());
    }
}
