package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author subwiz
 */
public interface ReqEntity extends Cloneable {

    String getBody() throws UnsupportedEncodingException;

    byte[] getBodyBytes();

    String getCharSet();

    String getContentType();

    String getContentTypeCharsetFormatted();

    Object clone();
}
