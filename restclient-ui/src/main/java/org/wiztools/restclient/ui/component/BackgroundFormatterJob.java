package org.wiztools.restclient.ui.component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 *
 * @author subhash
 */
public class BackgroundFormatterJob {
    private final ExecutorService formatterThreadPool = Executors.newSingleThreadExecutor();
    private Future formatterFuture;
    
    public void run(Runnable r,
            final BodyPopupMenuListener listener,
            boolean isSeparateThread) {
        if(isSeparateThread) {
            if(formatterFuture != null && !formatterFuture.isDone()) {
                listener.onMessage("Last formatter job running!");
                return;
            }
            listener.onMessage("Starting formatter job...");
            new Thread() {

                @Override
                public void run() {
                    while(true) {
                        // Sleep:
                        try {
                            TimeUnit.SECONDS.sleep(30);
                        }
                        catch(InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        // Feedback to user:
                        if(formatterFuture != null && !formatterFuture.isDone()) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onMessage("Still running formatter job...");
                                }
                            });
                        }
                        else {
                            break;
                        }
                    }
                }
                
            }.start();
            
            formatterFuture = formatterThreadPool.submit(r);
        }
        else {
            r.run();
        }
    }
    
    public void cancelRunningJob() {
        if(formatterFuture != null && !formatterFuture.isDone()) {
            formatterFuture.cancel(true);
        }
    }
}
