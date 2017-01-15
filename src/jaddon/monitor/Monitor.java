/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public class Monitor implements ActionListener {
    
    private Timer timer = null;
    private Instant instant_last = Instant.now();
    private Object object = null;
    
    public Monitor() {
        this(100);
    }
    
    public Monitor(int delay) {
        setTimer(delay);
    }
    
    public Object monitor() {
        return null;
    }
    
    public void update(Object oldObject, Instant instant_last, Object newObject, Instant instant_now) {
        //Do something
    }
    
    private void updateInner() {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                Instant instant_now = Instant.now();
                Object object_temp = monitor();
                Object object_old = object;
                if(object_temp != object_old) {
                    //New
                }
                object = object_temp;
                update(object_old, instant_last, object_temp, instant_now);
                instant_last = instant_now;
            }
            
        });
        thread.start();
    }
    
    public void setTimer(int delay) {
        if(delay < 0 || delay > 60000) {
            delay = 100;
        }
        timer = new Timer(delay, this);
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            updateInner();
        }
    }
    
}
