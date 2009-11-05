package org.wiztools.restclient.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author subwiz
 */
class ThreadOrchestrator {

    ThreadOrchestrator(int threadCount, List<File> files) {
        int fileCount = files.size();
        System.out.println("thread count =>"+threadCount);
        System.out.println("file count =>"+fileCount);
        if(threadCount < fileCount){
            // Each thread executes x number of files:
            System.out.println("--- Each thread executes "+threadCount+" number of files ---");
            // Split the files:
            int i = 0;
            List<File> splitFiles = new ArrayList<File>();
            for(File f: files){
                if(i < threadCount){
                    splitFiles.add(f);
                }
                else if(i == threadCount){
                    i = 0;
                    System.out.println("calling new thread:");
                    new ThreadExecuter(splitFiles).start();
                    // start creating the next batch:
                    splitFiles = new ArrayList<File>();
                }
                i++;
            }
        }
        else{
            //Each file will be executed by x threads:
            System.out.println("--- Each file will be executed by "+threadCount+" threads ---");
            for(File f: files){
                System.out.println(f.getName() + " is going to be executed by "+threadCount+" threads");
                for(int i=0; i<threadCount; i++ ){
                    new ThreadExecuter(f).start();
                }
            }
        }
    }
    
}
