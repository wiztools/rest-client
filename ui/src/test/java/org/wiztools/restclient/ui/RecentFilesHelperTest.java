package org.wiztools.restclient.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author subwiz
 */
public class RecentFilesHelperTest {
    
    public RecentFilesHelperTest() {
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
    
    private String encode(String str) {
        try{
            return URLEncoder.encode(str, "UTF-8");
        }
        catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Test of getStringRepresentation method, of class UIPreferenceRepo.
     */
    @Test
    public void testGetStringRepresentation() throws Exception{
        System.out.println("getStringRepresentation");
        LinkedList<File> recentFiles = new LinkedList<File>();
        File file1 = new File("subhash.txt");
        recentFiles.add(file1);
        File file2 = new File("aarthi.txt");
        recentFiles.add(file2);
        RecentFilesHelper instance = new RecentFilesHelper();
        String expResult = encode(file1.getAbsolutePath()) + ";" + encode(file2.getAbsolutePath());
        String result = instance.getStringRepresentation(recentFiles);
        assertEquals(expResult, result);
    }

    /**
     * Test of getListRepresentation method, of class UIPreferenceRepo.
     */
    @Test
    public void testGetListRepresentation() {
        System.out.println("getListRepresentation");
        RecentFilesHelper instance = new RecentFilesHelper();
        
        File file1 = new File(new File(System.getProperty("user.dir")), "subhash.txt");
        File file2 = new File(new File(System.getProperty("user.dir")), "aarthi.txt");
        
        LinkedList<File> expResult = new LinkedList<File>();
        expResult.add(file1);
        expResult.add(file2);
        
        String recentFilesStr = instance.getStringRepresentation(expResult);
        List<File> result = instance.getListRepresentation(recentFilesStr);
        assertEquals(expResult, result);
    }
}
