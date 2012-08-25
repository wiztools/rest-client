package org.wiztools.restclient;

import java.io.File;
import java.net.URL;
import org.junit.*;
import static org.junit.Assert.*;
import org.wiztools.commons.Charsets;
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
    
    @Test
    public void testStringBody() throws Exception {
        RequestBean expResult = new RequestBean();
        expResult.setMethod(HTTPMethod.POST);
        expResult.setUrl(new URL("http://localhost:10101/"));
        expResult.setHttpVersion(HTTPVersion.HTTP_1_1);
        expResult.setFollowRedirect(true);
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityStringBean body = new ReqEntityStringBean("Subhash loves Aarthi", ct);
        expResult.setBody(body);
        
        Request actual = XMLUtil.getRequestFromXMLFile(new File("src/test/resources/reqBodyString.rcq"));
        
        assertEquals(expResult, actual);
    }
    
    @Test
    public void testFileBody() {
        
    }
    
    @Test
    public void testByteArrayBody() {
        
    }
}
