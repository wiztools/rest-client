package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
public final class ContentTypeUtil {
    
    private static String getContentTypeBeforeSemiColon(String contentType) {
        return (contentType.indexOf(';') != -1)?
                contentType.split(";")[0]:
                contentType;
    }
    
    public static boolean isXmlContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        if(ct.startsWith("application/xml")
                || ct.startsWith("text/xml")
                || ct.endsWith("+xml")){
            return true;
        }
        return false;
    }
    
    public static boolean isJsonContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        if(ct.startsWith("application/json")
                || ct.endsWith("+json")){
            return true;
        }
        return false;
    }
}
