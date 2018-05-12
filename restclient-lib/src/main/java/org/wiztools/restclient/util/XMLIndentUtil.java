package org.wiztools.restclient.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.wiztools.jxmlfmt.XMLFmt;
import org.wiztools.restclient.persistence.XMLException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author subwiz
 */
public final class XMLIndentUtil {
    private XMLIndentUtil() {
    }

    public static String getIndented(String inXml) throws IOException {
        return XMLFmt.fmt(inXml, 4);
    }

}
