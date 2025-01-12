package org.wiztools.restclient.persistence;

import java.io.File;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.KeyStoreType;
import org.wiztools.restclient.bean.SSLHostnameVerifier;
import org.wiztools.restclient.bean.SSLKeyStoreBean;
import org.wiztools.restclient.bean.SSLReq;
import org.wiztools.restclient.bean.SSLReqBean;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
class XmlSslUtil {
    private XmlSslUtil() {}
    
    static Element getSslReq(SSLReq req) {
        Element eSsl = new Element("ssl");
        
        if(req.isTrustAllCerts()) {
            Element e = new Element("ignore-cert-errs");
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
            e.addAttribute(new Attribute("type", req.getKeyStore().getType().name()));
            e.addAttribute(new Attribute("file", req.getKeyStore().getFile().getAbsolutePath()));
            e.addAttribute(new Attribute("password", Util.base64encode(new String(req.getKeyStore().getPassword()))));
            eSsl.appendChild(e);
        }
        
        // Trust store
        if(req.getTrustStore() != null) {
            Element e = new Element("truststore");
            e.addAttribute(new Attribute("type", req.getTrustStore().getType().name()));
            e.addAttribute(new Attribute("file", req.getTrustStore().getFile().getAbsolutePath()));
            e.addAttribute(new Attribute("password", Util.base64encode(new String(req.getTrustStore().getPassword()))));
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
            if(null != name) switch (name) {
                case "trust-self-signed-cert": // backward-compatibility...
                case "ignore-cert-errs":
                    out.setTrustAllCerts(true);
                    break;
                case "hostname-verifier":
                    out.setHostNameVerifier(SSLHostnameVerifier.valueOf(e.getValue()));
                    break;
                case "keystore":
                    final SSLKeyStoreBean keyStore = new SSLKeyStoreBean();
                    { // type:
                        final String typeStr = e.getAttributeValue("type");
                        if(StringUtil.isNotEmpty(typeStr))
                            keyStore.setType(KeyStoreType.valueOf(typeStr));
                    }
                    keyStore.setFile(new File(e.getAttributeValue("file")));
                    keyStore.setPassword(Util.base64decode(e.getAttributeValue("password")).toCharArray());
                    out.setKeyStore(keyStore);
                    break;
                case "truststore":
                    final SSLKeyStoreBean trustStore = new SSLKeyStoreBean();
                    { // type:
                        final String typeStr = e.getAttributeValue("type");
                        if(StringUtil.isNotEmpty(typeStr))
                            trustStore.setType(KeyStoreType.valueOf(typeStr));
                    }
                    trustStore.setFile(new File(e.getAttributeValue("file")));
                    trustStore.setPassword(Util.base64decode(e.getAttributeValue("password")).toCharArray());
                    out.setTrustStore(trustStore);
                    break;
            }
        }
        
        return out;
    }
}
