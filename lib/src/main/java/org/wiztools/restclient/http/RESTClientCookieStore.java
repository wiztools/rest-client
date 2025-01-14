package org.wiztools.restclient.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;

/**
 *
 * @author subwiz
 */
public class RESTClientCookieStore implements CookieStore {
    
    private List<Cookie> cookies = new ArrayList<Cookie>();

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return Collections.unmodifiableList(cookies);
    }

    @Override
    public boolean clearExpired(Date date) {
        // Do nothing
        return true;
    }

    @Override
    public void clear() {
        cookies.clear();
    }
    
}
