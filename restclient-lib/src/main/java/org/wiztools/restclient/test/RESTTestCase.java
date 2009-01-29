package org.wiztools.restclient.test;

import groovy.util.GroovyTestCase;

/**
 *
 * @author schandran
 */
public class RESTTestCase extends GroovyTestCase {
    
    protected RoRequestBean request;
    protected RoResponseBean response;
    
    public void setRoRequestBean(final RoRequestBean request){
        this.request = request;
    }
    
    public void setRoResponseBean(final RoResponseBean response){
        this.response = response;
    }

}
