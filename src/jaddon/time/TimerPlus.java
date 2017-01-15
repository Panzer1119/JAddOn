/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.time;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public class TimerPlus implements ActionListener {
    
    private Timer timer;
    private int delay = 100;
    private int permilli = 1000;
    public long all = 0;
    public long size = 0;
    private String format = "%.3f per second";
    
    public TimerPlus() {
        this(100);
    }
    
    public TimerPlus(int delay) {
        setDelay(delay);
    }
    
    public void update(String text) {
        //Do something
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            size++;
            double temp = ((all * 1.0) / (size * 1.0)) * ((permilli * 1.0) / (delay * 1.0));
            String text = String.format(format, temp);
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    update(text);
                }

            });
            thread.start();
        } catch (Exception ex) {
            
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        stop();
        timer = new Timer(delay, this);
    }

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getPermilli() {
        return permilli;
    }

    public void setPermilli(int permilli) {
        this.permilli = permilli;
    }
    
    public void reset() {
        size = 0;
        all = 0;
    }
    
    public void start() {
        try {
            reset();
            timer.start();
        } catch (Exception ex) {
            
        }
    }
    
    public void stop() {
        try {
            timer.stop();
        } catch (Exception ex) {
            
        }
    }
    
}
