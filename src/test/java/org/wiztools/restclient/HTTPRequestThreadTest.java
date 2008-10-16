/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.net.MalformedURLException;
import java.net.URL;
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
public class HTTPRequestThreadTest {

    public HTTPRequestThreadTest() {
    }
    
    private RequestBean getRequestBean() throws MalformedURLException{
        RequestBean request = new RequestBean();
        request.setUrl(new URL("http://localhost:"+TraceServer.DEFAULT_PORT+"/"));
        return request;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        TraceServer.start();
    }

    @After
    public void tearDown() throws Exception {
        TraceServer.stop();
    }
    
    @Test
    public void testPremptiveAuth() throws Exception{
        System.out.println("testPreemptiveAuth");
        RequestBean req = getRequestBean();
        req.setMethod("GET");
        req.setAuthPreemptive(true);
        req.setAuthUsername("subhash");
        req.setAuthPassword("subhash".toCharArray());
        req.addAuthMethod("GET");
        req.addAuthMethod("DIGEST");
        View view = new View() {
            public void doStart(RequestBean request) {
                System.out.println("Starting request...");
            }

            public void doResponse(ResponseBean response) {
                System.out.println("in doResponse()...");
                String body = response.getResponseBody();
                if(!body.contains("Authorization: Basic c3ViaGFzaDpzdWJoYXNo")){
                    fail("Pre-emptive Authorization does not happen");
                }
            }

            public void doEnd() {
                
            }

            public void doError(String error) {
                System.out.println(error);
            }

            public void doCancelled() {
                
            }
        };
        HTTPRequestThread instance = new HTTPRequestThread(req, view);
        instance.run();
    }

    /**
     * Test of run method, of class HTTPRequestThread.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        
        final String contentType = "test/text";
        final String charset = "UTF-8";
        RequestBean request = getRequestBean();
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
                System.out.println(error);
            }

            public void doCancelled() {
                
            }
        };
        
        // jServer.
        
        HTTPRequestThread instance = new HTTPRequestThread(request, view);
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

}