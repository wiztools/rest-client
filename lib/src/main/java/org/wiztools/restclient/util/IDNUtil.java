package org.wiztools.restclient.util;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility to convert to IDN name.
 * @author subwiz
 */
public final class IDNUtil {
    public static URL getIDNizedURL(URL inUrl) throws IllegalArgumentException {
        try {
            return new URI(inUrl.getProtocol(),
                null,
                IDN.toASCII(inUrl.getHost()),
                inUrl.getPort(),
                inUrl.getFile(), inUrl.getQuery(), null).toURL();
        } catch(MalformedURLException | URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
