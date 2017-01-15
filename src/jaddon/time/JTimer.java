/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.time;

import jaddon.controller.StaticStandard;
import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author Paul
 */
public class JTimer {
    
    private Instant start = Instant.now();
    private Instant stop = Instant.now(); 
    private Duration duration = Duration.ZERO;
    private boolean run = false;
    private boolean pause = false;
    
    public JTimer() {
        StaticStandard.setTimer(this);
        reset();
    }
    
    public void start() {
        if(!run && !pause) {
            start = Instant.now();
            run = true;
            pause = false;
        } else if(!run && pause) {
            run = true;
            pause = false;
        } else if(run) {
            System.err.println("Timer already running");
        }
    }
    
    public void stop() {
        stop = Instant.now();
        run = false;
        pause = false;
        calculateDuration();
    }
    
    public void pause() {
        stop = Instant.now();
        run = false;
        pause = true;
        calculateDuration();
    }
    
    public void reset() {
        start = Instant.now();
        stop = Instant.now();
        duration = Duration.ZERO;
        run = false;
        pause = false;
    }
    
    private void calculateDuration() {
        if(run) {
            duration = Duration.between(start, Instant.now());
        } else {
            duration = Duration.between(start, stop);
        }
    }
    
    public boolean isAfter(JTimer timer) {
        return this.getTimeMillis() <  timer.getTimeMillis();
    }
    
    public boolean isBefore(JTimer timer) {
        return this.getTimeMillis() > timer.getTimeMillis();
    }
    
    public Duration getTime() {
        calculateDuration();
        return duration;
    }
    
    public long getTimeMillis() {
        calculateDuration();
        return duration.toMillis();
    }
    
    public long getTimeSeconds() {
        calculateDuration();
        return duration.getSeconds();
    }
    
    public long getTimeNano() {
        calculateDuration();
        return duration.getNano();
    }

    public Instant getInstantStart() {
        return start;
    }

    public void setInstantStart(Instant start) {
        this.start = start;
    }

    public Instant getInstantStop() {
        return stop;
    }

    public void setInstantStop(Instant stop) {
        this.stop = stop;
    }

    public boolean isRun() {
        return run;
    }
    
    public boolean isPause() {
        return pause;
    }
    
}
