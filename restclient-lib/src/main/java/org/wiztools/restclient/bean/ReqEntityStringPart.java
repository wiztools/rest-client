package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface ReqEntityStringPart extends ReqEntityPart {
    String getPart();
    
    ContentType getContentType();
}
