package org.wiztools.restclient.bean;

import java.util.List;

/**
 *
 * @author subwiz
 */
public interface ReqEntityMultipart extends ReqEntity {
    MultipartFormat getFormat();
    List<ReqEntityPart> getBody();
}
