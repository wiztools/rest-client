package org.wiztools.restclient.persistence;

import java.io.File;
import java.io.IOException;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;

/**
 *
 * @author subwiz
 */
public interface PersistenceRead {
    Request getRequestFromFile(File f) throws IOException, PersistenceException;
    Response getResponseFromFile(File f) throws IOException, PersistenceException;
}
