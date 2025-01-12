package org.wiztools.restclient.util;

import java.net.MalformedURLException;
import io.mola.galimatias.URL;
import io.mola.galimatias.GalimatiasParseException;

public class Url {
    public static java.net.URL get(String spec) throws MalformedURLException {
        try {
            return URL.parse(spec).toJavaURL();
            // return new java.net.URL(spec);
        } catch(GalimatiasParseException ex) {
            throw new MalformedURLException(ex.toString());
        }
    }
}
