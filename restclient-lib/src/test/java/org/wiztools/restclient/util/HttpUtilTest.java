package org.wiztools.restclient.util;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wiztools.commons.Charsets;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ContentTypeBean;

/**
 *
 * @author subwiz
 */
public class HttpUtilTest {
    
    public HttpUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getContentType method, of class HttpUtil.
     */
    @Test
    public void testGetContentType_String() {
        System.out.println("getContentType");
        String header = "application/vnd.mnet.staticwebspaces+xml;version=1;charset=UTF-8";
        ContentType expResult = new ContentTypeBean("application/vnd.mnet.staticwebspaces+xml", Charsets.UTF_8);
        ContentType result = HttpUtil.getContentType(header);
        assertEquals(expResult, result);
    }    
}
