package org.wiztools.restclient.ant;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author subwiz
 */
public class RESTClientTaskTest extends BuildFileTest {
    
    public RESTClientTaskTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        configureProject("src/test/resources/build.xml");
    }

    @Override
    protected void tearDown() throws Exception {
        
    }

    /**
     * Test of execute method, of class RESTClientTask.
     */
    public void testExecute() {
        System.out.println("execute");
        executeTarget("run.restclient");
        // instance.execute();
    }

}
