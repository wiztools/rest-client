package org.wiztools.restclient.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author subhash
 */
public class ContentTypesCommon {
    private static final List<String> contentTypes = new ArrayList<>();
    
    static {
        contentTypes.add("Accept");
        contentTypes.add("Accept-Charset");
        contentTypes.add("Accept-Encoding");
        contentTypes.add("Accept-Language");
        contentTypes.add("Content-MD5");
        contentTypes.add("Content-Type");
        contentTypes.add("Date");
        contentTypes.add("Expect");
        contentTypes.add("From");
        contentTypes.add("If-Match");
        contentTypes.add("If-Modified-Since");
        contentTypes.add("If-None-Match");
        contentTypes.add("If-Range");
        contentTypes.add("If-Unmodified-Since");
        contentTypes.add("Max-Forwards");
        contentTypes.add("Origin");
        contentTypes.add("Pragma");
        contentTypes.add("Range");
        contentTypes.add("Referer");
        contentTypes.add("TE");
        contentTypes.add("User-Agent");
        contentTypes.add("Upgrade");
        contentTypes.add("Via");
        contentTypes.add("Warning");
    }
    
    public static List<String> getCommon() {
        return Collections.unmodifiableList(contentTypes);
    }
}
