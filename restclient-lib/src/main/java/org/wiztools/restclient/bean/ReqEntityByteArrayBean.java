package org.wiztools.restclient.bean;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityByteArrayBean other = (ReqEntityByteArrayBean) obj;
        if (!Arrays.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Arrays.hashCode(this.body);
        return hash;
    }
}
