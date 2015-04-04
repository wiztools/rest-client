package org.wiztools.restclient.persistence;

import java.io.File;
import java.io.IOException;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;

/**
 *
 * @author subwiz
 */
public interface Persistence {
    String XML_MIME = "application/xml";
    String JSON_MIME = "application/json";
    
    Request getRequestFromFile(File f) throws IOException, PersistenceException;
    Response getResponseFromFile(File f) throws IOException, PersistenceException;
    void writeRequest(Request req, File f) throws IOException, PersistenceException;
    void writeResponse(Response res, File f) throws IOException, PersistenceException;
}
