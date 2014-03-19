package org.wiztools.restclient.util;

import org.apache.http.entity.ContentType;

/**
 *
 * @author subwiz
 */
public final class ApacheHttpUtil {

    private ApacheHttpUtil() {}
    
    public static org.apache.http.entity.ContentType getHCContentType(
            org.wiztools.restclient.bean.ContentType ct) {
        return ContentType.create(ct.getContentType(), ct.getCharset());
    }
}
