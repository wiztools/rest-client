package org.wiztools.restclient.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Url {
    public static java.net.URL get(String spec) throws MalformedURLException {
        try {
            return new java.net.URI(spec).toURL();
        } catch(URISyntaxException ex) {
            throw new MalformedURLException(ex.toString());
        }
    }
}
