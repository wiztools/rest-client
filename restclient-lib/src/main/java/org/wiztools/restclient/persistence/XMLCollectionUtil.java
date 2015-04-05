package org.wiztools.restclient.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.restclient.Versions;
import org.wiztools.restclient.bean.Request;

/**
 *
 * @author subwiz
 */
public final class XMLCollectionUtil {

    private XMLCollectionUtil() {}
    
    public static void writeRequestCollectionXML(final List<Request> requests, final File f)
            throws IOException, XMLException {
        XmlPersistenceWrite xUtl = new XmlPersistenceWrite();
        
        Element eRoot = new Element("request-collection");
        eRoot.addAttribute(new Attribute("version", Versions.CURRENT));
        for(Request req: requests) {
            Element e = xUtl.getRequestElement(req);
            eRoot.appendChild(e);
        }
        Document doc = new Document(eRoot);
        xUtl.writeXML(doc, f);
    }
    
    public static List<Request> getRequestCollectionFromXMLFile(final File f)
            throws IOException, XMLException {
        XmlPersistenceRead xUtlRead = new XmlPersistenceRead();
        
        List<Request> out = new ArrayList<>();
        Document doc = xUtlRead.getDocumentFromFile(f);
        Element eRoot = doc.getRootElement();
        if(!"request-collection".equals(eRoot.getLocalName())) {
            throw new XMLException("Expecting root element <request-collection>, but found: "
                    + eRoot.getLocalName());
        }
        final String version = eRoot.getAttributeValue("version");
        try {
            Versions.versionValidCheck(version);
        }
        catch(Versions.VersionValidationException ex) {
            throw new XMLException(ex);
        }
        xUtlRead.setReadVersion(version);
        
        Elements eRequests = doc.getRootElement().getChildElements();
        for(int i=0; i<eRequests.size(); i++) {
            Element eRequest = eRequests.get(i);
            Request req = xUtlRead.getRequestBean(eRequest);
            out.add(req);
        }
        return out;
    }
    
}
