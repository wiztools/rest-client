package org.wiztools.restclient.bean;

import java.nio.charset.Charset;

/**
 *
 * @author subwiz
 */
public interface ReqEntitySimple extends ReqEntity {
    Charset getCharset();

    String getContentType();

    String getContentTypeCharsetFormatted();
}
