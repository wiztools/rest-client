package org.wiztools.restclient.bean;

import org.wiztools.restclient.ImplementedBy;
import org.wiztools.restclient.HTTPClientRequestExecuter;
import org.wiztools.restclient.View;

/**
 * This is the interface used to execute the HTTP request. For getting the
 * default implementation for this interface, use the Implementation class:
 *
 * <pre>
 * import org.wiztools.restclient.RequestExecuter;
 * import org.wiztools.restclient.Implementation;
 * </pre>
 * @author subwiz
 */
@ImplementedBy(HTTPClientRequestExecuter.class)
public interface RequestExecuter {

    /**
     * Use this method to execute the HTTP request.
     * @param request The request object.
     * @param views This is a vararg parameter. You may pass any number of View
     * implementation.
     */
    void execute(Request request, View ... views);

    /**
     * Use this method to abort a request in progress. The recommended way to
     * use this:
     *
     * <pre>
     *
     * import org.wiztools.restclient.Request;
     * import org.wiztools.restclient.View;
     * import org.wiztools.restclient.RequestExecuter;
     * import org.wiztools.restclient.ServiceLocator;
     *
     * ...
     *
     * final Request request = ...;
     * final View view = ...;
     * final RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
     * Thread t = new Thread(){
     *      {@literal @}Override
     *      public void run(){
     *          executer.execute(request, view);
     *      }
     *      {@literal @}Override
     *      public void interrupt(){
     *          executer.abortExecution();
     *          super.interrupt();
     *      }
     * }
     * t.start();
     *
     * // to interrupt in later stage:
     * t.interrupt();
     *
     * </pre>
     */
    void abortExecution();
}
