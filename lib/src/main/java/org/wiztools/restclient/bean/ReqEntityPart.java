package org.wiztools.restclient.bean;

import java.io.Serializable;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author subwiz
 */
public interface ReqEntityPart extends Serializable {
    String getName();
    ContentType getContentType();
    MultiValueMap<String, String> getFields();
}
