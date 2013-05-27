package org.wiztools.restclient.util;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility to convert to IDN name.
 * @author subwiz
 */
public final class IDNUtil {
    public static URL getIDNizedURL(URL inUrl) throws MalformedURLException {
        return new URL(inUrl.getProtocol(),
                IDN.toASCII(inUrl.getHost()),
                inUrl.getPort(),
                inUrl.getFile());
    }
}
