package org.wiztools.restclient;

import java.nio.charset.Charset;

/**
 *
 * @author subwiz
 */
public interface ReqEntity extends Cloneable {

    Charset getCharset();

    String getContentType();

    String getContentTypeCharsetFormatted();

    Object clone();
}
