package org.wiztools.restclient.persistence;

import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.Versions;
import org.wiztools.restclient.bean.CookieVersion;
import org.wiztools.restclient.bean.HTTPMethod;
import org.wiztools.restclient.bean.HTTPVersion;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.RequestBean;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.ResponseBean;
import org.wiztools.restclient.bean.TestResultBean;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class XmlPersistenceRead implements PersistenceRead {
    private String readVersion;
    
    public void setReadVersion(String version) {
        readVersion = version;
    }
    
    private Map<String, String> getHeadersFromHeaderNode(final Element node)
            throws XMLException {
        Map<String, String> m = new LinkedHashMap<>();

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
    
    private List<HttpCookie> getCookiesFromCookiesNode(final Element node) 
            throws XMLException {
        List<HttpCookie> out = new ArrayList<>();
        
        for (int i = 0; i < node.getChildElements().size(); i++) {
            Element e = node.getChildElements().get(i);
            if(!"cookie".equals(e.getQualifiedName())) {
                throw new XMLException("<cookies> element should contain only <cookie> elements");
            }
            
            HttpCookie cookie = new HttpCookie(e.getAttributeValue("name"),
                    e.getAttributeValue("value"));
            final String cookieVerStr = e.getAttributeValue("version");
            if(StringUtil.isNotEmpty(cookieVerStr)) {
                cookie.setVersion(Integer.parseInt(cookieVerStr));
            }
            else {
                cookie.setVersion(CookieVersion.DEFAULT_VERSION.getIntValue());
            }
            out.add(cookie);
        }
        
        return out;
    }
    
    protected Request getRequestBean(Element requestNode)
            throws MalformedURLException, XMLException {
        RequestBean requestBean = new RequestBean();
        
        for (int i = 0; i < requestNode.getChildElements().size(); i++) {
            Element tNode = requestNode.getChildElements().get(i);
            String nodeName = tNode.getQualifiedName();
            if ("http-version".equals(nodeName)) {
                String t = tNode.getValue();
                HTTPVersion httpVersion = "1.1".equals(t)?
                        HTTPVersion.HTTP_1_1 : HTTPVersion.HTTP_1_0;
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
                XmlBodyRead bdUtl = new XmlBodyRead(readVersion);
                ReqEntity body = bdUtl.getReqEntity(tNode);
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

    protected Request xml2Request(final Document doc)
            throws MalformedURLException, XMLException {
        // get the rootNode
        Element rootNode = doc.getRootElement();

        if (!"rest-client".equals(rootNode.getQualifiedName())) {
            throw new XMLException("Root node is not <rest-client>");
        }

        // checking correct rest version
        final String rcVersion = rootNode.getAttributeValue("version");
        try {
            Versions.versionValidCheck(rcVersion);
        }
        catch(Versions.VersionValidationException ex) {
            throw new XMLException(ex);
        }
        
        readVersion = rcVersion;

        // if more than two request element is present then throw the exception 
        if (rootNode.getChildElements().size() != 1) {
            throw new XMLException("There can be only one child node for root node: <request>");
        }
        // minimum one request element is present in xml 
        if (rootNode.getFirstChildElement("request") == null) {
            throw new XMLException("The child node of <rest-client> should be <request>");
        }
        Element requestNode = rootNode.getFirstChildElement("request");
        
        return getRequestBean(requestNode);
    }
    
    protected Response xml2Response(final Document doc)
            throws XMLException {
        ResponseBean responseBean = new ResponseBean();

        // get the rootNode
        Element rootNode = doc.getRootElement();

        if (!"rest-client".equals(rootNode.getQualifiedName())) {
            throw new XMLException("Root node is not <rest-client>");
        }

        // checking correct rest version
        try {
            Versions.versionValidCheck(rootNode.getAttributeValue("version"));
        }
        catch(Versions.VersionValidationException ex) {
            throw new XMLException(ex);
        }

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
    
    protected Document getDocumentFromFile(final File f)
            throws IOException, XMLException {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(f);
            return doc;
        }
        catch (ParsingException | IOException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public Request getRequestFromFile(final File f)
            throws IOException, PersistenceException {
        Document doc = getDocumentFromFile(f);
        return xml2Request(doc);
    }

    @Override
    public Response getResponseFromFile(final File f)
            throws IOException, PersistenceException {
        Document doc = getDocumentFromFile(f);
        return xml2Response(doc);
    }
}
