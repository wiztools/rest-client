/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.xml;

import java.io.File;
import java.net.URL;
import nu.xom.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wiztools.restclient.ReqEntityBean;
import static org.junit.Assert.*;
import org.wiztools.restclient.RequestBean;
import org.wiztools.restclient.ResponseBean;

/**
 *
 * @author subwiz
 */
public class XMLUtilTest {

    public XMLUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDocumentCharset method, of class XMLUtil.
     */
    @Test
    public void testGetDocumentCharset() throws Exception {
        System.out.println("getDocumentCharset");
        File f = new File("src/test/resources/reqFromXml.rcq");
        String expResult = "UTF-8";
        String result = XMLUtil.getDocumentCharset(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of writeRequestXML method, of class XMLUtil.
     */
    /*@Test
    public void testWriteRequestXML() throws Exception {
        System.out.println("writeRequestXML");
        RequestBean bean = null;
        File f = null;
        XMLUtil.writeRequestXML(bean, f);
    }*/

    /**
     * Test of writeResponseXML method, of class XMLUtil.
     */
    /*@Test
    public void testWriteResponseXML() throws Exception {
        System.out.println("writeResponseXML");
        ResponseBean bean = null;
        File f = null;
        XMLUtil.writeResponseXML(bean, f);
    }*/

    /**
     * Test of getRequestFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetRequestFromXMLFile() throws Exception {
        System.out.println("getRequestFromXMLFile");
        File f = new File("src/test/resources/reqFromXml.rcq");

        RequestBean expResult = new RequestBean();
        expResult.setUrl(new URL("http://localhost:10101/"));
        expResult.setMethod("POST");
        expResult.addHeader("key1", "value1");
        expResult.setBody(new ReqEntityBean("Body Text", "text/plain", "UTF-8"));
        expResult.addAuthMethod("BASIC");
        expResult.setAuthPreemptive(true);
        expResult.setAuthRealm("realm");
        expResult.setAuthUsername("username");
        expResult.setAuthPassword("password".toCharArray());
        
        RequestBean result = XMLUtil.getRequestFromXMLFile(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of getResponseFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetResponseFromXMLFile() throws Exception {
        System.out.println("getResponseFromXMLFile");
        File f = new File("src/test/resources/resFromXml.rcs");

        ResponseBean expResult = new ResponseBean();
        expResult.setStatusLine("HTTP/1.1 200 OK");
        expResult.setStatusCode(200);
        expResult.addHeader("Content-Type", "text/plain; charset=utf-8");
        expResult.addHeader("Transfer-Encoding", "chunked");
        expResult.addHeader("Server", "Jetty");
        expResult.setResponseBody("**RESTClient TraceServlet**");
        
        ResponseBean result = XMLUtil.getResponseFromXMLFile(f);
        assertEquals(expResult, result);
    }

}