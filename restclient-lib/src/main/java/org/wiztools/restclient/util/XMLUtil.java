package org.wiztools.restclient.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import org.wiztools.restclient.persistence.XMLException;

/**
 *
 * @author subwiz
 */
public final class XMLUtil {
    private XMLUtil() {}
    
    private static final Logger LOG = Logger.getLogger(XMLUtil.class.getName());
    
    public static final String XML_MIME = "application/xml";
    
    public static String getDocumentCharset(final File f)
            throws IOException, XMLException {
        XMLEventReader reader = null;
        try {
            // using stax to get xml factory objects and read the input file
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            reader = inputFactory.createXMLEventReader(new FileInputStream(f));
            XMLEvent event = reader.nextEvent();
            // Always the first element is StartDocument
            // even if the XML does not have explicit declaration:
            StartDocument document = (StartDocument) event;
            return document.getCharacterEncodingScheme();
        }
        catch (XMLStreamException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
        finally{
            if(reader != null){
                try{
                    reader.close();
                }
                catch(XMLStreamException ex){
                    LOG.warning(ex.getMessage());
                }
            }
        }
    }
    
    // Deprecation reason:
    // http://code.google.com/p/rest-client/issues/detail?id=158
    @Deprecated
    public static String indentXML(final String in)
            throws XMLException, IOException {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(in, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Serializer serializer = new Serializer(baos);
            serializer.setIndent(4);
            serializer.setMaxLength(69);
            serializer.write(doc);
            return new String(baos.toByteArray());
        } catch (ParsingException ex) {
            // LOG.log(Level.SEVERE, null, ex);
            throw new XMLException("XML indentation failed.", ex);
        }
    }
}
