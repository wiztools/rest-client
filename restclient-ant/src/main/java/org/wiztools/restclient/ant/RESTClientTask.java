package org.wiztools.restclient.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
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
public class RESTClientTask extends Task {

    /*
     * This is the variable storing destination directory where the
     * test output files need to be generated.
     */
    private String destdir;
    private List<FileSet> filesets = new ArrayList<FileSet>();

    private int runCount;
    private int failureCount;
    private int errorCount;

    public String getDestdir() {
        return destdir;
    }

    public void setDestdir(String destdir) {
        this.destdir = destdir;
    }

    @Override
    public void execute(){
        if(destdir == null){
            new BuildException("Mandatory attribute `destdir' missing in task: " + getTaskName());
        }

        // Construct the responseDir:
        File responseDir = new File(destdir);
        if(!responseDir.exists()){
            responseDir.mkdirs();
        }

        if(!responseDir.isDirectory()){
            throw new BuildException("`destdir' attribute points not to a directory.");
        }

        // Execute request for each file:
        try{
            for(FileSet fileSet: filesets){
                DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
                File dir = fileSet.getDir(getProject());
                String[] includedFiles = ds.getIncludedFiles();

                for(String filePath: includedFiles){
                    // This is the request file:
                    File f = new File(dir, filePath);

                    Request request = XMLUtil.getRequestFromXMLFile(f);
                    View view = new RESTClientAntView(f, new File(responseDir, f.getName()));
                    RequestExecuter executer = Implementation.of(RequestExecuter.class);
                    executer.execute(request, view);
                }
            }
            // Print the summary of test results:
            log("============================================================");
            log("**WizTools.org RESTClient Test Results**\n");
            log("Total tests run:    " + runCount);
            log("Total tests failed: " + failureCount);
            log("Total test errors:  " + errorCount);
            log("============================================================");
        }
        catch(IOException ex){
            log(ex, Project.MSG_ERR);
        }
        catch(XMLException ex){
            log(ex, Project.MSG_ERR);
        }
    }

    public void addConfiguredFileset(FileSet fileset){
        filesets.add(fileset);
    }

    private class RESTClientAntView implements View{

        private final File requestFile;
        private final File responseFile;

        public RESTClientAntView(File request, File response){
            requestFile = request;
            responseFile = response;
        }

        public void doStart(Request request) {
            log("RESTClient starting: " + requestFile.getAbsolutePath());
        }

        public void doResponse(Response response) {
            // 1. Update the test count values:
            TestResult testResult = response.getTestResult();
            runCount += testResult.getRunCount();
            failureCount += testResult.getFailureCount();
            errorCount += testResult.getErrorCount();

            // 2. Write response:
            try{
                XMLUtil.writeResponseXML(response, responseFile);
            }
            catch(IOException ex){
                log(ex, Project.MSG_ERR);
            }
            catch(XMLException ex){
                log(ex, Project.MSG_ERR);
            }
        }

        public void doCancelled() {
            // Do nothing!
        }

        public void doEnd() {
            log("RESTClient ended: " + requestFile.getAbsolutePath());
        }

        public void doError(String error) {
            log(error, Project.MSG_WARN);
        }
    }
}
