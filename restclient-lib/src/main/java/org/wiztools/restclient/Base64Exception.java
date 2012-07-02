package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public class Base64Exception extends RuntimeException {

    public Base64Exception(String string) {
        super(string);
    }

    public Base64Exception(Throwable thrwbl) {
        super(thrwbl);
    }

    public Base64Exception(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
