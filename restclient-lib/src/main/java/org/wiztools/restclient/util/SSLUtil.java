package org.wiztools.restclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 *
 * @author subhash
 */
public final class SSLUtil {
    private SSLUtil() {}
    
    public static KeyStore getPemKeyStore(File file)
            throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {
        KeyStore store  = KeyStore.getInstance(KeyStore.getDefaultType());
        store.load(null);
        
        if(file != null) {
            try(FileInputStream fis = new FileInputStream(file)) {
                for (Certificate cert : CertificateFactory.getInstance("X509")
                        .generateCertificates(fis)) {
                    final X509Certificate crt = (X509Certificate) cert;
                    final String alias = crt.getSubjectX500Principal().getName();
                    store.setCertificateEntry(alias, crt);
                }
            }
        }
        return store;
    }
}
