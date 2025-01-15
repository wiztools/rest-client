package org.wiztools.restclient.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Url {
    public static java.net.URL get(String spec) throws MalformedURLException {
        if(spec == null || spec.trim().isEmpty()) {
            throw new MalformedURLException("URL is empty");
        }
        try {
            return new java.net.URI(spec).toURL();
        } catch(URISyntaxException ex) {
            throw new MalformedURLException(ex.toString());
        }
    }
}
