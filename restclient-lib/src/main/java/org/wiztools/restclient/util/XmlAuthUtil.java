package org.wiztools.restclient.util;

import nu.xom.Element;
import org.wiztools.restclient.bean.*;

/**
 *
 * @author subwiz
 */
class XmlAuthUtil {
    private XmlAuthUtil() {}
    
    static Element getAuthElement(Auth auth) {
        Element eAuth = new Element("auth");
        
        if(auth instanceof BasicAuth) {
            eAuth.appendChild(getBasicAuthElement((BasicAuth)auth));
        }
        else if(auth instanceof DigestAuth) {
            eAuth.appendChild(getDigestAuthElement((DigestAuth)auth));
        }
        else if(auth instanceof NtlmAuth) {
            eAuth.appendChild(getNtlmAuthElement((NtlmAuth)auth));
        }
        else if(auth instanceof OAuth2BearerAuth) {
            eAuth.appendChild(getOAuth2BearerElement((OAuth2BearerAuth)auth));
        }
        
        return eAuth;
    }
    
    static Element getBasicAuthElement(BasicAuth auth) {
        Element e = new Element("basic");
        
        populateBasicDigestElement(e, auth);
        
        return e;
    }
    
    static Element getDigestAuthElement(DigestAuth auth) {
        Element e = new Element("digest");
        
        populateBasicDigestElement(e, auth);
        
        return e;
    }
    
    static Element getNtlmAuthElement(NtlmAuth auth) {
        Element e = new Element("ntlm");
        
        Element eDomain = new Element("domain");
        eDomain.appendChild(auth.getDomain());
        e.appendChild(eDomain);
        
        Element eWorkstation = new Element("workstation");
        eWorkstation.appendChild(auth.getWorkstation());
        e.appendChild(eWorkstation);
        
        populateUsernamePasswordElement(e, auth);
        
        return e;
    }
    
    static Element getOAuth2BearerElement(OAuth2BearerAuth auth) {
        Element e = new Element("oauth2-bearer");
        
        Element eToken = new Element("token");
        eToken.appendChild(auth.getOAuth2BearerToken());
        e.appendChild(eToken);
        
        return e;
    }
    
    static void populateBasicDigestElement(Element eParent, BasicDigestAuth auth) {
        Element eHost = new Element("host");
        eHost.appendChild(auth.getHost());
        eParent.appendChild(eHost);
        
        Element eRealm = new Element("realm");
        eRealm.appendChild(auth.getRealm());
        eParent.appendChild(eRealm);
        
        populateUsernamePasswordElement(eParent, auth);
    }
    
    static void populateUsernamePasswordElement(Element eParent, UsernamePasswordAuth auth) {
        Element eUsername = new Element("username");
        eUsername.appendChild(auth.getUsername());
        eParent.appendChild(eUsername);
        
        Element ePassword = new Element("password");
        ePassword.appendChild(Util.base64encode(new String(auth.getPassword())));
        eParent.appendChild(ePassword);
    }
}
