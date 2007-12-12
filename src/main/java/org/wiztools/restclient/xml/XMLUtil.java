/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.restclient.xml;

import org.wiztools.restclient.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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

    public static Document request2XML(final RequestBean bean)
            throws ParserConfigurationException {
        Document xmldoc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Element e = null;
        Element request = null;
        Node n = null;
        Element subChild = null;
        Node node = null;

        xmldoc = impl.createDocument(null, "rest-client", null);
        Element root = xmldoc.getDocumentElement();

        request = xmldoc.createElementNS(null, "request");
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
        if (authMethods != null || authMethods.size() > 0) {
            e = xmldoc.createElementNS(null, "auth-methods");
            String methods = "";
            for (String authMethod : authMethods) {
                methods = methods + authMethod + ",";
            }
            String authenticationMethod = methods.substring(0, methods.length() - 1);
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
        String authPassword = bean.getAuthPassword().toString();
        if (authPassword != null) {
            e = xmldoc.createElementNS(null, "auth-password");
            n = xmldoc.createTextNode(authPassword);
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
        if (rBean.getBody() != null) {
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
        root.appendChild(request);

        return xmldoc;
    }

    public static RequestBean xml2Request(final Document doc)
            throws MalformedURLException {
        RequestBean requestBean = new RequestBean();
        NodeList elements = null;
        Node node = null;

        //get url
        elements = doc.getElementsByTagName("URL");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            URL url = new URL(node.getTextContent());
            requestBean.setUrl(url);
        }

        //get method
        elements = doc.getElementsByTagName("method");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            requestBean.setMethod(node.getTextContent());
        }

        //get auth-methods
        elements = doc.getElementsByTagName("auth-methods");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            String[] authenticationMethods = node.getTextContent().split(",");
            for (int j = 0; j < authenticationMethods.length; j++) {
                requestBean.addAuthMethod(authenticationMethods[j]);
            }
        }

        //get auth-preemptive
        elements = doc.getElementsByTagName("auth-preemptive");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            if (node.getTextContent().equals("true")) {
                requestBean.setAuthPreemptive(true);
            }
            else{
                requestBean.setAuthPreemptive(false);
            }
        }

        //get auth-host
        elements = doc.getElementsByTagName("auth-host");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            requestBean.setAuthHost(node.getTextContent());
        }

        //get auth-realm
        elements = doc.getElementsByTagName("auth-realm");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            requestBean.setAuthRealm(node.getTextContent());
        }

        //get auth-username
        elements = doc.getElementsByTagName("auth-username");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            requestBean.setAuthUsername(node.getTextContent());
        }

        //get password
        elements = doc.getElementsByTagName("auth-password");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            requestBean.setAuthPassword(node.getTextContent().toCharArray());
        }

        //get headers
        elements = doc.getElementsByTagName("header");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            NamedNodeMap nodeMap = node.getAttributes();
            Node key = nodeMap.getNamedItem("key");
            Node value = nodeMap.getNamedItem("value");
            requestBean.addHeader(key.getNodeValue(), value.getNodeValue());
        }

        //get body
        elements = doc.getElementsByTagName("body");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            NamedNodeMap nodeMap = node.getAttributes();
            Node contentType = nodeMap.getNamedItem("content-type");
            Node charSet = nodeMap.getNamedItem("charset");
            requestBean.setBody(new ReqEntityBean(node.getTextContent(), contentType.getNodeValue(),
                    charSet.getNodeValue()));
        }

        return requestBean;
    }

    public static Document response2XML(final ResponseBean bean)
            throws ParserConfigurationException {

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

        response = xmldoc.createElementNS(null, "response");

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

        root.appendChild(response);

        return xmldoc;
    }

    public static ResponseBean xml2Response(final Document doc) {
        ResponseBean responseBean = new ResponseBean();
        NodeList elements = null;
        Node node = null;

        //get status line and status code
        elements = doc.getElementsByTagName("status");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            responseBean.setStatusLine(node.getTextContent());
            NamedNodeMap nodeMap = node.getAttributes();
            Node n = nodeMap.getNamedItem("code");
            responseBean.setStatusCode(Integer.parseInt(n.getNodeValue()));
        }

        //get headers
        elements = doc.getElementsByTagName("header");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            NamedNodeMap nodeMap = node.getAttributes();
            Node key = nodeMap.getNamedItem("key");
            Node value = nodeMap.getNamedItem("value");
            responseBean.addHeader(key.getNodeValue(), value.getNodeValue());
        }

        //get body
        elements = doc.getElementsByTagName("body");
        for (int i = 0; i < elements.getLength(); i++) {
            node = elements.item(i);
            System.out.println("body content" + node.getTextContent());
            responseBean.setResponseBody(node.getTextContent());
        }

        return responseBean;
    }

    public static void writeXML(final Document doc, final File f)
            throws IOException, TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        FileOutputStream out = new FileOutputStream(f);
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.transform(domSource, streamResult);
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

    public static void writeRequestXML(final RequestBean bean, final File f)
            throws IOException, ParserConfigurationException,
            TransformerConfigurationException, TransformerException {
        Document doc = request2XML(bean);
        writeXML(doc, f);
    }

    public static void writeResponseXML(final ResponseBean bean, final File f)
            throws IOException, ParserConfigurationException,
            TransformerConfigurationException, TransformerException {
        Document doc = response2XML(bean);
        writeXML(doc, f);
    }

    public static void writeXMLRequest(final File f, RequestBean bean)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        bean = xml2Request(doc);
    }

    public static void writeXMLResponse(final File f, ResponseBean bean)
            throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        bean = xml2Response(doc);
    }

    public static RequestBean getRequestFromXMLFile(final File f) throws IOException, XMLException {
        Document doc = getDocumentFromFile(f);
        return xml2Request(doc);
    }
}
