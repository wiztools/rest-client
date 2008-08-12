/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 *
 * @author subwiz
 */
public class TraceServer {
    public static final int PORT = 10101;
    
    private static Server server = new Server(PORT);
    
    static{
        Context root = new Context(server,"/",Context.SESSIONS);
        root.addServlet(new ServletHolder(new TraceServlet()), "/*");
        server.setStopAtShutdown(true);
    }
    
    public static synchronized void start() throws Exception{
        if(!(server.isStarted() || server.isRunning())){
            server.start();
        }
    }
    
    public static boolean isRunning(){
        return server.isRunning() || server.isStarted();
    }
    
    public static synchronized void stop() throws Exception{
        server.stop();
    }
}
