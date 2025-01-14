package org.wiztools.restclient.bean;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 * @author subwiz
 */
public interface SSLKeyStore {
    File getFile();
    KeyStoreType getType();
    char[] getPassword();
    
    KeyStore getKeyStore() throws KeyStoreException,
            IOException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            CertificateException;
}
