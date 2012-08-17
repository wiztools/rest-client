package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public interface BasicDigestAuth extends UsernamePasswordAuth {
    String getHost();
    String getRealm();
    boolean isPreemptive();
}
