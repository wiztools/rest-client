/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.test;

import groovy.util.GroovyTestCase;

/**
 *
 * @author schandran
 */
public class RESTTestCase extends GroovyTestCase {
    
    private RoRequestBean request;
    private RoResponseBean response;
    
    public void setRoRequestBean(final RoRequestBean request){
        this.request = request;
    }
    
    public void setRoResponseBean(final RoResponseBean response){
        this.response = response;
    }
    
}
