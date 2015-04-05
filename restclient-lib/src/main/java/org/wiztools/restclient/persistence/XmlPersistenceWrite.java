package org.wiztools.restclient.persistence;

import java.io.*;
import java.net.HttpCookie;
import java.util.*;
import nu.xom.*;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.restclient.Versions;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.util.Util;
import org.wiztools.restclient.util.XMLUtil;

/**
 *
 * @author rsubramanian
 */
public class XmlPersistenceWrite implements PersistenceWrite {
    
    private Element getRootElement() {
        Element eRoot = new Element("rest-client");
        // set version attributes to rest-client root tag
        eRoot.addAttribute(new Attribute("version", Versions.CURRENT));
        return eRoot;
    }
    
    protected Element getRequestElement(final Request bean) {
        Element reqElement = new Element("request");

        { // HTTP Version
            Element e = new Element("http-version");
            e.appendChild(bean.getHttpVersion().versionNumber());
            reqElement.appendChild(e);
        }

        if(bean.isFollowRedirect()) { // HTTP Follow Redirect
            Element e = new Element("http-follow-redirects");
            reqElement.appendChild(e);
        }

        if(bean.isIgnoreResponseBody()) { // Response body ignored
            Element e = new Element("ignore-response-body");
            reqElement.appendChild(e);
        }

        { // creating the URL child element
            Element e = new Element("URL");
            e.appendChild(bean.getUrl().toString());
            reqElement.appendChild(e);
        }

        { // creating the method child element
            Element e = new Element("method");
            e.appendChild(bean.getMethod().name());
            reqElement.appendChild(e);
        }

        { // auth
            Auth auth = bean.getAuth();
            if(auth != null) {
                Element eAuth = XmlAuthUtil.getAuthElement(auth);
                reqElement.appendChild(eAuth);
            }
        }

        // Creating SSL elements
        if(bean.getSslReq() != null) {
            Element eSsl = XmlSslUtil.getSslReq(bean.getSslReq());
            reqElement.appendChild(eSsl);
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
            reqElement.appendChild(e);
        }
        
        // Cookies
        List<HttpCookie> cookies = bean.getCookies();
        if(!cookies.isEmpty()) {
            Element e = new Element("cookies");
            
            for(HttpCookie cookie: cookies) {
                Element ee = new Element("cookie");
                ee.addAttribute(new Attribute("name", cookie.getName()));
                ee.addAttribute(new Attribute("value", cookie.getValue()));
                ee.addAttribute(new Attribute("version", String.valueOf(cookie.getVersion())));
                e.appendChild(ee);
            }
            reqElement.appendChild(e);
        }

        { // creating the body child element
            ReqEntity entityBean = bean.getBody();
            if(entityBean != null) {
                XmlBodyWrite bdUtl = new XmlBodyWrite();
                Element e = bdUtl.getReqEntity(entityBean);
                reqElement.appendChild(e);
            }
        }

        // creating the test-script child element
        String testScript = bean.getTestScript();
        if (testScript != null) {

            Element e = new Element("test-script");
            e.appendChild(testScript);
            reqElement.appendChild(e);
        }
        return reqElement;
    }

    protected Document request2XML(final Request bean)
            throws XMLException {
        Element reqRootElement = getRootElement();
        reqRootElement.appendChild(getRequestElement(bean));

        Document xomDocument = new Document(reqRootElement);
        return xomDocument;
    }
    
    protected Element getResponseElement(final Response bean) {
        Element respElement = new Element("response");
        Element respChildSubElement = null;
        Element respChildSubSubElement = null;

        // adding first sub child element - execution-time and append to response child element
        respChildSubElement = new Element("execution-time");
        respChildSubElement.appendChild(String.valueOf(bean.getExecutionTime()));
        respElement.appendChild(respChildSubElement);

        // adding second sub child element - status and code attributes and append to response child element
        respChildSubElement = new Element("status");
        Attribute codeAttributes = new Attribute("code", String.valueOf(bean.getStatusCode()));
        respChildSubElement.addAttribute(codeAttributes);
        respChildSubElement.appendChild(bean.getStatusLine());
        respElement.appendChild(respChildSubElement);

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
            respElement.appendChild(respChildSubElement);
        }

        byte[] responseBody = bean.getResponseBody();
        if (responseBody != null) {
            //creating the body child element and append to response child element
            respChildSubElement = new Element("body");
            final String base64encodedBody = Util.base64encode(responseBody);
            respChildSubElement.appendChild(base64encodedBody);
            respElement.appendChild(respChildSubElement);
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

            respElement.appendChild(respChildSubElement);
        }
        return respElement;
    }

    protected Document response2XML(final Response bean)
            throws XMLException {
        Element respRootElement = getRootElement();
        respRootElement.appendChild(getResponseElement(bean));

        Document xomDocument = new Document(respRootElement);
        return xomDocument;
    }    

    protected void writeXML(final Document doc, final File f)
            throws IOException, XMLException {

        try {
            OutputStream out = new FileOutputStream(f);
            out = new BufferedOutputStream(out);
            // getDocumentCharset(f) - to retrieve the charset encoding attribute
            Serializer serializer = new Serializer(out, XMLUtil.getDocumentCharset(f));
            serializer.write(doc);
            out.close();
        } catch (IOException ex) {
            throw new XMLException(ex.getMessage(), ex);
        }
    }

    @Override
    public void writeRequest(final Request bean, final File f)
            throws IOException, XMLException {
        Document doc = request2XML(bean);
        writeXML(doc, f);
    }

    @Override
    public void writeResponse(final Response bean, final File f)
            throws IOException, XMLException {
        Document doc = response2XML(bean);
        writeXML(doc, f);
    }
}
