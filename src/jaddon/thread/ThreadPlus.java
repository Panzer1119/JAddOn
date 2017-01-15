/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.thread;

/**
 *
 * @author Paul
 */
public class ThreadPlus extends Thread {
    
    private boolean await = false;
    private int sleep_time = 10;
    
    public ThreadPlus() {
        super();
    }
    
    public ThreadPlus(Runnable runnable) {
        super(runnable);
    }
    
    public void await() throws InterruptedException {
        await = true;
        while(await) {
            Thread.sleep(sleep_time);
        }
    }
    
    public void wake() {
        await = false;
    }

    public boolean isAwait() {
        return await;
    }

    public int getSleepTime() {
        return sleep_time;
    }

    public void setSleepTime(int sleep_time) {
        this.sleep_time = sleep_time;
    }
    
}
