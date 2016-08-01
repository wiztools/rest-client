package org.wiztools.restclient.ui.update;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wiztools.appupdate.VersionImpl;

/**
 *
 * @author subhash
 */
public class AppUpdateRunnerTest {
    
    public AppUpdateRunnerTest() {
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
     * Test of doUpdateCheck method, of class AppUpdateRunner.
     */
    @Test
    public void testDoUpdateCheck() {
        System.out.println("doUpdateCheck");
        long lastUpdateCheck = 0L;
        AppUpdateRunner instance = new AppUpdateRunner();
        boolean expResult = true;
        boolean result = instance.doUpdateCheck(lastUpdateCheck);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testRequiresUpdate() throws Exception {
        AppUpdateRunner instance = new AppUpdateRunner();
        boolean result = instance.requiresUpdate(new VersionImpl("10.0"));
        assertTrue(result);
    }
}
