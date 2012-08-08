package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public interface ReqEntity extends Cloneable {

    String getCharset();

    String getContentType();

    String getContentTypeCharsetFormatted();

    Object clone();
}
