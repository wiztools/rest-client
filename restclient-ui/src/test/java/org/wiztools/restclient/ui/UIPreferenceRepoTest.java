package org.wiztools.restclient.ui;

import java.io.File;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author subwiz
 */
public class UIPreferenceRepoTest {
    
    public UIPreferenceRepoTest() {
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
     * Test of getStringRepresentation method, of class UIPreferenceRepo.
     */
    @Test
    public void testGetStringRepresentation() {
        System.out.println("getStringRepresentation");
        LinkedList<String> recentFiles = new LinkedList<String>();
        recentFiles.add("subhash.txt");
        recentFiles.add("aarthi.txt");
        UIPreferenceRepo instance = new UIPreferenceRepo();
        String expResult = "subhash.txt;aarthi.txt";
        String result = instance.getStringRepresentation(recentFiles);
        assertEquals(expResult, result);
    }

    /**
     * Test of getListRepresentation method, of class UIPreferenceRepo.
     */
    @Test
    public void testGetListRepresentation() {
        System.out.println("getListRepresentation");
        String recentFilesStr = "subhash.txt;aarthi.txt";
        UIPreferenceRepo instance = new UIPreferenceRepo();
        LinkedList<String> expResult = new LinkedList<String>();
        expResult.add("subhash.txt");
        expResult.add("aarthi.txt");
        LinkedList result = instance.getListRepresentation(recentFilesStr);
        assertEquals(expResult, result);
    }
}
