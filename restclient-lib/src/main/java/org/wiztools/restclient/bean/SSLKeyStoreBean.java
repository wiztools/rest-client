package org.wiztools.restclient.bean;

import java.io.File;

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
        return password;
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
