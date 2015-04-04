package org.wiztools.restclient.persistence;

/**
 *
 * @author subwiz
 */
public class XMLException extends PersistenceException {
    public XMLException(String msg){
        super(msg);
    }
    
    public XMLException(String msg, Throwable t){
        super(msg, t);
    }

    public XMLException(Throwable cause) {
        super(cause);
    }
}