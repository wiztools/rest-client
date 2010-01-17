package org.wiztools.restclient;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wiztools.commons.Implementation;
import static org.junit.Assert.*;
import org.wiztools.restclient.server.TraceServer;

/**
 *
 * @author subwiz
 */
public class HTTPClientRequestExecuterTest {

    public HTTPClientRequestExecuterTest() {
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
        req.setMethod(HTTPMethod.GET);
        req.setAuthPreemptive(true);
        req.setAuthUsername("subhash");
        req.setAuthPassword("subhash".toCharArray());
        req.addAuthMethod(HTTPAuthMethod.BASIC);
        req.addAuthMethod(HTTPAuthMethod.DIGEST);
        View view = new View() {
            public void doStart(Request request) {
                System.out.println("Starting request...");
            }

            public void doResponse(Response response) {
                System.out.println("in doResponse()...");
                try{
                    String body = response.getResponseBody();
                    if(!body.contains("Authorization: Basic c3ViaGFzaDpzdWJoYXNo")){
                        fail("Pre-emptive Authorization does not happen");
                    }
                }
                catch(UnsupportedEncodingException ex){
                    ex.printStackTrace();
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

        // Execute:
        RequestExecuter executer = Implementation.of(RequestExecuter.class);
        executer.execute(req, view);
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
        request.setMethod(HTTPMethod.POST);
        ReqEntityBean rBean = new ReqEntityBean(new byte[]{}, contentType, charset);
        request.setBody(rBean);
        
        View view = new View() {

            public void doStart(Request request) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doResponse(Response response) {
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
        
        // Execute:
        RequestExecuter executer = Implementation.of(RequestExecuter.class);
        executer.execute(request, view);
    }

    @Test
    public void testMultipleExecution() throws Exception{
        try{
            RequestBean request = getRequestBean();
            RequestExecuter executer = Implementation.of(RequestExecuter.class);
            View view = new ViewAdapter();
            executer.execute(request, view);
            // Second execution should throw exception:
            executer.execute(request, view);
            fail("Multiple execution not allowed for same RequestExecuter object!");
        }
        catch(MultipleRequestInSameRequestExecuterException ex){
            // This is the success path.
        }
    }
}
