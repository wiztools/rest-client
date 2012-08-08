package org.wiztools.restclient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    private RequestBean getDefaultRequestBean() throws MalformedURLException{
        RequestBean expResult = new RequestBean();
        expResult.setUrl(new URL("http://localhost:10101/"));
        expResult.setMethod(HTTPMethod.POST);
        expResult.addHeader("key1", "value1");
        expResult.setBody(new ReqEntityStringBean("Body Text", "text/plain", "UTF-8"));
        expResult.addAuthMethod(HTTPAuthMethod.BASIC);
        expResult.setAuthPreemptive(true);
        expResult.setAuthRealm("realm");
        expResult.setAuthUsername("username");
        expResult.setAuthPassword("password".toCharArray());
        return expResult;
    }

    private ResponseBean getDefaultResponseBean(){
        ResponseBean expResult = new ResponseBean();
        expResult.setStatusLine("HTTP/1.1 200 OK");
        expResult.setStatusCode(200);
        expResult.addHeader("Content-Type", "text/plain; charset=utf-8");
        expResult.addHeader("Transfer-Encoding", "chunked");
        expResult.addHeader("Server", "Jetty");
        expResult.setResponseBody("**RESTClient TraceServlet**");
        return expResult;
    }

    /**
     * Test of getDocumentCharset method, of class XMLUtil.
     */
    @Test
    public void testGetDocumentCharset() throws Exception {
        System.out.println("getDocumentCharset");

        // When document complies to standard:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset1.xml");
            String expResult = "UTF-8";
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println("encoding attribute: " + result ) ;
            // assertEquals(expResult, result);
        }

        // When document does not have encoding attribute:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset2.xml");
            String expResult = Charset.defaultCharset().displayName();
            System.out.println("expResult: " + expResult);
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println("encoding attribute: " + result ) ;
            // assertEquals(expResult, result);
        }

        // When document does not have XML declaration:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset3.xml");
            String expResult = Charset.defaultCharset().displayName();
            System.out.println("expResult: " + expResult);
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println("encoding attribute: " + result ) ;
            // assertEquals(expResult, result);
        }

    }

    /**
     * Test of writeRequestXML method, of class XMLUtil.
     */
    @Test
    public void testWriteRequestXML() throws Exception {
        System.out.println("writeRequestXML");
        RequestBean bean = getDefaultRequestBean();
        File f = File.createTempFile("prefix", ".rcq");
        XMLUtil.writeRequestXML(bean, f);
        Request expResult = XMLUtil.getRequestFromXMLFile(f);
        assertEquals(expResult, bean);
    }

    /**
     * Test of writeResponseXML method, of class XMLUtil.
     */
    @Test
    public void testWriteResponseXML() throws Exception {
        System.out.println("writeResponseXML");
        ResponseBean bean = getDefaultResponseBean();
        File f = File.createTempFile("prefix", ".rcs");
        XMLUtil.writeResponseXML(bean, f);
        Response expResult = XMLUtil.getResponseFromXMLFile(f);
        assertEquals(expResult, bean);
    }

    /**
     * Test of getRequestFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetRequestFromXMLFile() throws Exception {
        System.out.println("getRequestFromXMLFile");
        File f = new File("src/test/resources/reqFromXml.rcq");

        RequestBean expResult = getDefaultRequestBean();
        
        Request result = XMLUtil.getRequestFromXMLFile(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of getResponseFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetResponseFromXMLFile() throws Exception {
        System.out.println("getResponseFromXMLFile");
        File f = new File("src/test/resources/resFromXml.rcs");

        ResponseBean expResult = getDefaultResponseBean();
        
        Response result = XMLUtil.getResponseFromXMLFile(f);
        assertEquals(expResult, result);
    }

    /**
     * Test to verify if the write operation of Request corrupts
     * the content of the test script.
     * @throws java.lang.Exception
     */
    @Test
    public void testIntegrityOfTestScript() throws Exception{
        File f = new File("src/test/resources/resTestScriptIntegrity.rcq");
        Request req = XMLUtil.getRequestFromXMLFile(f);
        File outFile = File.createTempFile("abc", "xyz");
        XMLUtil.writeRequestXML(req, outFile);
        Request req1 = XMLUtil.getRequestFromXMLFile(outFile);
        assertEquals(req.getTestScript(), req1.getTestScript());
    }
}