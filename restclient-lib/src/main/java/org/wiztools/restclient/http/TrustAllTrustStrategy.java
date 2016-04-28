package org.wiztools.restclient.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.TrustStrategy;

/**
 *
 * @author subhash
 */
public class TrustAllTrustStrategy implements TrustStrategy {

    @Override
    public boolean isTrusted(X509Certificate[] xcs, String string) throws CertificateException {
        return true;
    }
    
}
