package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
final class ContentTypeUtil {
    static boolean isXmlContentType(String contentType) {
        if(contentType.startsWith("application/xml")
                || contentType.startsWith("text/xml")
                || contentType.endsWith("+xml")){
            return true;
        }
        return false;
    }
    
    static boolean isJsonContentType(String contentType) {
        if(contentType.startsWith("application/json")
                || contentType.endsWith("+json")){
            return true;
        }
        return false;
    }
}
