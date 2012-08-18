package org.wiztools.restclient.util;

import java.io.*;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import nu.xom.*;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.restclient.RCConstants;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.bean.*;

/**
 *
 * @author rsubramanian
 */
public final class XMLUtil {

    private XMLUtil() {
    }
    private static final Logger LOG = Logger.getLogger(XMLUtil.class.getName());
    private static final String[] VERSIONS = new String[]{
        RCConstants.VERSION
    };

    public static final String XML_MIME = "application/xml";
    
    static {
        // Sort the version array for binary search
        Arrays.sort(VERSIONS);
    }

    private static void checkIfVersionValid(final String restVersion)
            throws XMLException {
        if (restVersion == null) {
            throw new XMLException("Attribute `version' not available for root element <rest-client>");
        }
        int res = Arrays.binarySearch(VERSIONS, restVersion);
        if (res == -1) {
            throw new XMLException("Version not supported");
        }
    }

    private static Document request2XML(final Request bean)
            throws XMLException {
        try {

            Element reqRootElement = new Element("rest-client");
            // set version attributes to rest-client root tag
            Attribute versionAttributes = new Attribute("version", RCConstants.VERSION);
            reqRootElement.addAttribute(versionAttributes);

            Element reqChildElement = new Element("request");

            { // HTTP Version
                Element e = new Element("http-version");
                e.appendChild(bean.getHttpVersion().versionNumber());
                reqChildElement.appendChild(e);
            }

            if(bean.isFollowRedirect()) { // HTTP Follow Redirect
                Element e = new Element("http-follow-redirects");
                reqChildElement.appendChild(e);
            }
            
            if(bean.isIgnoreResponseBody()) { // Response body ignored
                Element e = new Element("ignore-response-body");
                reqChildElement.appendChild(e);
            }

            { // creating the URL child element
                Element e = new Element("URL");
                e.appendChild(bean.getUrl().toString());
                reqChildElement.appendChild(e);
            }

            { // creating the method child element
                Element e = new Element("method");
                e.appendChild(bean.getMethod().name());
                reqChildElement.appendChild(e);
            }
            
            { // auth
                Auth auth = bean.getAuth();
                if(auth != null) {
                    Element eAuth = XmlAuthUtil.getAuthElement(auth);
                    reqChildElement.appendChild(eAuth);
                }
            }
            
            // Creating SSL elements
            Element eSsl = XmlSslUtil.getSslReq(bean.getSslReq());
            reqChildElement.appendChild(eSsl);

            // creating the headers child element
            MultiValueMap<String, String> headers = bean.getHeaders();
            if (!headers.isEmpty()) {
                Element e = new Element("headers");
                for (String key : headers.keySet()) {
                    for(String value: headers.get(key)) {
                        Element ee = new Element("header");
                        ee.addAttribute(new Attribute("key", key));
                        ee.addAttribute(new Attribute("value", value));
                        e.appendChild(ee);
                    }
                }
                reqChildElement.appendChild(e);
            }
            
            // Cookies
            List<HttpCookie> cookies = bean.getCookies();
            if(!cookies.isEmpty()) {
                Element e = new Element("cookies");
                for(HttpCookie cookie: cookies) {
                    Element ee = new Element("cookie");
                    ee.addAttribute(new Attribute("name", cookie.getName()));
                    ee.addAttribute(new Attribute("value", cookie.getValue()));
                    e.appendChild(ee);
                }
                reqChildElement.appendChild(e);
            }

            { // creating the body child element
                ReqEntity entityBean = bean.getBody();
                if(entityBean != null) {
                    Element e = XmlBodyUtil.getReqEntity(entityBean);
                    reqChildElement.appendChild(e);
                }
            }
            
            // creating the test-script child element
            String testScript = bean.getTestScript();
            if (testScript != null) {

                Element e = new Element("test-script");
                e.appendChild(testScript);
                reqChildElement.appendChild(e);
            }
            reqRootElement.appendChild(reqChildElement);

            Document xomDocument = new Document(reqRootElement);

            return xomDocument;
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    private static Map<String, String> getHeadersFromHeaderNode(final Element node)
            throws XMLException {
        Map<String, String> m = new LinkedHashMap<String, String>();

        for (int i = 0; i < node.getChildElements().size(); i++) {
            Element headerElement = node.getChildElements().get(i);
            if (!"header".equals(headerElement.getQualifiedName())) {
                throw new XMLException("<headers> element should contain only <header> elements");
            }

            m.put(headerElement.getAttributeValue("key"),
                    headerElement.getAttributeValue("value"));

        }
        return m;
    }
    
    private static List<HttpCookie> getCookiesFromCookiesNode(final Element node) 
            throws XMLException {
        List<HttpCookie> out = new ArrayList<HttpCookie>();
        
        for (int i = 0; i < node.getChildElements().size(); i++) {
            Element e = node.getChildElements().get(i);
            if(!"cookie".equals(e.getQualifiedName())) {
                throw new XMLException("<cookies> element should contain only <cookie> elements");
            }
            
            HttpCookie cookie = new HttpCookie(e.getAttributeValue("name"),
                    e.getAttributeValue("value"));
            out.add(cookie);
        }
        
        return out;
    }

    private static Request xml2Request(final Document doc)
            throws MalformedURLException, XMLException {
        RequestBean requestBean = new RequestBean();

        // get the rootNode
        Element rootNode = doc.getRootElement();

        if (!"rest-client".equals(rootNode.getQualifiedName())) {
            throw new XMLException("Root node is not <rest-client>");
        }

        // checking correct rest version
        final String rcVersion = rootNode.getAttributeValue("version");
        checkIfVersionValid(rcVersion);

        // assign rootnode to current node and also finding 'request' node
        Element tNode = null;
        Element requestNode = null;

        // if more than two request element is present then throw the exception 
        if (rootNode.getChildElements().size() != 1) {
            throw new XMLException("There can be only one child node for root node: <request>");
        }
        // minimum one request element is present in xml 
        if (rootNode.getFirstChildElement("request") == null) {
            throw new XMLException("The child node of <rest-client> should be <request>");
        }
        requestNode = rootNode.getFirstChildElement("request");
        for (int i = 0; i < requestNode.getChildElements().size(); i++) {
            tNode = requestNode.getChildElements().get(i);
            String nodeName = tNode.getQualifiedName();
            if ("http-version".equals(nodeName)) {
                String t = tNode.getValue();
                HTTPVersion httpVersion = "1.1".equals(t) ? HTTPVersion.HTTP_1_1 : HTTPVersion.HTTP_1_0;
                requestBean.setHttpVersion(httpVersion);
            }
            else if("http-follow-redirects".equals(nodeName)) {
                requestBean.setFollowRedirect(true);
            }
            else if("ignore-response-body".equals(nodeName)) {
                requestBean.setIgnoreResponseBody(true);
            }
            else if ("URL".equals(nodeName)) {
                URL url = new URL(tNode.getValue());
                requestBean.setUrl(url);
            }
            else if ("method".equals(nodeName)) {
                requestBean.setMethod(HTTPMethod.get(tNode.getValue()));
            }
            else if("auth".equals(nodeName)) {
                requestBean.setAuth(XmlAuthUtil.getAuth(tNode));
            }
            else if("ssl".equals(nodeName)) {
                requestBean.setSslReq(XmlSslUtil.getSslReq(tNode));
            }
            else if ("headers".equals(nodeName)) {
                Map<String, String> m = getHeadersFromHeaderNode(tNode);
                for (String key : m.keySet()) {
                    requestBean.addHeader(key, m.get(key));
                }
            }
            else if ("cookies".equals(nodeName)) {
                List<HttpCookie> cookies = getCookiesFromCookiesNode(tNode);
                for (HttpCookie cookie: cookies) {
                    requestBean.addCookie(cookie);
                }
            }
            else if ("body".equals(nodeName)) {
                ReqEntity body = XmlBodyUtil.getReqEntity(tNode);
                requestBean.setBody(body);
            }
            else if ("test-script".equals(nodeName)) {
                requestBean.setTestScript(tNode.getValue());
            }
            else {
                throw new XMLException("Invalid element encountered: <" + nodeName + ">");
            }
        }
        return requestBean;
    }

    private static Document response2XML(final Response bean)
            throws XMLException {

        try {

            Element respRootElement = new Element("rest-client");
            Element respChildElement = new Element("response");
            Element respChildSubElement = null;
            Element respChildSubSubElement = null;

            // set version attributes to rest-client root tag
            Attribute versionAttributes = new Attribute("version", RCConstants.VERSION);
            respRootElement.addAttribute(versionAttributes);

            // adding first sub child element - execution-time and append to response child element
            respChildSubElement = new Element("execution-time");
            respChildSubElement.appendChild(String.valueOf(bean.getExecutionTime()));
            respChildElement.appendChild(respChildSubElement);

            // adding second sub child element - status and code attributes and append to response child element
            respChildSubElement = new Element("status");
            Attribute codeAttributes = new Attribute("code", String.valueOf(bean.getStatusCode()));
            respChildSubElement.addAttribute(codeAttributes);
            respChildSubElement.appendChild(bean.getStatusLine());
            respChildElement.appendChild(respChildSubElement);

            // adding third sub child element - headers
            MultiValueMap<String, String> headers = bean.getHeaders();
            if (!headers.isEmpty()) {
                Attribute keyAttribute = null;
                Attribute valueAttribute = null;
                // creating sub child-child element 
                respChildSubElement = new Element("headers");
                for (String key : headers.keySet()) {
                    for(String value: headers.get(key)) {
                        respChildSubSubElement = new Element("header");
                        keyAttribute = new Attribute("key", key);
                        valueAttribute = new Attribute("value", value);
                        respChildSubSubElement.addAttribute(keyAttribute);
                        respChildSubSubElement.addAttribute(valueAttribute);
                        respChildSubElement.appendChild(respChildSubSubElement);
                    }
                }
                // add response child element - headers
                respChildElement.appendChild(respChildSubElement);
            }

            byte[] responseBody = bean.getResponseBody();
            if (responseBody != null) {
                //creating the body child element and append to response child element
                respChildSubElement = new Element("body");
                final String base64encodedBody = Util.base64encode(responseBody);
                respChildSubElement.appendChild(base64encodedBody);
                respChildElement.appendChild(respChildSubElement);
            }
            // test result 
            TestResult testResult = bean.getTestResult();
            if (testResult != null) {
                //creating the test-result child element
                respChildSubElement = new Element("test-result");

                // Counts:
                Element e_runCount = new Element("run-count");
                e_runCount.appendChild(String.valueOf(testResult.getRunCount()));
                Element e_failureCount = new Element("failure-count");
                e_failureCount.appendChild(String.valueOf(testResult.getFailureCount()));
                Element e_errorCount = new Element("error-count");
                e_errorCount.appendChild(String.valueOf(testResult.getErrorCount()));
                respChildSubElement.appendChild(e_runCount);
                respChildSubElement.appendChild(e_failureCount);
                respChildSubElement.appendChild(e_errorCount);

                // Failures
                if (testResult.getFailureCount() > 0) {
                    Element e_failures = new Element("failures");
                    List<TestExceptionResult> l = testResult.getFailures();
                    for (TestExceptionResult b : l) {
                        Element e_message = new Element("message");
                        e_message.appendChild(b.getExceptionMessage());
                        Element e_line = new Element("line-number");
                        e_line.appendChild(String.valueOf(b.getLineNumber()));
                        Element e_failure = new Element("failure");
                        e_failure.appendChild(e_message);
                        e_failure.appendChild(e_line);
                        e_failures.appendChild(e_failure);
                    }
                    respChildSubElement.appendChild(e_failures);
                }

                //Errors
                if (testResult.getErrorCount() > 0) {
                    Element e_errors = new Element("errors");
                    List<TestExceptionResult> l = testResult.getErrors();
                    for (TestExceptionResult b : l) {
                        Element e_message = new Element("message");
                        e_message.appendChild(b.getExceptionMessage());
                        Element e_line = new Element("line-number");
                        e_line.appendChild(String.valueOf(b.getLineNumber()));
                        Element e_error = new Element("error");
                        e_error.appendChild(e_message);
                        e_error.appendChild(e_line);
                        e_errors.appendChild(e_error);
                    }
                    respChildSubElement.appendChild(e_errors);
                }
                // Trace
                Element e_trace = new Element("trace");
                e_trace.appendChild(testResult.toString());
                respChildSubElement.appendChild(e_trace);

                respChildElement.appendChild(respChildSubElement);
            }

            respRootElement.appendChild(respChildElement);

            Document xomDocument = new Document(respRootElement);

            return xomDocument;

        } catch (Exception ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    private static Response xml2Response(final Document doc)
            throws XMLException {
        ResponseBean responseBean = new ResponseBean();

        // get the rootNode
        Element rootNode = doc.getRootElement();

        if (!"rest-client".equals(rootNode.getQualifiedName())) {
            throw new XMLException("Root node is not <rest-client>");
        }

        // checking correct rest version
        checkIfVersionValid(rootNode.getAttributeValue("version"));

        // assign rootnode to current node and also finding 'response' node
        Element tNode = null;
        Element responseNode = null;

        // if more than two request element is present then throw the exception
        if (rootNode.getChildElements().size() != 1) {
            throw new XMLException("There can be only one child node for root node: <response>");
        }
        // minimum one response element is present in xml
        if (rootNode.getFirstChildElement("response") == null) {
            throw new XMLException("The child node of <rest-client> should be <response>");
        }
        responseNode = rootNode.getFirstChildElement("response");
        for (int i = 0; i < responseNode.getChildElements().size(); i++) {
            tNode = responseNode.getChildElements().get(i);
            String nodeName = tNode.getQualifiedName();

            if ("execution-time".equals(nodeName)) {
                responseBean.setExecutionTime(Long.parseLong(tNode.getValue()));
            } else if ("status".equals(nodeName)) {
                responseBean.setStatusLine(tNode.getValue());
                responseBean.setStatusCode(Integer.parseInt(tNode.getAttributeValue("code")));
            } else if ("headers".equals(nodeName)) {
                Map<String, String> m = getHeadersFromHeaderNode(tNode);
                for (String key : m.keySet()) {
                    responseBean.addHeader(key, m.get(key));
                }
            } else if ("body".equals(nodeName)) {
                final String base64body = tNode.getValue();
                responseBean.setResponseBody(Util.base64decodeByteArray(base64body));
            } else if ("test-result".equals(nodeName)) {
                //responseBean.setTestResult(node.getTextContent()); TODO
                TestResultBean testResultBean = new TestResultBean();

                for (int j = 0; j < tNode.getChildCount(); j++) {
                    String nn = tNode.getQualifiedName();
                    if ("run-count".equals(nn)) {
                        throw new XMLException("<headers> element should contain only <header> elements");
                    } else if ("failure-count".equals(nn)) {
                        throw new XMLException("<headers> element should contain only <header> elements");
                    } else if ("error-count".equals(nn)) {
                        throw new XMLException("<headers> element should contain only <header> elements");
                    } else if ("failures".equals(nn)) {
                        throw new XMLException("<headers> element should contain only <header> elements");
                    } else if ("errors".equals(nn)) {
                        throw new XMLException("<headers> element should contain only <header> elements");
                    }
                }
                responseBean.setTestResult(testResultBean);
            } else {
                throw new XMLException("Unrecognized element found: <" + nodeName + ">");
            }
        }
        return responseBean;
    }

    private static void writeXML(final Document doc, final File f)
            throws IOException, XMLException {

        try {
            OutputStream out = new FileOutputStream(f);
            out = new BufferedOutputStream(out);
            // getDocumentCharset(f) - to retrieve the charset encoding attribute
            Serializer serializer = new Serializer(out, getDocumentCharset(f));
            serializer.write(doc);
            out.close();
        } catch (IOException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    private static Document getDocumentFromFile(final File f)
            throws IOException, XMLException {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(f);
            return doc;
        } catch (ParsingException ex) {
            throw new XMLException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }

    }

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

    public static void writeRequestXML(final Request bean, final File f)
            throws IOException, XMLException {
        Document doc = request2XML(bean);
        writeXML(doc, f);
    }

    public static void writeResponseXML(final Response bean, final File f)
            throws IOException, XMLException {
        Document doc = response2XML(bean);
        writeXML(doc, f);
    }

    public static Request getRequestFromXMLFile(final File f)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        return xml2Request(doc);
    }

    public static Response getResponseFromXMLFile(final File f)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        return xml2Response(doc);
    }

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
