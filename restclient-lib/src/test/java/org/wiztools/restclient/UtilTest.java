package org.wiztools.restclient;

import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.Util;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapLinkedHashSet;

/**
 *
 * @author subWiz
 */
public class UtilTest {

    public UtilTest() {
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
     * Test of parameterEncode method, of class Util.
     */
    @Test
    public void testParameterEncode() {
        System.out.println("parameterEncode");
        MultiValueMap<String, String> params = new MultiValueMapLinkedHashSet<String, String>();
        params.put("q", "r1");
        params.put("q", "r2");
        String expResult = "q=r1&q=r2";
        String result = Util.parameterEncode(params);
        assertEquals(expResult, result);
    }

    /**
     * Test of getStatusCodeFromStatusLine method, of class Util.
     */
    /*@Test
    public void testGetStatusCodeFromStatusLine() {
        System.out.println("getStatusCodeFromStatusLine");
        String statusLine = "";
        int expResult = 0;
        int result = Util.getStatusCodeFromStatusLine(statusLine);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getFormattedContentType method, of class Util.
     */
    /*@Test
    public void testGetFormattedContentType() {
        System.out.println("getFormattedContentType");
        String contentType = "";
        String charset = "";
        String expResult = "";
        String result = Util.getFormattedContentType(contentType, charset);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getCharsetFromContentType method, of class Util.
     */
    @Test
    public void testGetCharsetFromContentType() {
        System.out.println("getCharsetFromContentType");
        String contentType = "Content-type: text/html; charset=UTF-8";
        String expResult = "UTF-8";
        String result = HttpUtil.getCharsetFromContentType(contentType);
        assertEquals(expResult, result);

        // when charset is not available, return null:
        contentType = "Content-type: text/html";
        expResult = null;
        result = HttpUtil.getCharsetFromContentType(contentType);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetMimeFromContentType() {
        System.out.println("getMimeFromContentType");
        String contentType = "application/xml;charset=UTF-8";
        String expResult = "application/xml";
        String result = HttpUtil.getMimeFromContentType(contentType);
        assertEquals(expResult, result);
    }
}