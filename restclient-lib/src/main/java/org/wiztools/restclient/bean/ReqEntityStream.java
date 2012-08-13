package org.wiztools.restclient.bean;

import java.io.InputStream;

/**
 *
 * @author subwiz
 */
public interface ReqEntityStream extends ReqEntitySimple {
    InputStream getBody();
    long getLength();
}
