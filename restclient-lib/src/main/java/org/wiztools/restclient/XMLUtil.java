package org.wiztools.restclient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import nu.xom.*;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StringUtil;

/**
 *
 * @author rsubramanian
 */
public final class XMLUtil {

    private XMLUtil() {
    }
    private static final Logger LOG = Logger.getLogger(XMLUtil.class.getName());
    private static final String[] VERSIONS = new String[]{
        "2.0", "2.1", "2.2a1", "2.2a2", "2.2", "2.3b1", "2.3", "2.3.1", "2.3.2",
        "2.3.3", "2.4", RCConstants.VERSION
    };
    private static final List<String> LEGACY_BASE64_VERSIONS = Arrays.asList(new String[]{
        "2.0", "2.1", "2.2a1", "2.2a2", "2.2", "2.3b1", "2.3", "2.3.1", "2.3.2",
        "2.3.3", "2.4"
    });
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

            { // HTTP Follow Redirect
                Element e = new Element("http-follow-redirects");
                e.appendChild(String.valueOf(bean.isFollowRedirect()));
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

            { // creating the auth-methods child element
                List<HTTPAuthMethod> authMethods = bean.getAuthMethods();
                if (authMethods == null || authMethods.size() > 0) {

                    { // auth-methods
                        Element e = new Element("auth-methods");
                        String methods = "";
                        for (HTTPAuthMethod authMethod : authMethods) {
                            methods = methods + authMethod + ",";
                        }
                        String authenticationMethod = methods.substring(0, methods.length() == 0 ? 0 : methods.length() - 1);
                        e.appendChild(authenticationMethod);
                        reqChildElement.appendChild(e);
                    }

                    { // creating the auth-preemptive child element
                        Element e = new Element("auth-preemptive");
                        e.appendChild(String.valueOf(bean.isAuthPreemptive()));
                        reqChildElement.appendChild(e);
                    }

                    // creating the auth-host child element
                    String authHost = bean.getAuthHost();
                    if (StringUtil.isNotEmpty(authHost)) {
                        Element e = new Element("auth-host");
                        e.appendChild(authHost);
                        reqChildElement.appendChild(e);
                    }
                    // creating the auth-realm child element
                    String authRealm = bean.getAuthRealm();
                    if (StringUtil.isNotEmpty(authRealm)) {
                        Element e = new Element("auth-realm");
                        e.appendChild(authRealm);
                        reqChildElement.appendChild(e);
                    }
                    // creating the auth-username child element
                    String authUsername = bean.getAuthUsername();
                    if (StringUtil.isNotEmpty(authUsername)) {
                        Element e = new Element("auth-username");
                        e.appendChild(authUsername);
                        reqChildElement.appendChild(e);
                    }
                    // creating the auth-password child element
                    String authPassword = null;
                    if (bean.getAuthPassword() != null) {
                        authPassword = new String(bean.getAuthPassword());
                        if (StringUtil.isNotEmpty(authPassword)) {
                            String encPassword = Util.base64encode(authPassword);

                            Element e = new Element("auth-password");
                            e.appendChild(encPassword);
                            reqChildElement.appendChild(e);
                        }
                    }
                    // creating auth-token child element
                    String authToken = bean.getAuthToken();
                    if(StringUtil.isNotEmpty(authToken)) {
                        Element e = new Element("auth-token");
                        e.appendChild(authToken);
                        reqChildElement.appendChild(e);
                    }
                }
            }
            
            // Creating SSL elements
            String sslTruststore = bean.getSslTrustStore();
            if (StringUtil.isNotEmpty(sslTruststore)) {
                { // 1. Create trust-store entry
                    Element e = new Element("ssl-truststore");
                    e.appendChild(sslTruststore);
                    reqChildElement.appendChild(e);
                }
                { // 2. Create password entry
                    String sslPassword = new String(bean.getSslTrustStorePassword());
                    String encPassword = Util.base64encode(sslPassword);
                    Element e = new Element("ssl-truststore-password");
                    e.appendChild(encPassword);
                    reqChildElement.appendChild(e);
                }
            }
            
            String sslKeystore = bean.getSslKeyStore();
            if(StringUtil.isNotEmpty(sslKeystore)) {
            	{ // 1. Create keystore entry
            		Element e = new Element("ssl-keystore");
            		e.appendChild(sslKeystore);
            		reqChildElement.appendChild(e);
            	}
            	
            	{ // 2. Create password entry
            		String sslPassword = new String(bean.getSslKeyStorePassword());
            		String encPassword = Util.base64encode(sslPassword);
            		Element e = new Element("ssl-keystore-password");
            		e.appendChild(encPassword);
            		reqChildElement.appendChild(e);
            	}
            }
            { // Create Hostname Verifier entry
                String sslHostnameVerifier = bean.getSslHostNameVerifier().name();
                Element e = new Element("ssl-hostname-verifier");
                e.appendChild(sslHostnameVerifier);
                reqChildElement.appendChild(e);
            }   
            { // Create Trust Self-signed cert entry:
                if(bean.isSslTrustSelfSignedCert()) {
                    Element e = new Element("ssl-trust-self-signed-cert");
                    reqChildElement.appendChild(e);
                }
            }

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

            // creating the body child element
            ReqEntity rBean = bean.getBody();
            if (rBean != null) {
                String contentType = rBean.getContentType();
                String charSet = rBean.getCharSet();
                String body = rBean.getBody();
                Element e = new Element("body");
                e.addAttribute(new Attribute("content-type", contentType));
                e.addAttribute(new Attribute("charset", charSet));
                e.appendChild(body);
                reqChildElement.appendChild(e);
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
    
    
    
    private static String base64decode(String rcVersion, String base64Str) {
        if(LEGACY_BASE64_VERSIONS.contains(rcVersion)) {
            return (String) LegacyBase64.decodeToObject(base64Str);
        }
        else {
            return Util.base64decode(base64Str);
        }
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
                requestBean.setFollwoRedirect(Boolean.valueOf(tNode.getValue()));
            }
            else if ("URL".equals(nodeName)) {
                URL url = new URL(tNode.getValue());
                requestBean.setUrl(url);
            }
            else if ("method".equals(nodeName)) {
                requestBean.setMethod(HTTPMethod.get(tNode.getValue()));
            }
            else if ("auth-methods".equals(nodeName)) {
                String[] authenticationMethods = tNode.getValue().split(",");
                for (int j = 0; j < authenticationMethods.length; j++) {
                    requestBean.addAuthMethod(HTTPAuthMethod.get(authenticationMethods[j]));
                }
            }
            else if ("auth-preemptive".equals(nodeName)) {
                if (tNode.getValue().equals("true")) {
                    requestBean.setAuthPreemptive(true);
                } else {
                    requestBean.setAuthPreemptive(false);
                }
            }
            else if ("auth-host".equals(nodeName)) {
                requestBean.setAuthHost(tNode.getValue());
            }
            else if ("auth-realm".equals(nodeName)) {
                requestBean.setAuthRealm(tNode.getValue());
            }
            else if ("auth-username".equals(nodeName)) {
                requestBean.setAuthUsername(tNode.getValue());
            }
            else if ("auth-password".equals(nodeName)) {
                String password = base64decode(rcVersion, tNode.getValue());
                requestBean.setAuthPassword(password.toCharArray());
            }
            else if("auth-token".equals(nodeName)) {
                requestBean.setAuthToken(tNode.getValue());
            }
            else if ("ssl-truststore".equals(nodeName)) {
                String sslTrustStore = tNode.getValue();
                requestBean.setSslTrustStore(sslTrustStore);
            }
            else if ("ssl-truststore-password".equals(nodeName)) {
                String sslTrustStorePassword = base64decode(rcVersion, tNode.getValue());
                requestBean.setSslTrustStorePassword(sslTrustStorePassword.toCharArray());
            }
            else if("ssl-hostname-verifier".equals(nodeName)){
                String sslHostnameVerifierStr = tNode.getValue();
                SSLHostnameVerifier sslHostnameVerifier = SSLHostnameVerifier.valueOf(sslHostnameVerifierStr);
                requestBean.setSslHostNameVerifier(sslHostnameVerifier);
            }
            else if("ssl-trust-self-signed-cert".equals(nodeName)) {
                requestBean.setSslTrustSelfSignedCert(true);
            }
            else if ("ssl-keystore".equals(nodeName)) {
                String sslKeyStore = tNode.getValue();
                requestBean.setSslKeyStore(sslKeyStore);
            }
            else if ("ssl-keystore-password".equals(nodeName)) {
                String sslKeyStorePassword = base64decode(rcVersion, tNode.getValue());
                requestBean.setSslKeyStorePassword(sslKeyStorePassword.toCharArray());
            }
            else if ("headers".equals(nodeName)) {
                Map<String, String> m = getHeadersFromHeaderNode(tNode);
                for (String key : m.keySet()) {
                    requestBean.addHeader(key, m.get(key));
                }
            }
            else if ("body".equals(nodeName)) {
                requestBean.setBody(new ReqEntityBean(tNode.getValue(), tNode.getAttributeValue("content-type"),
                        tNode.getAttributeValue("charset")));
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

            String responseBody = bean.getResponseBody();
            if (responseBody != null) {
                //creating the body child element and append to response child element
                respChildSubElement = new Element("body");
                respChildSubElement.appendChild(responseBody);
                respChildElement.appendChild(respChildSubElement);
            }
            // test result 
            TestResult testResult = bean.getTestResult();
            if (testResult != null) {
                //creating the test-result child element
                respChildSubElement = new Element("test-result");

                // Counts:
                Element e_runCount = new Element("run-coun");
                e_runCount.appendChild(String.valueOf(testResult.getRunCount()));
                Element e_failureCount = new Element("failure-coun");
                e_failureCount.appendChild(String.valueOf(testResult.getFailureCount()));
                Element e_errorCount = new Element("error-coun");
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
                responseBean.setResponseBody(tNode.getValue());
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
