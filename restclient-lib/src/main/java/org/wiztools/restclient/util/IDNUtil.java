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
                "",
                IDN.toASCII(inUrl.getHost()),
                inUrl.getPort(),
                inUrl.getFile(), "", "").toURL();
        } catch(MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        } catch(URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
