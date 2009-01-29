package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public interface IGlobalOptions {

    void acquire();

    String getProperty(String key);

    void release();

    void removeProperty(String key);

    void setProperty(String key, String value);

    void writeProperties();

}
