package org.wiztools.restclient.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.wiztools.restclient.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author rsubramanian
 */
public final class XMLUtil {
    
    private static final Logger LOG = Logger.getLogger(XMLUtil.class.getName());
    
    private static final String[] VERSIONS = new String[]{"2.0", "2.1", "2.2a1", RCConstants.VERSION};
    
    public static final String XML_MIME = "application/xml";
    public static final String XML_DEFAULT_ENCODING = "UTF-8";
    
    static{
        // Sort the version array for binary search
        Arrays.sort(VERSIONS);
    }
    
    private static void checkIfVersionValid(final Node versionNode) throws XMLException{
        if(versionNode == null){
            throw new XMLException("Attribute `version' not available for root element <rest-client>");
        }
        int res = Arrays.binarySearch(VERSIONS, versionNode.getNodeValue());
        if(res == -1){
            throw new XMLException("Version not supported");                
        }
    }

    public static Document request2XML(final RequestBean bean)
            throws XMLException {
        try{
            Document xmldoc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Element e = null;
            Element request = null;
            Node n = null;
            Element subChild = null;

            xmldoc = impl.createDocument(null, "rest-client", null);
            Element root = xmldoc.getDocumentElement();
            root.setAttributeNS(null, "version", RCConstants.VERSION);

            request = xmldoc.createElementNS(null, "request");
            
            // HTTP version
            e = xmldoc.createElementNS(null, "http-version");
            n = xmldoc.createTextNode(bean.getHttpVersion().versionNumber());
            e.appendChild(n);
            request.appendChild(e);
            
            // creating the URL child element
            e = xmldoc.createElementNS(null, "URL");
            n = xmldoc.createTextNode(bean.getUrl().toString());
            e.appendChild(n);
            request.appendChild(e);

            // creating the method child element
            e = xmldoc.createElementNS(null, "method");
            n = xmldoc.createTextNode(bean.getMethod());
            e.appendChild(n);
            request.appendChild(e);

            // creating the auth-methods child element
            List<String> authMethods = bean.getAuthMethods();
            if(authMethods.size() > 0){
                if (authMethods != null && authMethods.size() > 0) {
                    e = xmldoc.createElementNS(null, "auth-methods");
                    String methods = "";
                    for (String authMethod : authMethods) {
                        methods = methods + authMethod + ",";
                    }
                    String authenticationMethod = methods.substring(0, methods.length()==0?0:methods.length()-1);
                    n = xmldoc.createTextNode(authenticationMethod);
                    e.appendChild(n);
                    request.appendChild(e);
                }


                // creating the auth-preemptive child element
                Boolean authPreemptive = bean.isAuthPreemptive();
                if (authPreemptive != null) {
                    e = xmldoc.createElementNS(null, "auth-preemptive");
                    n = xmldoc.createTextNode(authPreemptive.toString());
                    e.appendChild(n);
                    request.appendChild(e);
                }

                // creating the auth-host child element
                String authHost = bean.getAuthHost();
                if (authHost != null) {
                    e = xmldoc.createElementNS(null, "auth-host");
                    n = xmldoc.createTextNode(authHost);
                    e.appendChild(n);
                    request.appendChild(e);
                }

                // creating the auth-realm child element
                String authRealm = bean.getAuthRealm();
                if (authRealm != null) {
                    e = xmldoc.createElementNS(null, "auth-realm");
                    n = xmldoc.createTextNode(authRealm);
                    e.appendChild(n);
                    request.appendChild(e);
                }

                // creating the auth-username child element
                String authUsername = bean.getAuthUsername();
                if (authUsername != null) {
                    e = xmldoc.createElementNS(null, "auth-username");
                    n = xmldoc.createTextNode(authUsername);
                    e.appendChild(n);
                    request.appendChild(e);
                }

                // creating the auth-password child element
                String authPassword = null;
                if(bean.getAuthPassword() != null){
                    authPassword = new String(bean.getAuthPassword());
                    e = xmldoc.createElementNS(null, "auth-password");
                    String encPassword = Base64.encodeObject(authPassword);
                    n = xmldoc.createTextNode(encPassword);
                    e.appendChild(n);
                    request.appendChild(e);
                }
            }
            
            // Creating SSL elements
            String sslTruststore = bean.getSslTrustStore();
            if(!Util.isStrEmpty(sslTruststore)){
                // 1. Create truststore entry
                e = xmldoc.createElementNS(null, "ssl-truststore");
                n = xmldoc.createTextNode(sslTruststore);
                e.appendChild(n);
                request.appendChild(e);
                
                // 2. Create password entry
                String sslPassword = new String(bean.getSslTrustStorePassword());
                String encPassword = Base64.encodeObject(sslPassword);
                e = xmldoc.createElementNS(null, "ssl-truststore-password");
                n = xmldoc.createTextNode(encPassword);
                e.appendChild(n);
                request.appendChild(e);
            }

            // creating the headers child element
            Map<String, String> headers = bean.getHeaders();
            if (!headers.isEmpty()) {
                e = xmldoc.createElementNS(null, "headers");
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    subChild = xmldoc.createElementNS(null, "header");
                    subChild.setAttributeNS(null, "key", key);
                    subChild.setAttributeNS(null, "value", value);
                    e.appendChild(subChild);
                }
                request.appendChild(e);
            }

            // creating the body child element
            ReqEntityBean rBean = bean.getBody();
            if (rBean != null) {
                e = xmldoc.createElementNS(null, "body");
                String contentType = rBean.getContentType();
                String charSet = rBean.getCharSet();
                String body = rBean.getBody();
                e.setAttributeNS(null, "content-type", contentType);
                e.setAttributeNS(null, "charset", charSet);
                n = xmldoc.createTextNode(body);
                e.appendChild(n);
                request.appendChild(e);
            }
            // creating the test-script child element
            String testScript = bean.getTestScript();
            if (testScript != null) {
                e = xmldoc.createElementNS(null, "test-script");
                n = xmldoc.createTextNode(testScript);
                e.appendChild(n);
                request.appendChild(e);
            }
            
            root.appendChild(request);

            return xmldoc;
        }
        catch(ParserConfigurationException ex){
            throw new XMLException(ex.getMessage(), ex);
        }
    }
    
    private static Map<String, String> getHeadersFromHeaderNode(final Node node) throws XMLException{
        Map<String, String> m = new LinkedHashMap<String, String>();
        
        NodeList llHeader = node.getChildNodes();
        int maxHeader = llHeader.getLength();
        for(int j=0; j<maxHeader; j++){
            Node headerNode = llHeader.item(j);
            if(headerNode.getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
            if(!"header".equals(headerNode.getNodeName())){
                throw new XMLException("<headers> element should contain only <header> elements");
            }
            NamedNodeMap nodeMap = headerNode.getAttributes();
            Node key = nodeMap.getNamedItem("key");
            Node value = nodeMap.getNamedItem("value");
            m.put(key.getNodeValue(), value.getNodeValue());
        }
        
        return m;
    }

    public static RequestBean xml2Request(final Document doc)
            throws MalformedURLException, XMLException {
        RequestBean requestBean = new RequestBean();
        
        // Get the rootNode
        Node rootNode = doc.getFirstChild();
        
        if(!"rest-client".equals(rootNode.getNodeName())){
            throw new XMLException("Root node is not <rest-client>");
        }
        NamedNodeMap nodeMapVerAttr = rootNode.getAttributes();
        Node versionNode = nodeMapVerAttr.getNamedItem("version");
        checkIfVersionValid(versionNode);
        
        // Get the requestNode
        NodeList llRequest = rootNode.getChildNodes();
        int size = llRequest.getLength();
        int reqNodeCount = 0;
        Node requestNode = null;
        for(int i=0; i<size; i++){
            Node tNode = llRequest.item(i);
            if(tNode.getNodeType() == Node.ELEMENT_NODE){
                requestNode = tNode;
                reqNodeCount++;
            }
        }
        if(reqNodeCount != 1){
            throw new XMLException("There can be only one child node for root node: <request>");
        }
        if(!"request".equals(requestNode.getNodeName())){
            throw new XMLException("The child node of <rest-client> should be <request>");
        }
        
        // Process other nodes
        NodeList ll = requestNode.getChildNodes();
        int max = ll.getLength();
        for(int i=0; i<max; i++){
            Node node = ll.item(i);
            String nodeName = node.getNodeName();
            LOG.fine(nodeName + " : " + node.getNodeType());
            if(node.getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
            if("http-version".equals(nodeName)){
                String t = node.getTextContent();
                HTTPVersion httpVersion = "1.1".equals(t)? HTTPVersion.HTTP_1_1: HTTPVersion.HTTP_1_0;
                requestBean.setHttpVersion(httpVersion);
            }
            else if("URL".equals(nodeName)){
                URL url = new URL(node.getTextContent());
                requestBean.setUrl(url);
            }
            else if("method".equals(nodeName)){
                requestBean.setMethod(node.getTextContent());
            }
            else if("auth-methods".equals(nodeName)){
                String[] authenticationMethods = node.getTextContent().split(",");
                for (int j = 0; j < authenticationMethods.length; j++) {
                    requestBean.addAuthMethod(authenticationMethods[j]);
                }
            }
            else if("auth-preemptive".equals(nodeName)){
                if (node.getTextContent().equals("true")) {
                    requestBean.setAuthPreemptive(true);
                }
                else{
                    requestBean.setAuthPreemptive(false);
                }
            }
            else if("auth-host".equals(nodeName)){
                requestBean.setAuthHost(node.getTextContent());
            }
            else if("auth-realm".equals(nodeName)){
                requestBean.setAuthRealm(node.getTextContent());
            }
            else if("auth-username".equals(nodeName)){
                requestBean.setAuthUsername(node.getTextContent());
            }
            else if("auth-password".equals(nodeName)){
                String password = (String) Base64.decodeToObject(node.getTextContent());
                requestBean.setAuthPassword(password.toCharArray());
            }
            else if("ssl-truststore".equals(nodeName)){
                String sslTrustStore = node.getTextContent();
                requestBean.setSslTrustStore(sslTrustStore);
            }
            else if("ssl-truststore-password".equals(nodeName)){
                String sslTrustStorePassword = (String) Base64.decodeToObject(node.getTextContent());
                requestBean.setSslTrustStorePassword(sslTrustStorePassword.toCharArray());
            }
            else if("headers".equals(nodeName)){
                Map<String, String> m = getHeadersFromHeaderNode(node);
                for(String key: m.keySet()){
                    requestBean.addHeader(key, m.get(key));
                }
            }
            else if("body".equals(nodeName)){
                NamedNodeMap nodeMap = node.getAttributes();
                Node contentType = nodeMap.getNamedItem("content-type");
                Node charSet = nodeMap.getNamedItem("charset");
                requestBean.setBody(new ReqEntityBean(node.getTextContent(), contentType.getNodeValue(),
                        charSet.getNodeValue()));
            }
            else if("test-script".equals(nodeName)){
                requestBean.setTestScript(node.getTextContent());
            }
            else{
                throw new XMLException("Invalid element encountered: <" + nodeName + ">");
            }
        }

        return requestBean;
    }

    public static Document response2XML(final ResponseBean bean)
            throws XMLException {

        try{
            Document xmldoc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Element e = null;
            Node n = null;
            Element response = null;
            Element subChild = null;

            xmldoc = impl.createDocument(null, "rest-client", null);
            Element root = xmldoc.getDocumentElement();
            root.setAttributeNS(null, "version", RCConstants.VERSION);

            response = xmldoc.createElementNS(null, "response");
            
            // creating the execution time child element
            e = xmldoc.createElementNS(null, "execution-time");
            n = xmldoc.createTextNode(String.valueOf(bean.getExecutionTime()));
            e.appendChild(n);
            response.appendChild(e);

            // creating the status child element
            e = xmldoc.createElementNS(null, "status");
            e.setAttributeNS(null, "code", String.valueOf(bean.getStatusCode()));
            n = xmldoc.createTextNode(bean.getStatusLine());
            e.appendChild(n);
            response.appendChild(e);

            // creating the headers child element
            Map<String, String> headers = bean.getHeaders();
            if (!headers.isEmpty()) {
                e = xmldoc.createElementNS(null, "headers");
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    subChild = xmldoc.createElementNS(null, "header");
                    subChild.setAttributeNS(null, "key", key);
                    subChild.setAttributeNS(null, "value", value);
                    e.appendChild(subChild);
                }
                response.appendChild(e);
            }

            //creating the body child element
            String responseBody = bean.getResponseBody();
            if (responseBody != null) {
                e = xmldoc.createElementNS(null, "body");
                n = xmldoc.createTextNode(responseBody);
                e.appendChild(n);
                response.appendChild(e);
            }
            
            // test result
            String testResult = bean.getTestResult();
            if(testResult != null){
                e = xmldoc.createElementNS(null, "test-result");
                n = xmldoc.createTextNode(testResult);
                e.appendChild(n);
                response.appendChild(e);
            }

            root.appendChild(response);

            return xmldoc;
        }
        catch(ParserConfigurationException ex){
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    public static ResponseBean xml2Response(final Document doc) throws XMLException {
        ResponseBean responseBean = new ResponseBean();
        
        // Get the rootNode
        Node rootNode = doc.getFirstChild();
        if(!"rest-client".equals(rootNode.getNodeName())){
            throw new XMLException("The root node must be <rest-client>");
        }
        NamedNodeMap nodeMapVerAttr = rootNode.getAttributes();
        Node nodeVersion = nodeMapVerAttr.getNamedItem("version");
        checkIfVersionValid(nodeVersion);
        
        // Get the responseNode
        NodeList llResponse = rootNode.getChildNodes();
        int size = llResponse.getLength();
        int resNodeCount = 0;
        Node responseNode = null;
        for(int i=0; i<size; i++){
            Node tNode = llResponse.item(i);
            if(tNode.getNodeType() == Node.ELEMENT_NODE){
                responseNode = tNode;
                resNodeCount++;
            }
        }
        if(resNodeCount != 1){
            throw new XMLException("There can be only one child node for root node: <response>");
        }
        if(!"response".equals(responseNode.getNodeName())){
            throw new XMLException("The child node of <rest-client> should be <response>");
        }
        
        // Process other nodes
        NodeList ll = responseNode.getChildNodes();
        int max = ll.getLength();
        for(int i=0; i < max; i++){
            Node node = ll.item(i);
            String nodeName = node.getNodeName();
            LOG.fine(nodeName + " : " + node.getNodeType());
            if(node.getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
            if("execution-time".equals(nodeName)){
                responseBean.setExecutionTime(Long.parseLong(node.getTextContent()));
            }
            else if("status".equals(nodeName)){
                responseBean.setStatusLine(node.getTextContent());
                NamedNodeMap nodeMap = node.getAttributes();
                Node n = nodeMap.getNamedItem("code");
                responseBean.setStatusCode(Integer.parseInt(n.getNodeValue()));
            }
            else if("headers".equals(nodeName)){
                Map<String, String> m = getHeadersFromHeaderNode(node);
                for(String key: m.keySet()){
                    responseBean.addHeader(key, m.get(key));
                }
            }
            else if("body".equals(nodeName)){
                responseBean.setResponseBody(node.getTextContent());
            }
            else if("test-result".equals(nodeName)){
                responseBean.setTestResult(node.getTextContent());
            }
            else{
                throw new XMLException("Unrecognized element found: <" + nodeName + ">");
            }
        }

        return responseBean;
    }

    public static void writeXML(final Document doc, final File f)
            throws IOException, XMLException {
        try{
            DOMSource domSource = new DOMSource(doc);
            FileOutputStream out = new FileOutputStream(f);
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.transform(domSource, streamResult);
            out.close();
        }
        catch(TransformerConfigurationException ex){
            throw new XMLException(ex.getMessage(), ex);
        }
        catch(TransformerException ex){
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    public static Document getDocumentFromFile(final File f) throws IOException,
            XMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);
            return doc;
        } catch (ParserConfigurationException ex) {
            throw new XMLException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }
    
    public static String getDocumentCharset(final File f) throws IOException, XMLException{
        Document doc = getDocumentFromFile(f);
        return doc.getXmlEncoding();
    }

    public static void writeRequestXML(final RequestBean bean, final File f)
            throws IOException, XMLException {
        Document doc = request2XML(bean);
        writeXML(doc, f);
    }

    public static void writeResponseXML(final ResponseBean bean, final File f)
            throws IOException, XMLException {
        Document doc = response2XML(bean);
        writeXML(doc, f);
    }

    /*public static void writeXMLRequest(final File f, RequestBean bean)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        bean = xml2Request(doc);
    }*/

    /*public static void writeXMLResponse(final File f, ResponseBean bean)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        bean = xml2Response(doc);
    }*/

    public static RequestBean getRequestFromXMLFile(final File f) throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        return xml2Request(doc);
    }
    
    public static ResponseBean getResponseFromXMLFile(final File f) throws IOException, XMLException{
        Document doc = getDocumentFromFile(f);
        return xml2Response(doc);
    }
    
    public static String indentXML(final String in) 
            throws ParserConfigurationException,
            SAXException,
            IOException,
            TransformerConfigurationException,
            TransformerException{
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(new ByteArrayInputStream(in.getBytes("UTF-8")));
        Source source = new DOMSource(doc);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Result result = new StreamResult(new OutputStreamWriter(baos));
        
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("indent-number", 4);
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.transform(source, result);
        byte[] arr = baos.toByteArray();
        
        return new String(arr);
    }
}
