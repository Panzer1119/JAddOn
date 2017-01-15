/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.thread;

import jaddon.controller.StaticStandard;
import jaddon.utils.ArrayListProcess;
import jaddon.utils.ArrayListProcessMethod;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class ThreadManagerOld extends Thread {
    
    private ArrayList<Thread> threads = new ArrayList<>();
    private int runtime_per_thread = 10;
    private boolean done = false;
    private boolean alive = false;
    
    public ThreadManagerOld() {
        
    }
    
    public ThreadManagerOld(ArrayList<Thread> threads) {
        this.threads = threads;
    }
    
    @Override
    public synchronized void run() {
        done = true;
        for(Thread t : threads) {
            if(t.isAlive()) {
                done = false;
                break;
            }
        }
        threads = new ArrayListProcess(threads, new ArrayListProcessMethod<Thread>() {

            @Override
            public Thread process(Thread t) {
                t.start();
                try {
                    t.wait();
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while waiting for thread (start): " + ex);
                }
                return t;
            }

        }).run();
        StaticStandard.log("Aha");
        while(!done) {
            alive = false;
            for(Thread t : threads) {
                if(t.isAlive()) {
                    done = false;
                    alive = true;
                } else {
                    continue;
                }
                t.notify();
                try {
                    Thread.sleep(runtime_per_thread);
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while waiting the runtime: " + ex);
                }
                try {
                    t.wait();
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while waiting for thread: " + ex);
                }
            }
            if(!alive) {
                done = true;
            }
        }
    }

    public ArrayList<Thread> getThreads() {
        return threads;
    }

    public void setThreads(ArrayList<Thread> threads) {
        this.threads = threads;
    }
    
    public int getRuntimePerThread() {
        return runtime_per_thread;
    }

    public void setRuntimePerThread(int runtime_per_thread) {
        this.runtime_per_thread = runtime_per_thread;
    }
    
    public boolean isDone() {
        return done;
    }
    
    public boolean isAnyThreadAlive() {
        return alive;
    }
    
}
