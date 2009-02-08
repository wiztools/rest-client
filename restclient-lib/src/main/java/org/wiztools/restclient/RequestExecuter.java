package org.wiztools.restclient;

/**
 * This is the interface used to execute the HTTP request. For getting the
 * default implementation for this interface, use the Implementation class:
 *
 * <pre>
 * import org.wiztools.restclient.RequestExecuter;
 * import org.wiztools.restclient.Implementation;
 *
 * RequestExecuter executer = Implementation.of(RequestExecuter.class);
 * </pre>
 * @author subwiz
 */
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
     * import org.wiztools.restclient.Request;
     * import org.wiztools.restclient.View;
     * import org.wiztools.restclient.RequestExecuter;
     * import org.wiztools.restclient.Implementation;
     *
     * ...
     * 
     * final Request request = ...;
     * final View view = ...;
     * final RequestExecuter executer = Implementation.of(RequestExecuter.class);
     * Thread t = new Thread(){
     *      @Override
     *      public void run(){
     *          executer.execute(request, view);
     *      }
     *      @Override
     *      public void interrupt(){
     *          executer.abortExecution();
     *          super.interrupt();
     *      }
     * }
     * t.start();
     *
     * // to interrupt in later stage:
     * t.interrupt();
     * </pre>
     */
    void abortExecution();
}
