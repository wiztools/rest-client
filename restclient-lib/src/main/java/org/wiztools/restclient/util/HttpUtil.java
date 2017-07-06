package org.wiztools.restclient.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ContentTypeBean;
import org.wiztools.restclient.bean.HTTPMethod;

/**
 *
 * @author subwiz
 */
public final class HttpUtil {
    
    public static final ContentType DEFAULT_CONTENT_TYPE = new ContentTypeBean("text/plain", Charsets.UTF_8);
    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
    
    public static ContentType getContentType(String header) {
        final String[] arr = header.split("\\s*;\\s*");
        if(arr.length == 1) {
            return new ContentTypeBean(header, null);
        }
        else {
            final String contentType = arr[0];
            for(int i=1; i<arr.length; i++) {
                final String headerPart = arr[i];
                if(headerPart.contains("charset=")) {
                    Pattern p = Pattern.compile("charset=(.+)");
                    Matcher m = p.matcher(headerPart);
                    if(m.matches()) {
                        Charset charset = Charset.forName(m.group(1));
                        return new ContentTypeBean(contentType, charset);
                    }
                }
            }
            return new ContentTypeBean(contentType, null);
        }
    }
    
    public static ContentType getContentType(MultiValueMap<String, String> headers) {
        for(String key: headers.keySet()) {
            if("content-type".equalsIgnoreCase(key.trim())) {
                return getContentType(headers.get(key).iterator().next());
            }
        }
        return null;
    }
    
    /**
     * Parses the HTTP response status line, and returns the status code.
     * @param statusLine The HTTP status line.
     * @return The status code from HTTP response status line.
     */
    public static int getStatusCodeFromStatusLine(final String statusLine){
        int retVal = -1;
        final String STATUS_PATTERN = "[^\\s]+\\s([0-9]{3})\\s.*";
        Pattern p = Pattern.compile(STATUS_PATTERN);
        Matcher m = p.matcher(statusLine);
        if(m.matches()){
            retVal = Integer.parseInt(m.group(1));
        }
        return retVal;
    }

    /**
     * Method formats content-type and charset for use as HTTP header value
     * @param contentType The content type string.
     * @param charset The charset string.
     * @return The formatted content-type and charset.
     */
    public static String getFormattedContentType(final String contentType, final String charset){
        String charsetFormatted = StringUtil.isEmpty(charset)? "": "; charset=" + charset;
        return contentType + charsetFormatted;
    }
    
    public static String getFormattedContentType(final String contentType, final Charset charset){
        return getFormattedContentType(contentType, (charset!=null? charset.name(): null));
    }
    
    public static String getFormattedContentType(final ContentType contentType){
        return getFormattedContentType(contentType.getContentType(), contentType.getCharset());
    }

    public static String getCharsetFromContentType(final String contentType) {
        Pattern p = Pattern.compile("^.+charset=([^;]+).*$");
        Matcher m = p.matcher(contentType);
        if(m.matches()) {
            return m.group(1).trim();
        }
        return null;
    }

    /**
     * Parses the Content-Type HTTP header and returns the MIME type part of the
     * response. For example, when receiving Content-Type header like:
     *
     * application/xml;charset=UTF-8
     *
     * This method will return "application/xml".
     * @param contentType The content-type string.
     * @return The mime part of the content-type string.
     */
    public static String getMimeFromContentType(final String contentType) {
        final int occurance = contentType.indexOf(';');
        if(occurance == -1) {
            return contentType;
        }
        else {
            return contentType.substring(0, occurance);
        }
    }
    
    private static String getContentTypeBeforeSemiColon(String contentType) {
        return (contentType.indexOf(';') != -1)?
                contentType.split(";")[0]:
                contentType;
    }
    
    public static boolean isWebImageContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.equals("image/jpeg")
                || ct.equals("image/png")
                || ct.equals("image/gif");
    }
    
    // All text content that needs to be rendered as text except those
    // starting with `text/`:
    private static List<String> TEXT_CT = Arrays.asList(new String[]{
        "application/x-javascript", "application/javascript"
    });
    
    public static boolean isTextContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("text/")
                || isXmlContentType(ct)
                || isJsonContentType(ct)
                || isFormUrlEncodedContentType(ct)
                || TEXT_CT.contains(ct);
    }
    
    public static boolean isXmlContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("application/xml")
                || ct.startsWith("text/xml")
                || ct.endsWith("+xml");
    }
    
    public static boolean isJsonContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("application/json")
                || ct.endsWith("+json");
    }
    
    public static boolean isJsContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("application/javascript")
                || ct.startsWith("application/x-javascript")
                || ct.startsWith("text/javascript");
    }
    
    public static boolean isCssContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("text/css");
    }

    public static boolean isHTMLContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("text/html")
                || ct.endsWith("+html");
    }

    public static boolean isFormUrlEncodedContentType(final String contentType) {
        final String ct = getContentTypeBeforeSemiColon(contentType);
        return ct.startsWith("application/x-www-form-urlencoded");
    }

    public static Charset getCharsetDefault(final ContentType type) {
        return type != null?
                (type.getCharset() != null? type.getCharset(): DEFAULT_CHARSET)
                : DEFAULT_CHARSET;
    }
    
    private static final List<String> entityEnclosingMethods = 
            Collections.unmodifiableList(
                    Arrays.asList(new String[]{"GET", "POST", "PUT", "PATCH", "DELETE"}));
    public static boolean isEntityEnclosingMethod(final String method) {
        return entityEnclosingMethods.contains(method);
    }
    
    public static boolean isEntityEnclosingMethod(final HTTPMethod method) {
        return isEntityEnclosingMethod(method.name());
    }
}
