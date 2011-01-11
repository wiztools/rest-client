package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public interface ReqEntity extends Cloneable {

    String getBody();

    String getCharSet();

    String getContentType();

    String getContentTypeCharsetFormatted();

    Object clone();
}
