package org.wiztools.restclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    
    public static class Issue191Bean {
        private String date;
        private String performance;
        private String totalValue;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPerformance() {
            return performance;
        }

        public void setPerformance(String performance) {
            this.performance = performance;
        }

        public String getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(String totalValue) {
            this.totalValue = totalValue;
        }
        
    }

    /**
     * Test of indentJSON method, of class JSONUtil.
     */
    @Test
    public void testIndentJSON() throws Exception {
        System.out.println("indentJSON");
        String jsonIn = FileUtil.getContentAsString(
                new File("src/test/resources/issue_191/one-line.json"), Charsets.UTF_8);
        
        String expResult = "1.015786164055542498";
        
        String resultJson = JSONUtil.indentJSON(jsonIn);
        Gson gson = new GsonBuilder().create();
        Issue191Bean indentedObj = gson.fromJson(resultJson, Issue191Bean.class);
        String result = indentedObj.performance;
        
        // Test loss of double precision in indented json:
        assertEquals(expResult, result);
    }
    
}
