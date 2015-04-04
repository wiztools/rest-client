package org.wiztools.restclient.persistence;

/**
 *
 * @author subwiz
 */
public class PersistenceException extends RuntimeException {

//    public PersistenceException() {
//    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
