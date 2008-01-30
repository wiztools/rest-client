package org.wiztools.restclient.xml;

/**
 *
 * @author schandran
 */
public class XMLException extends Exception{
    public XMLException(String msg){
        super(msg);
    }
    
    public XMLException(String msg, Throwable t){
        super(msg, t);
    }
}