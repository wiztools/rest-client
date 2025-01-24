package org.wiztools.restclient;

import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.Request;
import groovy.test.GroovyTestCase;

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
