package org.wiztools.restclient.bean;

import java.nio.charset.Charset;

/**
 *
 * @author subwiz
 */
public interface ReqEntityStringPart extends ReqEntityPart {
    String getPart();
    
    String getContentType();
    Charset getCharset();
}
