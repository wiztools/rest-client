package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface UsernamePasswordAuth extends Auth {
    String getUsername();
    char[] getPassword();
}
