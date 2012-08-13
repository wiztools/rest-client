package org.wiztools.restclient.bean;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * @author subwiz
 */
public class ReqEntityByteArrayBean extends AbstractReqEntitySimpleBean implements ReqEntityByteArray {
    
    private byte[] body;

    public ReqEntityByteArrayBean(byte[] body, ContentType contentType) {
        super(contentType);
        this.body = body;
    }

    @Override
    public byte[] getBody() {
        return Arrays.copyOf(body, body.length);
    }

    @Override
    public Object clone() {
        ReqEntityByteArrayBean out = new ReqEntityByteArrayBean(
                Arrays.copyOf(body, body.length), contentType);
        return out;
    }
}
