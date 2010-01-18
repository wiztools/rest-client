package org.wiztools.restclient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.ParsingException;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;
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
        "2.0", "2.1", "2.2a1", "2.2a2", "2.2", "2.3b1", "2.3", RCConstants.VERSION
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
            Element reqChildSubElement = null;
            Element reqChildSubSubElement = null;

            // HTTP Version
            reqChildSubElement = new Element("http-version");
            reqChildSubElement.appendChild(bean.getHttpVersion().versionNumber());
            reqChildElement.appendChild(reqChildSubElement);

            // Redirect
            reqChildSubElement = new Element("auto-redirect");
            reqChildSubElement.appendChild(Boolean.toString(bean.isAutoRedirect()));
            reqChildElement.appendChild(reqChildSubElement);

            // creating the URL child element 
            reqChildSubElement = new Element("URL");
            reqChildSubElement.appendChild(bean.getUrl().toString());
            reqChildElement.appendChild(reqChildSubElement);

            // creating the method child element
            reqChildSubElement = new Element("method");
            reqChildSubElement.appendChild(bean.getMethod().name());
            reqChildElement.appendChild(reqChildSubElement);

            // creating the auth-methods child element
            List<HTTPAuthMethod> authMethods = bean.getAuthMethods();
            if (authMethods == null || authMethods.size() > 0) {

                reqChildSubElement = new Element("auth-methods");
                String methods = "";
                for (HTTPAuthMethod authMethod : authMethods) {
                    methods = methods + authMethod + ",";
                }
                String authenticationMethod = methods.substring(0, methods.length() == 0 ? 0 : methods.length() - 1);
                reqChildSubElement.appendChild(authenticationMethod);
                reqChildElement.appendChild(reqChildSubElement);

                // creating the auth-preemptive child element
                boolean authPreemptive = bean.isAuthPreemptive();

                reqChildSubElement = new Element("auth-preemptive");
                reqChildSubElement.appendChild(new Boolean(authPreemptive).toString());
                reqChildElement.appendChild(reqChildSubElement);

                // creating the auth-host child element
                String authHost = bean.getAuthHost();
                if (!StringUtil.isStrEmpty(authHost)) {
                    reqChildSubElement = new Element("auth-host");
                    reqChildSubElement.appendChild(authHost);
                    reqChildElement.appendChild(reqChildSubElement);
                }
                // creating the auth-realm child element
                String authRealm = bean.getAuthRealm();
                if (!StringUtil.isStrEmpty(authRealm)) {
                    reqChildSubElement = new Element("auth-realm");
                    reqChildSubElement.appendChild(authRealm);
                    reqChildElement.appendChild(reqChildSubElement);
                }
                // creating the auth-username child element
                String authUsername = bean.getAuthUsername();
                if (!StringUtil.isStrEmpty(authUsername)) {
                    reqChildSubElement = new Element("auth-username");
                    reqChildSubElement.appendChild(authUsername);
                    reqChildElement.appendChild(reqChildSubElement);
                }
                // creating the auth-password child element
                String authPassword = null;
                if (bean.getAuthPassword() != null) {
                    authPassword = new String(bean.getAuthPassword());
                    if (!StringUtil.isStrEmpty(authPassword)) {
                        String encPassword = Base64.encodeObject(authPassword);

                        reqChildSubElement = new Element("auth-password");
                        reqChildSubElement.appendChild(encPassword);
                        reqChildElement.appendChild(reqChildSubElement);
                    }
                }
            }

            // Creating SSL elements
            String sslTruststore = bean.getSslTrustStore();
            if (!StringUtil.isStrEmpty(sslTruststore)) {
                // 1. Create truststore entry
                reqChildSubElement = new Element("ssl-truststore");
                reqChildSubElement.appendChild(sslTruststore);
                reqChildElement.appendChild(reqChildSubElement);

                // 2. Create password entry
                String sslPassword = new String(bean.getSslTrustStorePassword());
                String encPassword = Base64.encodeObject(sslPassword);
                reqChildSubElement = new Element("ssl-truststore-password");
                reqChildSubElement.appendChild(encPassword);
                reqChildElement.appendChild(reqChildSubElement);

                // 3. Create Hostname Verifier entry
                String sslHostnameVerifier = bean.getSslHostNameVerifier().name();
                reqChildSubElement = new Element("ssl-hostname-verifier");
                reqChildSubElement.appendChild(sslHostnameVerifier);
                reqChildElement.appendChild(reqChildSubElement);
            }

            // creating the headers child element
            MultiValueMap<String, String> headers = bean.getHeaders();
            if (!headers.isEmpty()) {
                reqChildSubElement = new Element("headers");
                for (String key : headers.keySet()) {
                    for(String value: headers.get(key)){
                        reqChildSubSubElement = new Element("header");
                        reqChildSubSubElement.addAttribute(new Attribute("key", key));
                        reqChildSubSubElement.addAttribute(new Attribute("value", value));
                        reqChildSubElement.appendChild(reqChildSubSubElement);
                    }
                }
                reqChildElement.appendChild(reqChildSubElement);
            }

            // creating the body child element
            ReqEntity rBean = bean.getBody();
            if (rBean != null) {
                reqChildSubElement = new Element("body");
                String contentType = rBean.getContentType();
                String charSet = rBean.getCharSet();
                byte[] bodyBytes = rBean.getBodyBytes();
                final String body = org.apache.commons.codec.binary.Base64.encodeBase64String(bodyBytes);
                reqChildSubElement.addAttribute(new Attribute("content-type", contentType));
                reqChildSubElement.addAttribute(new Attribute("charset", charSet));
                reqChildSubElement.appendChild(body);
                reqChildElement.appendChild(reqChildSubElement);
            }
            // creating the test-script child element
            String testScript = bean.getTestScript();
            if (testScript != null) {

                reqChildSubElement = new Element("test-script");
                reqChildSubElement.appendChild(testScript);
                reqChildElement.appendChild(reqChildSubElement);
            }
            reqRootElement.appendChild(reqChildElement);

            Document xomDocument = new Document(reqRootElement);

            return xomDocument;
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    private static MultiValueMap<String, String> getHeadersFromHeaderNode(final Element node)
            throws XMLException {
        MultiValueMap<String, String> m = new MultiValueMapArrayList<String, String>();

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

    private static Request xml2Request(final Document doc)
            throws MalformedURLException, XMLException {
        RequestBean requestBean = new RequestBean();

        // get the rootNode
        Element rootNode = doc.getRootElement();

        if (!"rest-client".equals(rootNode.getQualifiedName())) {
            throw new XMLException("Root node is not <rest-client>");
        }

        // checking correct rest version
        checkIfVersionValid(rootNode.getAttributeValue("version"));

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
            else if("auto-redirect".equals(nodeName)){
                requestBean.setAutoRedirect(Boolean.valueOf(tNode.getValue()));
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
                String password = (String) Base64.decodeToObject(tNode.getValue());
                requestBean.setAuthPassword(password.toCharArray());
            }
            else if ("ssl-truststore".equals(nodeName)) {
                String sslTrustStore = tNode.getValue();
                requestBean.setSslTrustStore(sslTrustStore);
            }
            else if ("ssl-truststore-password".equals(nodeName)) {
                String sslTrustStorePassword = (String) Base64.decodeToObject(tNode.getValue());
                requestBean.setSslTrustStorePassword(sslTrustStorePassword.toCharArray());
            }
            else if("ssl-hostname-verifier".equals(nodeName)){
                String sslHostnameVerifierStr = tNode.getValue();
                SSLHostnameVerifier sslHostnameVerifier = SSLHostnameVerifier.valueOf(sslHostnameVerifierStr);
                requestBean.setSslHostNameVerifier(sslHostnameVerifier);
            }
            else if ("headers".equals(nodeName)) {
                MultiValueMap<String, String> m = getHeadersFromHeaderNode(tNode);
                for (String key : m.keySet()) {
                    for(String value: m.get(key)){
                        requestBean.addHeader(key, value);
                    }
                }
            }
            else if ("body".equals(nodeName)) {
                final String contentType = tNode.getAttributeValue("content-type");
                final String charset = tNode.getAttributeValue("charset");
                byte[] body = org.apache.commons.codec.binary.Base64.decodeBase64(tNode.getValue());
                requestBean.setBody(new ReqEntityBean(body,
                        contentType,
                        charset));
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
                    for(String value: headers.get(key)){
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

            final byte[] responseBodyBytes = bean.getResponseBodyBytes();
            if (responseBodyBytes != null) {
                //creating the body child element and append to response child element
                respChildSubElement = new Element("body");
                String responseBody = org.apache.commons.codec.binary.Base64.encodeBase64String(responseBodyBytes);
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
                    List<TestFailureResult> l = testResult.getFailures();
                    for (TestFailureResult b : l) {
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
                    List<TestFailureResult> l = testResult.getErrors();
                    for (TestFailureResult b : l) {
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
                MultiValueMap<String, String> m = getHeadersFromHeaderNode(tNode);
                for (String key : m.keySet()) {
                    for(String value: m.get(key)){
                        responseBean.addHeader(key, value);
                    }
                }
            } else if ("body".equals(nodeName)) {
                byte[] body = org.apache.commons.codec.binary.Base64.decodeBase64(tNode.getValue());
                responseBean.setResponseBodyBytes(body);
            } else if ("test-result".equals(nodeName)) {
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
