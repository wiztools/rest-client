package org.wiztools.restclient;

import java.util.List;

/**
 *
 * @author subwiz
 */
public interface ReqEntityMultipart extends ReqEntity {
    List<ReqEntityPart> getBody();
}
