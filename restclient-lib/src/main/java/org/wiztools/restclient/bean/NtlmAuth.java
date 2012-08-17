package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface NtlmAuth extends UsernamePasswordAuth {

    String getDomain();

    String getWorkstation();
    
}
