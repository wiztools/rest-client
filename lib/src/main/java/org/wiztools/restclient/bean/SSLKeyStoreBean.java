package org.wiztools.restclient.bean;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import org.wiztools.restclient.util.SSLUtil;

/**
 *
 * @author subwiz
 */
public class SSLKeyStoreBean implements SSLKeyStore {
    
    private File file;
    private KeyStoreType type = KeyStoreType.JKS;
    private char[] password;

    public void setFile(File file) {
        this.file = file;
    }

    public void setType(KeyStoreType type) {
        this.type = type;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public KeyStoreType getType() {
        return type;
    }

    @Override
    public char[] getPassword() {
        if(type == KeyStoreType.PEM) {
            return SSLUtil.PEM_PWD.toCharArray();
        }
        return password;
    }
    
    @Override
    public KeyStore getKeyStore()
            throws KeyStoreException, IOException, InvalidKeySpecException,
            NoSuchAlgorithmException, CertificateException {
        return SSLUtil.getKeyStore(file, type, password);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@SSLKeyStore[");
        sb.append("type=").append(type).append(", ");
        sb.append("file=").append(file.getPath()).append(", ");
        sb.append("password=").append(
                (password!=null? password.length: 0)).append(", ");
        sb.append("]");
        return sb.toString();
    }
}
