package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface ReqEntitySimple extends ReqEntity {
    ContentType getContentType();
    String getContentTypeCharsetFormatted();
}
