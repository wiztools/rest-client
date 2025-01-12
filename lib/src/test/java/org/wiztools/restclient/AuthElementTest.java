package org.wiztools.restclient;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;

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
        PersistenceRead p = new XmlPersistenceRead();
        Request req = p.getRequestFromFile(new File("src/test/resources/reqOAuth2Bearer.rcq"));
        Auth a = req.getAuth();
        OAuth2BearerAuth auth = (OAuth2BearerAuth) a;
        assertEquals("subhash", auth.getOAuth2BearerToken());
    }
    
    @Test
    public void testNtlm() throws Exception {
        System.out.println("testNtlm");
        PersistenceRead p = new XmlPersistenceRead();
        Request req = p.getRequestFromFile(new File("src/test/resources/reqNtlm.rcq"));
        Auth a = req.getAuth();
        NtlmAuth auth = (NtlmAuth) a;
        
        NtlmAuthBean expResult = new NtlmAuthBean();
        expResult.setDomain("testdomain");
        expResult.setWorkstation("testworkstation");
        expResult.setUsername("subwiz");
        expResult.setPassword("password".toCharArray());
        
        assertEquals(expResult, auth);
    }
}
