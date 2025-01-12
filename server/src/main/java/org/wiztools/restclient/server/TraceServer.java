package org.wiztools.restclient.server;

import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author subwiz
 */
public class TraceServer {

    private static final Logger LOG = Logger.getLogger(TraceServer.class.getName());

    public static final String SYS_PROPERTY_PORT = "rc:trace-server-port";
    public static final int DEFAULT_PORT = 10101;

    public static final int PORT;
    private static final Server server;
    
    static{
        // Set the port
        int port = 0;
        try{
            String t = System.getProperty(SYS_PROPERTY_PORT);
            if(t != null){
                int t_port = Integer.parseInt(t);
                if(t_port > 65535 || t_port < 0){
                    LOG.warning(SYS_PROPERTY_PORT
                            + " is not in valid port range. Reverting to default:"
                            + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
                else{
                    port = t_port;
                }
            }
            else{ // System property not supplied, use default port
                port = DEFAULT_PORT;
            }
        }
        catch(NumberFormatException ex){
            LOG.warning(SYS_PROPERTY_PORT
                    + " is not a number. Reverting to default: "
                    + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
        PORT = port;

        // Create the server object
        server = new Server(PORT);

        // Attach the trace servlet
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        server.setHandler(ctx);
        
        ctx.addServlet(new ServletHolder(new TraceServlet()), "/*");
        
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
