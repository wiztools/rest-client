package org.wiztools.restclient.bean;

import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public class HTTPMethod {
    public static final HTTPMethod GET = new HTTPMethod("GET");
    public static final HTTPMethod POST = new HTTPMethod("POST");
    public static final HTTPMethod PUT = new HTTPMethod("PUT");
    public static final HTTPMethod PATCH = new HTTPMethod("PATCH");
    public static final HTTPMethod DELETE = new HTTPMethod("DELETE");
    public static final HTTPMethod HEAD = new HTTPMethod("HEAD");
    public static final HTTPMethod OPTIONS = new HTTPMethod("OPTIONS");
    public static final HTTPMethod TRACE = new HTTPMethod("TRACE");

    private final String method;
    
    public HTTPMethod(String method) {
        this.method = method;
    }

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
            return new HTTPMethod(method);
        }
    }
    
    public String name() {
        return method;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.method);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HTTPMethod other = (HTTPMethod) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }
        return true;
    }
    
}
