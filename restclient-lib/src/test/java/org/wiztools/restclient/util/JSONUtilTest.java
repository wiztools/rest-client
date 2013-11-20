package org.wiztools.restclient.util;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;

/**
 *
 * @author subwiz
 */
public class JSONUtilTest {
    
    public JSONUtilTest() {
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
     * Test of indentJSON method, of class JSONUtil.
     */
    @Test
    public void testIndentJSON() throws Exception {
        System.out.println("indentJSON");
        String jsonIn = FileUtil.getContentAsString(
                new File("src/test/resources/issue_191/one-line.json"), Charsets.UTF_8);
        String expResult = FileUtil.getContentAsString(
                new File("src/test/resources/issue_191/indented.json"),
                Charsets.UTF_8).trim();
        String result = JSONUtil.indentJSON(jsonIn);
        assertEquals(expResult, result);
    }
    
}
