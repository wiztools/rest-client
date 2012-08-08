package org.wiztools.restclient;

import java.io.InputStream;

/**
 *
 * @author subwiz
 */
public interface ReqEntityStream extends ReqEntitySimple {
    InputStream getBody();
    long getLength();
}
