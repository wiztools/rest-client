package org.wiztools.restclient.bean;

import java.io.File;

/**
 *
 * @author subwiz
 */
public interface SSLKeyStore {
    File getFile();
    KeyStoreType getType();
    char[] getPassword();
}
