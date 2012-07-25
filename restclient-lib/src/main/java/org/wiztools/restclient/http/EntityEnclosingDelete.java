package org.wiztools.restclient.http;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 *
 * @author subwiz
 */
public class EntityEnclosingDelete extends HttpEntityEnclosingRequestBase {

    public EntityEnclosingDelete(String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return "DELETE";
    }
}
