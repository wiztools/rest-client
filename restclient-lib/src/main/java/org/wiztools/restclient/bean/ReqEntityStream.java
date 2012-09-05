package org.wiztools.restclient.bean;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author subwiz
 */
public interface ReqEntityStream extends ReqEntitySimple {
    InputStream getBody() throws IOException;
    long getLength() throws IOException;
}
