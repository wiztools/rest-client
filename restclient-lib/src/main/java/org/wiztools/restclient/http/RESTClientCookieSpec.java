package org.wiztools.restclient.http;

import org.apache.http.cookie.*;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;

/**
 *
 * @author subwiz
 */
public class RESTClientCookieSpec implements CookieSpecFactory {
    
    public static final String NAME = "RESTClient_Cookie_Spec";

    @Override
    public CookieSpec newInstance(HttpParams params) {
        return new BrowserCompatSpec() {   
            @Override
            public void validate(Cookie cookie, CookieOrigin origin)
            throws MalformedCookieException {
                // Oh, I am easy
            }
        };
    }
}
