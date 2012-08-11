package org.wiztools.restclient.ui.resbody;

import org.wiztools.restclient.ContentType;

/**
 *
 * @author subwiz
 */
public interface ResBodyOps {
    void clearBody();
    void setBody(byte[] data, ContentType type);
    byte[] getBody();
}
