package org.wiztools.restclient.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.wiztools.restclient.Implementation;
import org.wiztools.restclient.Request;
import org.wiztools.restclient.RequestExecuter;
import org.wiztools.restclient.Response;
import org.wiztools.restclient.TestResult;
import org.wiztools.restclient.View;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.XMLUtil;

/**
 *
 * @author subwiz
 */
class ThreadExecuter extends Thread {

    private List<File> files;

    ThreadExecuter(List<File> files) {
        this.files = files;
    }

    ThreadExecuter(File file) {
        files = new ArrayList<File>();
        files.add(file);
    }

    @Override
    public void run() {
        System.out.println("run(): ");
        for(File f: files){
            try{
                Request request = XMLUtil.getRequestFromXMLFile(f);
                View view = new MultiView(f);
                // Execute:
                System.out.println("execute(): ");
                RequestExecuter executer = Implementation.of(RequestExecuter.class);
                executer.execute(request, view);
            }
            catch(IOException ex){
                ex.printStackTrace(System.err);
            }
            catch(XMLException ex){
                ex.printStackTrace(System.err);
            }
        }
    }

    private static class MultiView implements View{
        final File reqFile;
        Response lastResponse;

        MultiView(final File reqFile){
            this.reqFile = reqFile;
        }

        public void doStart(Request request) {
            System.out.println("Starting: " + reqFile.getAbsolutePath());
        }

        public void doResponse(Response response) {
            lastResponse = response;
            
            TestResult testResult = response.getTestResult();
            if(testResult != null){
                ResultAggregator.getInstance().addFailureCount(testResult.getFailureCount());
                ResultAggregator.getInstance().addErrorCount(testResult.getErrorCount());
                ResultAggregator.getInstance().addRunCount(testResult.getRunCount());
            }

        }

        public void doCancelled() {
            // Cannot cancell in cli mode
        }

        public void doEnd() {
            ResultBean bean = new ResultBean();
            bean.setFile(reqFile);
            bean.setResponse(lastResponse);
            bean.setThreadName(Thread.currentThread().getName());

            ResultAggregator.getInstance().addResultBean(bean);
            System.out.println("End: " + reqFile.getAbsolutePath());
        }

        public void doError(String error) {
            System.err.println("Error:");
            System.err.println(error);
        }

    }

}
