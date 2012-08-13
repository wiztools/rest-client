package org.wiztools.restclient.cli;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.wiztools.restclient.View;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.RequestExecuter;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.XMLUtil;
import org.wiztools.restclient.bean.TestResult;
import org.wiztools.restclient.*;

/**
 *
 * @author subwiz
 */
public class CliMain {

    private static int runCount;
    private static int failureCount;
    private static int errorCount;

    private static class CliCommand{
        @Argument(value = "output", alias = "o", description = "This is the output file", required = true)
        private File outDir;
    }

    private static class CliView implements View{
        final File outDir;
        final File reqFile;

        CliView(final File outDir, final File reqFile){
            this.outDir = outDir;
            this.reqFile = reqFile;
        }

        public void doStart(Request request) {
            System.out.println("Starting: " + reqFile.getAbsolutePath());
        }

        public void doResponse(Response response) {
            String reqFileName = reqFile.getName();
            String outFilePrefix = null;
            if(reqFileName.endsWith(".rcq")){
                outFilePrefix = reqFileName.replaceAll(".rcq", "");
            }
            else{
                outFilePrefix = reqFileName;
            }
            try{
                // Generate the response file:
                // Add response extension: .rcs
                File resFile = new File(outDir, outFilePrefix + ".rcs");
                if(resFile.exists()){
                    System.err.println("Response file exists: " + resFile.getAbsolutePath());
                    for(int i = 0; i< Integer.MAX_VALUE; i++){
                        resFile = new File(outDir, outFilePrefix + "_" + i + ".rcs");
                        if(!resFile.exists()){
                            System.err.println("Using alternative: " + resFile.getAbsolutePath());
                            break;
                        }
                    }
                }
                TestResult testResult = response.getTestResult();
                if(testResult != null){
                    failureCount += testResult.getFailureCount();
                    errorCount += testResult.getErrorCount();
                    runCount += testResult.getRunCount();
                }
                XMLUtil.writeResponseXML(response, resFile);
            }
            catch(IOException ex){
                ex.printStackTrace(System.err);
            }
            catch(XMLException ex){
                ex.printStackTrace(System.err);
            }
        }

        public void doCancelled() {
            // Cannot cancell in cli mode
        }

        public void doEnd() {
            System.out.println("End: " + reqFile.getAbsolutePath());
        }

        public void doError(String error) {
            System.err.println("Error:");
            System.err.println(error);
        }

    }

    public static void main(String[] arg){
        CliCommand command = new CliCommand();
        List<String> params = null;
        try{
            params = Args.parse(command, arg);
        }
        catch(IllegalArgumentException ex){
            System.err.println(ex.getMessage());
            Args.usage(command);
            System.exit(1);
        }
        if(params.size() == 0){
            System.err.println("No request(s) given as parameter.");
            System.exit(1);
        }
        File outDir = command.outDir;
        List<String> errors = new ArrayList<String>();
        if(!outDir.isDirectory()){
            errors.add("Out directory is not a directory: " + outDir.getAbsolutePath());
        }
        else if(!outDir.canWrite()){
            errors.add("Cannot write in out dir: " + outDir.getAbsolutePath());
        }
        else{
            for(String param: params){
                File f = new File(param);
                if(f.canRead()){
                    try{
                        Request request = XMLUtil.getRequestFromXMLFile(f);
                        View view = new CliView(outDir, f);
                        // Execute:
                        RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
                        executer.execute(request, view);
                    }
                    catch(IOException ex){
                        ex.printStackTrace(System.err);
                    }
                    catch(XMLException ex){
                        ex.printStackTrace(System.err);
                    }
                }
                else{
                    System.err.println("No read access: " + f.getAbsolutePath());
                }
            }
            // Print summary of tests:
            if(runCount > 0){
                System.out.println();
                System.out.println("Total tests executed: " + runCount);
                System.out.println("Total failures:       " + failureCount);
                System.out.println("Total errors:         " + errorCount);
            }
        }
        if(errors.size() > 0){
            System.err.println("There were errors: ");
            for(String error: errors){
                System.err.println("--> " + error);
            }
            System.exit(2);
        }
    }
}
