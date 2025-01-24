package org.wiztools.restclient;

import java.io.File;
import java.net.URL;
import org.junit.*;
import static org.junit.Assert.*;
import org.wiztools.restclient.bean.HTTPMethod;
import org.wiztools.restclient.bean.HTTPVersion;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.RequestBean;
import org.wiztools.restclient.bean.HostnameVerifier;
import org.wiztools.restclient.bean.TLSReqBean;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceRead;

/**
 *
 * @author subwiz
 */
public class SslTest {

    private PersistenceRead p = new XmlPersistenceRead();

    public SslTest() {
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
    public void testSsl()  throws Exception {
        RequestBean expResult = new RequestBean();
        expResult.setUrl(org.wiztools.restclient.util.Url.get("https://www.webshop.co.uk/"));
        expResult.setMethod(HTTPMethod.GET);
        expResult.setHttpVersion(HTTPVersion.HTTP_1_1);
        expResult.setFollowRedirect(true);
        TLSReqBean ssl = new TLSReqBean();
        ssl.setTrustAllCerts(true);
        ssl.setHostNameVerifier(HostnameVerifier.ALLOW_ALL);
        expResult.setTLSReq(ssl);

        Request actual = p.getRequestFromFile(new File("src/test/resources/reqSsl.rcq"));

        assertEquals(expResult, actual);
    }
}
