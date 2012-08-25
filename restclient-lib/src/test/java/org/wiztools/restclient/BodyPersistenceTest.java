package org.wiztools.restclient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import static org.junit.Assert.*;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.util.XMLUtil;

/**
 *
 * @author subwiz
 */
public class BodyPersistenceTest {
    
    public BodyPersistenceTest() {
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
    
    private RequestBean getRequestBeanWithoutBody() throws MalformedURLException {
        RequestBean expResult = new RequestBean();
        expResult.setMethod(HTTPMethod.POST);
        expResult.setUrl(new URL("http://localhost:10101/"));
        expResult.setHttpVersion(HTTPVersion.HTTP_1_1);
        expResult.setFollowRedirect(true);
        return expResult;
    }
    
    @Test
    public void testStringBody() throws Exception {
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityStringBean body = new ReqEntityStringBean("Subhash loves Aarthi", ct);
        expResult.setBody(body);
        
        Request actual = XMLUtil.getRequestFromXMLFile(new File("src/test/resources/reqBodyString.rcq"));
        
        assertEquals(expResult, actual);
    }
    
    @Test
    public void testFileBody() throws Exception {
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityFileBean body = new ReqEntityFileBean(new File("/etc/hosts"), ct);
        expResult.setBody(body);
        
        Request actual = XMLUtil.getRequestFromXMLFile(new File("src/test/resources/reqBodyFile.rcq"));
        
        assertEquals(expResult, actual);
    }
    
    @Test
    public void testByteArrayBody() throws Exception {
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("image/jpeg", null);
        byte[] arr = FileUtil.getContentAsBytes(new File("src/test/resources/subhash_by_aarthi.jpeg"));
        ReqEntityByteArrayBean body = new ReqEntityByteArrayBean(arr, ct);
        expResult.setBody(body);
        
        Request actual = XMLUtil.getRequestFromXMLFile(new File("src/test/resources/reqBodyByteArray.rcq"));
        
        assertEquals(expResult, actual);
    }
}
