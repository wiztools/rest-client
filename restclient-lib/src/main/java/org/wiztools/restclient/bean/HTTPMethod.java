package org.wiztools.restclient.bean;

import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public enum HTTPMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE;

    private static final Logger LOG = Logger.getLogger(HTTPMethod.class.getName());

    public static HTTPMethod get(final String method){
        if("GET".equals(method)){
            return GET;
        }
        else if("POST".equals(method)){
            return POST;
        }
        else if("PUT".equals(method)){
            return PUT;
        }
        else if("PATCH".equals(method)) {
            return PATCH;
        }
        else if("DELETE".equals(method)){
            return DELETE;
        }
        else if("HEAD".equals(method)){
            return HEAD;
        }
        else if("OPTIONS".equals(method)){
            return OPTIONS;
        }
        else if("TRACE".equals(method)){
            return TRACE;
        }
        else{
            LOG.warning("Unknown HTTP method encountered: " + method);
            LOG.warning("Setting default HTTP method: GET");
            return GET;
        }
    }
}
