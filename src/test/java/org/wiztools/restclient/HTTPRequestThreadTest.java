/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import static org.junit.Assert.*;

/**
 *
 * @author subwiz
 */
public class HTTPRequestThreadTest {

    public HTTPRequestThreadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private Server jServer;
    private static final int PORT = 45327;

    @Before
    public void setUp() throws Exception {
        jServer = new Server(PORT);
        jServer.start();
    }

    @After
    public void tearDown() throws Exception {
        if(jServer != null){
            jServer.stop();
        }
    }

    /**
     * Test of run method, of class HTTPRequestThread.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        
        final String contentType = "test/text";
        final String charset = "UTF-8";
        RequestBean request = new RequestBean();
        request.setUrl(new URL("http://localhost:"+PORT+"/"));
        request.setMethod("POST");
        ReqEntityBean rBean = new ReqEntityBean("", contentType, charset);
        request.setBody(rBean);
        
        View view = new View() {

            public void doStart(RequestBean request) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doResponse(ResponseBean response) {
                System.out.println(response);
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doEnd() {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doError(String error) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        
        // jServer.
        
        HTTPRequestThread instance = new HTTPRequestThread(request, view);
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

}