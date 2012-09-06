package org.wiztools.restclient;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import org.wiztools.restclient.bean.Auth;
import org.wiztools.restclient.bean.OAuth2BearerAuth;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.util.XMLUtil;

/**
 *
 * @author subwiz
 */
public class AuthElementTest {
    
    public AuthElementTest() {
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
    public void testOAuth2Bearer() throws Exception {
        System.out.println("testOAuth2Bearer");
        Request req = XMLUtil.getRequestFromXMLFile(new File("src/test/resources/reqOAuth2Bearer.rcq"));
        Auth a = req.getAuth();
        OAuth2BearerAuth auth = (OAuth2BearerAuth) a;
        assertEquals("subhash", auth.getOAuth2BearerToken());
    }
}
