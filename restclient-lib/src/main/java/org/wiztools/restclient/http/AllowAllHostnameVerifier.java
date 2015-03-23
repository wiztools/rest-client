package org.wiztools.restclient.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *
 * @author subwiz
 */
public class AllowAllHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String string, SSLSession ssls) {
        return true;
    }
    
}
