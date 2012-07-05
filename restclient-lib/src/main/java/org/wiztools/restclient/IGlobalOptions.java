package org.wiztools.restclient;

import com.google.inject.ImplementedBy;

/**
 *
 * @author subwiz
 */
@ImplementedBy(GlobalOptions.class)
public interface IGlobalOptions {

    void acquire();

    String getProperty(String key);

    void release();

    void removeProperty(String key);

    void setProperty(String key, String value);

    void writeProperties();

}
