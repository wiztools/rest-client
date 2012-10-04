package org.wiztools.restclient.bean;

import java.io.Serializable;

/**
 *
 * @author subwiz
 */
public interface ReqEntityPart extends Serializable {
    String getName();
    ContentType getContentType();
}
