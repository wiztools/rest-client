package org.wiztools.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.*;

/**
 *
 * @author subwiz
 */
class HTTPClientUtil {

    private static final Logger LOG = Logger.getLogger(HTTPClientUtil.class.getName());

    private static void appendHttpEntity(StringBuilder sb, HttpEntity e) {
        try {
            InputStream is = e.getContent();
            String encoding = e.getContentEncoding().getValue();
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

    static String getHTTPRequestTrace(HttpRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestLine());
        sb.append('\n');
        for (Header h : request.getAllHeaders()) {
            sb.append(h.getName()).append(": ").append(h.getValue()).append('\n');
        }
        sb.append('\n');

        // Check if the request is POST or PUT
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest) request;
            HttpEntity e = r.getEntity();
            if (e != null) {
                appendHttpEntity(sb, e);
            }
        }
        return sb.toString();
    }

    static String getHTTPResponseTrace(HttpResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getStatusLine()).append('\n');
        for (Header h : response.getAllHeaders()) {
            sb.append(h.getName()).append(": ").append(h.getValue()).append('\n');
        }
        sb.append('\n');
        HttpEntity e = response.getEntity();
        if (e != null) {
            appendHttpEntity(sb, e);
        }
        return sb.toString();
    }
    
    static AbstractHttpEntity getEntity(ReqEntitySimple bean) throws UnsupportedEncodingException {
        AbstractHttpEntity entity = null;
        if(bean instanceof ReqEntityString) {
            entity = new StringEntity(((ReqEntityString)bean).getBody());
        }
        else if(bean instanceof ReqEntityByteArray) {
            entity = new ByteArrayEntity(((ReqEntityByteArray)bean).getBody());
        }
        else if(bean instanceof ReqEntityStream) {
            entity = new InputStreamEntity(((ReqEntityStream)bean).getBody(),
                    ((ReqEntityStream)bean).getLength());
        }
        else if(bean instanceof ReqEntityFile) {
            entity = new FileEntity(((ReqEntityFile)bean).getBody());
        }
        return entity;
    }
}
