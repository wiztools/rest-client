package org.wiztools.restclient.ntlm;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

/**
 *
 * @author subwiz
 */
public class NTLMSchemeFactory implements AuthSchemeFactory {
    public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme(new JCIFSEngine());
    }
}
