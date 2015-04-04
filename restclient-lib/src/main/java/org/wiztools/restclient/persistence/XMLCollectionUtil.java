package org.wiztools.restclient.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.restclient.RCConstants;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.bean.Request;

/**
 *
 * @author subwiz
 */
public final class XMLCollectionUtil {

    private XMLCollectionUtil() {}
    
    public static void writeRequestCollectionXML(final List<Request> requests, final File f)
            throws IOException, XMLException {
        Element eRoot = new Element("request-collection");
        eRoot.addAttribute(new Attribute("version", RCConstants.VERSION));
        for(Request req: requests) {
            Element e = XMLUtil.getRequestElement(req);
            eRoot.appendChild(e);
        }
        Document doc = new Document(eRoot);
        XMLUtil.writeXML(doc, f);
    }
    
    public static List<Request> getRequestCollectionFromXMLFile(final File f)
            throws IOException, XMLException {
        List<Request> out = new ArrayList<Request>();
        Document doc = XMLUtil.getDocumentFromFile(f);
        Element eRoot = doc.getRootElement();
        if(!"request-collection".equals(eRoot.getLocalName())) {
            throw new XMLException("Expecting root element <request-collection>, but found: "
                    + eRoot.getLocalName());
        }
        final String version = eRoot.getAttributeValue("version");
        XMLUtil.checkIfVersionValid(version);
        Elements eRequests = doc.getRootElement().getChildElements();
        for(int i=0; i<eRequests.size(); i++) {
            Element eRequest = eRequests.get(i);
            Request req = XMLUtil.getRequestBean(eRequest);
            out.add(req);
        }
        return out;
    }
    
}
