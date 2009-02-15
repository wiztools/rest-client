package org.wiztools.restclient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author subwiz
 */
public class ImplementationTest {

    public ImplementationTest() {
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
     * Test of of method, of class Implementation.
     */
    @Test
    public void testOfSingleton() {
        System.out.println("of");
        IGlobalOptions expResult = Implementation.of(IGlobalOptions.class);
        IGlobalOptions result = Implementation.of(IGlobalOptions.class);
        assertSame(expResult, result);
    }

    @Test
    public void testOfNewInstance() {
        RequestExecuter expResult = Implementation.of(RequestExecuter.class);
        RequestExecuter result = Implementation.of(RequestExecuter.class);
        assertNotSame(expResult, result);
    }
}