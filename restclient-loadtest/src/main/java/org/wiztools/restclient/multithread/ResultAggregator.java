package org.wiztools.restclient.multithread;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author subwiz
 */
class ResultAggregator {
    private int runCount;
    private int failureCount;
    private int errorCount;

    private List<ResultBean> results = new ArrayList<ResultBean>();
    
    private static ResultAggregator _instance = new ResultAggregator();

    private ResultAggregator(){}

    static ResultAggregator getInstance(){
        return _instance;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<ResultBean> getResults() {
        return results;
    }

    public int getRunCount() {
        return runCount;
    }

    public void addRunCount(int k){
        runCount += k;
    }

    public void addFailureCount(int k){
        failureCount += k;
    }

    public void addErrorCount(int k){
        errorCount += k;
    }

    public synchronized void addResultBean(ResultBean result){
        results.add(result);
    }
}
