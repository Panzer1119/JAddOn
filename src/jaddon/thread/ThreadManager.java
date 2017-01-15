/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.thread;

import jaddon.controller.StaticStandard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Paul
 */
public class ThreadManager {
    
    public static final int MAXIMUMTHREADS = 1000;
    
    private final ArrayList<Object> objects = new ArrayList<>();
    public final ArrayList<Thread> threads = new ArrayList<>();
    private int max_threads = 1;
    private int active_threads = 0;
    private int active_threads_real = 0;
    private int sleep_time = 1;
    private boolean isRunning = false;
    
    public ThreadManager() {
        
    }
    
    public ThreadManager(ArrayList<Object> objects) {
        setObjects(objects);
    }
    
    public final ArrayList<Object> runProcess() {
        isRunning = true;
        resetThreads();
        final ArrayList<Object> output = new ArrayList<>();
//        final ExecutorService executor = Executors.newFixedThreadPool(max_threads);
        for(Object o : objects) {
            Runnable run = new Runnable() {
                
                @Override
                public void run() {
                    active_threads_real++;
                    try {
                        output.add(process(o));
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while processing object: " + ex, ex);
                    }
                    active_threads--;
                    active_threads_real--;
                    StaticStandard.log("Thread finished");
                }
                
            };
            //executor.execute(run);
            Thread thread = new Thread(run);
            threads.add(thread);
            while(active_threads >= max_threads) {
                try {
                    Thread.sleep(sleep_time);
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while sleeping: " + ex, ex);
                }
            }
            active_threads++;
            thread.start();
        }
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//        System.out.println("Finished all threads");
        /*        
        while(!isAlive()) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                StaticStandard.logErr("Error while sleeping: " + ex, ex);
            }
        }
        */
        /*
        boolean done = false;
        while(!done) {
            done = output.size() == objects.size();
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
                StaticStandard.logErr("Error while sleeping: " + ex, ex);
            }
        }
        */
        resetThreads();
        isRunning = false;
        return output;
    }
    
    private boolean isAlive() {
        final ArrayList<Thread> threads = new ArrayList<>();
        for(Thread thread : this.threads) {
            if(thread.isAlive()) {
                threads.add(thread);
            }
        }
        this.threads.clear();
        for(Thread thread : threads) {
            this.threads.add(thread);
        }
        return !threads.isEmpty();
    }
    
    public final void runProcessInThread() {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                runProcess();
            }
            
        });
        thread.start();
    }
    
    public Object process(Object o) {
        return o;
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }
    
    public void setObjects(ArrayList<Object> objects) {
        this.objects.clear();
        for(Object o : objects) {
            this.objects.add(o);
        }
    }
    
    public void setObjects(Object[] objects) {
        this.objects.clear();
        for(Object o : objects) {
            this.objects.add(o);
        }
    }
    
    public int getMaxThreads() {
        return max_threads;
    }
    
    public void setMaxThreads(int max_threads) {
        if(MAXIMUMTHREADS >= max_threads) {
            this.max_threads = max_threads;
        }
    }
    
    public final void resetThreads() {
        for(Thread thread : threads) {
            while(thread.isAlive()) {
                try {
                    Thread.sleep(1);
                } catch (Exception ex) {
                    
                }
            }
            try {
                thread.interrupt();
                thread.stop();
            } catch (Exception ex) {
                StaticStandard.logErr("Error while resetting thread: " + ex, ex);
            }
        }
        threads.clear();
        active_threads = 0;
        active_threads_real = 0;
    }

    public int getSleepTime() {
        return sleep_time;
    }

    public void setSleepTime(int sleep_time) {
        this.sleep_time = sleep_time;
    }
    
    public boolean isRunning() {
        return isRunning;
    }

    public int getActiveThreads() {
        return active_threads;
    }

    public int getActiveThreadsReal() {
        return active_threads_real;
    }
    
}
