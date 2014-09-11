package org.wiztools.restclient.bean;

import java.util.List;

/**
 *
 * @author subwiz
 */
public interface ReqEntityMultipart extends ReqEntity {
    MultipartMode getMode();
    List<ReqEntityPart> getBody();
}
