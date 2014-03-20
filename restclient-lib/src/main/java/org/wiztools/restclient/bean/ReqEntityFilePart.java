package org.wiztools.restclient.bean;

import java.io.File;

/**
 *
 * @author subwiz
 */
public interface ReqEntityFilePart extends ReqEntityPart {
    File getPart();
    String getFilename();
}
