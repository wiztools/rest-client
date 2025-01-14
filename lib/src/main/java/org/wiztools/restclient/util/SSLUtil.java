package org.wiztools.restclient.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.wiztools.restclient.bean.KeyStoreType;

/**
 *
 * @author subhash
 */
public final class SSLUtil {
    private SSLUtil() {}
    
    public static final String PEM_PWD = "changeit";
    
    public static KeyStore getKeyStore(File file, KeyStoreType type, char[] password)
            throws KeyStoreException,
                IOException,
                InvalidKeySpecException,
                NoSuchAlgorithmException,
                CertificateException {
        if(type == KeyStoreType.PEM) {
            return getPemKeyStore(file);
        }
        
        // Other KeyStore types:
        KeyStore store  = KeyStore.getInstance(type.name());
        if(file != null) {
            try(FileInputStream instream = new FileInputStream(file)) {
                store.load(instream, password);
            }
        }
        return store;
    }
    
    private static KeyStore getPemKeyStore(File file)
            throws KeyStoreException,
                IOException,
                InvalidKeySpecException,
                NoSuchAlgorithmException,
                CertificateException {
        KeyStore store  = KeyStore.getInstance(KeyStore.getDefaultType());
        store.load(null);
        
        if(file != null) {
            byte[] certAndKey = Files.readAllBytes(file.toPath());
            
            byte[] certBytes = parseDERFromPEM(certAndKey,
                    "-----BEGIN .*CERTIFICATE-----", "-----END .*CERTIFICATE-----");
            byte[] pvtKeyBytes = parseDERFromPEM(certAndKey,
                    "-----BEGIN .*PRIVATE KEY-----", "-----END .*PRIVATE KEY-----");
            byte[] pubKeyBytes = parseDERFromPEM(certAndKey,
                    "-----BEGIN .*PUBLIC KEY-----", "-----END .*PUBLIC KEY-----");
            
            X509Certificate cert = null;
            if(certBytes != null) {
                cert = generateCertFromDER(certBytes);
                String alias = cert.getSubjectX500Principal().getName();
                store.setCertificateEntry(alias, cert);
            }
            
            Certificate[] chain = cert==null?
                    new Certificate[]{}: new Certificate[] {cert};
            
            if(pvtKeyBytes != null) {
                RSAPrivateKey key  = generatePvtKeyFromDER(pvtKeyBytes);
                store.setKeyEntry("key-alias", key, PEM_PWD.toCharArray(),
                        chain);
            }
            
            if(pubKeyBytes != null) {
                RSAPublicKey key = generatePubKeyFromDER(pubKeyBytes);
                store.setKeyEntry("pubkey-alias", key, PEM_PWD.toCharArray(),
                        null);
            }
        }
        return store;
    }
    
    protected static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        if(tokens.length < 2) { // no results found!
            return null;
        }
        tokens = tokens[1].split(endDelimiter);
        if(tokens.length < 2) { // no results found!
            return null;
        }
        return DatatypeConverter.parseBase64Binary(tokens[0]);
    }

    protected static RSAPrivateKey generatePvtKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey)factory.generatePrivate(spec);
    }
    
    protected static RSAPublicKey generatePubKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey)factory.generatePublic(spec);
    }

    protected static X509Certificate generateCertFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }
}
