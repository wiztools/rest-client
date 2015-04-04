package org.wiztools.restclient.cli;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.*;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.RequestExecuter;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.TestResult;
import org.wiztools.restclient.persistence.PersistenceException;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.PersistenceWrite;
import org.wiztools.restclient.persistence.XmlPersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;

/**
 *
 * @author subwiz
 */
public class CliMain {

    private static int runCount;
    private static int failureCount;
    private static int errorCount;

    private static class CliCommand{
        @Argument(value = "output",
                alias = "o",
                description = "This is the output file",
                required = true)
        private File outDir;
        
        @Argument(value = "save-response-body",
                alias = "b",
                description = "Save response body instead of full response",
                required = false)
        private boolean saveResponseBody = false;
    }

    private static class CliView implements View{
        final File outDir;
        final File reqFile;
        final boolean saveResponseBody;

        CliView(final File outDir, final File reqFile, final boolean saveResponseBody){
            this.outDir = outDir;
            this.reqFile = reqFile;
            this.saveResponseBody = saveResponseBody;
        }

        @Override
        public void doStart(Request request) {
            System.out.println("Starting: " + reqFile.getAbsolutePath());
        }

        @Override
        public void doResponse(Response response) {
            String reqFileName = reqFile.getName();
            final String outFilePrefix = reqFileName.endsWith(".rcq")
                    ? reqFileName.replaceAll(".rcq", ""): reqFileName;
            
            try{
                // Generate the response file:
                // Add response extension:
                final String ext = this.saveResponseBody? ".body": ".rcs";
                final File resFile = new NonExistFileGenerator(outDir, outFilePrefix, ext).getFile();
                
                // Test:
                TestResult testResult = response.getTestResult();
                if(testResult != null){
                    failureCount += testResult.getFailureCount();
                    errorCount += testResult.getErrorCount();
                    runCount += testResult.getRunCount();
                }
                if(this.saveResponseBody) {
                    FileUtil.writeBytes(resFile, response.getResponseBody());
                }
                else {
                    PersistenceWrite p = new XmlPersistenceWrite();
                    p.writeResponse(response, resFile);
                }
            }
            catch(IOException | PersistenceException ex){
                ex.printStackTrace(System.err);
            }
        }

        @Override
        public void doCancelled() {
            // Cannot cancell in cli mode
        }

        @Override
        public void doEnd() {
            System.out.println("End: " + reqFile.getAbsolutePath());
        }

        @Override
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
        if(params.isEmpty()){
            System.err.println("No request(s) given as parameter.");
            System.exit(1);
        }
        File outDir = command.outDir;
        List<String> errors = new ArrayList<>();
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
                    try {
                        PersistenceRead p = new XmlPersistenceRead();
                        Request request = p.getRequestFromFile(f);
                        View view = new CliView(outDir, f, command.saveResponseBody);
                        // Execute:
                        RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
                        executer.execute(request, view);
                    }
                    catch(IOException | PersistenceException ex){
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
