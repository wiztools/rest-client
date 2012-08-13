package org.wiztools.restclient;

import groovy.util.GroovyTestCase;

/**
 *
 * @author schandran
 */
public class RESTTestCase extends GroovyTestCase {
    
    protected Request request;
    protected Response response;
    
    public void setRequest(final Request request){
        this.request = request;
    }
    
    public void setResponse(final Response response){
        this.response = response;
    }

}
