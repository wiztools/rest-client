package org.wiztools.restclient;

import org.wiztools.restclient.util.XMLUtil;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.ReqEntityStringBean;
import org.wiztools.restclient.bean.HTTPMethod;
import org.wiztools.restclient.bean.RequestBean;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.ResponseBean;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wiztools.commons.Charsets;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.PersistenceWrite;
import org.wiztools.restclient.persistence.XmlPersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class XMLUtilTest {
    
    private PersistenceRead pRead = new XmlPersistenceRead();
    private PersistenceWrite pWrite = new XmlPersistenceWrite();

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
        ContentType contentType = new ContentTypeBean("text/plain", Charsets.UTF_8);
        expResult.setBody(new ReqEntityStringBean("Body Text", contentType));
        
        BasicAuthBean auth = new BasicAuthBean();
        auth.setPreemptive(true);
        auth.setRealm("realm");
        auth.setUsername("username");
        auth.setPassword("password".toCharArray());
        expResult.setAuth(auth);
        
        expResult.setFollowRedirect(true);
        return expResult;
    }

    private ResponseBean getDefaultResponseBean(){
        ResponseBean expResult = new ResponseBean();
        expResult.setStatusLine("HTTP/1.1 200 OK");
        expResult.setStatusCode(200);
        expResult.addHeader("Content-Type", "text/plain; charset=utf-8");
        expResult.addHeader("Transfer-Encoding", "chunked");
        expResult.addHeader("Server", "Jetty(6.1.25)");
        expResult.setResponseBody(Util.base64decodeByteArray("KipSRVNUQ2xpZW50IFRyYWNlU2VydmxldCoqCgoqTWV0aG9kKgoJR0VUCgoqUGF0aCBJbmZvKgoJLwoKKkhlYWRlcnMqCglIb3N0OiBsb2NhbGhvc3Q6MTAxMDEKCUNvbm5lY3Rpb246IEtlZXAtQWxpdmUKCVVzZXItQWdlbnQ6IEFwYWNoZS1IdHRwQ2xpZW50LzQuMi4xIChqYXZhIDEuNSkKCipRdWVyeSBTdHJpbmcqCgludWxsCgoqUGFyYW1ldGVycyoKCipCb2R5IChGaXJzdCAxMDAgY2hhcmFjdGVycyBvbmx5KSoKCQoK"));
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
            assertEquals(expResult, result);
        }

        // When document does not have encoding attribute:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset2.xml");
            String expResult = Charsets.UTF_8.name();
            System.out.println("expResult: " + expResult);
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println("encoding attribute: " + result ) ;
            assertEquals(expResult, result);
        }

        // When document does not have XML declaration:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset3.xml");
            String expResult = Charsets.UTF_8.name();
            System.out.println("expResult: " + expResult);
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println("encoding attribute: " + result ) ;
            assertEquals(expResult, result);
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
        pWrite.writeRequest(bean, f);
        Request expResult = pRead.getRequestFromFile(f);
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
        pWrite.writeResponse(bean, f);
        Response expResult = pRead.getResponseFromFile(f);
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
        
        Request result = pRead.getRequestFromFile(f);
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
        
        Response result = pRead.getResponseFromFile(f);
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
        Request req = pRead.getRequestFromFile(f);
        File outFile = File.createTempFile("abc", "xyz");
        pWrite.writeRequest(req, outFile);
        Request req1 = pRead.getRequestFromFile(outFile);
        assertEquals(req.getTestScript(), req1.getTestScript());
    }
}