package org.wiztools.restclient.util;

import java.io.File;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.SSLHostnameVerifier;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.bean.SSLReqBean;

/**
 *
 * @author subwiz
 */
class XmlSslUtil {
    private XmlSslUtil() {}
    
    static Element getSslReq(SSLReq req) {
        Element eSsl = new Element("ssl");
        
        if(req.isTrustSelfSignedCert()) {
            Element e = new Element("trust-self-signed-cert");
            eSsl.appendChild(e);
        }
        
        { // Hostname verifier
            Element e = new Element("hostname-verifier");
            e.appendChild(req.getHostNameVerifier().name());
            eSsl.appendChild(e);
        }
        
        // Key store
        if(req.getKeyStore() != null) {
            Element e = new Element("keystore");
            e.addAttribute(new Attribute("file", req.getKeyStore().getAbsolutePath()));
            e.addAttribute(new Attribute("password", Util.base64encode(new String(req.getKeyStorePassword()))));
            eSsl.appendChild(e);
        }
        
        // Trust store
        if(req.getTrustStore() != null) {
            Element e = new Element("truststore");
            e.addAttribute(new Attribute("file", req.getTrustStore().getAbsolutePath()));
            e.addAttribute(new Attribute("password", Util.base64encode(new String(req.getTrustStorePassword()))));
            eSsl.appendChild(e);
        }
        
        return eSsl;
    }
    
    static SSLReq getSslReq(Element eSsl) {
        SSLReqBean out = new SSLReqBean();
        
        Elements eChildren = eSsl.getChildElements();
        for(int i=0; i<eChildren.size(); i++) {
            Element e = eChildren.get(i);
            final String name = e.getLocalName();
            if("trust-self-signed-cert".equals(name)) {
                out.setTrustSelfSignedCert(true);
            }
            else if("hostname-verifier".equals(name)) {
                out.setHostNameVerifier(SSLHostnameVerifier.valueOf(e.getValue()));
            }
            else if("keystore".equals(name)) {
                out.setKeyStore(new File(e.getAttributeValue("file")));
                out.setKeyStorePassword(Util.base64decode(e.getAttributeValue("password")).toCharArray());
            }
            else if("truststore".equals(name)) {
                out.setTrustStore(new File(e.getAttributeValue("file")));
                out.setTrustStorePassword(Util.base64decode(e.getAttributeValue("password")).toCharArray());
            }
        }
        
        return out;
    }
}
