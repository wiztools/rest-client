package org.wiztools.restclient.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author subwiz
 */
public class Main {
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    
    public static void main(String[] args){
        if(args.length == 0){
            System.err.println("Pass thread count as number in first arguement");
            System.err.println("Pass *.rcq files as second arguement");
            System.exit(-1);
        }

        List<File> files = new ArrayList<File>();
        String threadCountStr = args[0];
        int threadCount = 0;
        
        //getting threadCount:
        if (threadCountStr != null && NUMBER.matcher(threadCountStr).matches()
                && !"0".equals(threadCountStr)){
            threadCount = Integer.parseInt(args[0]);
        }
        else{
            System.err.println("Pass thread count as number in first arguement");
            System.exit(-1);
        }

        //getting filesList:
        for (int i=1; i < args.length; i++) {
            files.add(new File(args[i]));
        }

        //Calling ThreadOrchestrator:
        new ThreadOrchestrator(threadCount, files);

        //Printing Result Summary:
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
            public void run() {
                List<ResultBean> results = ResultAggregator.getInstance().getResults();
                System.out.println("-------Result-------");
                for(ResultBean result: results){
                    System.out.println(result);
                }
                System.out.println("-------------------");
            }
        }));
    }
}
