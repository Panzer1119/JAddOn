/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import jaddon.time.TimerPlus;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Paul
 */
public class ArrayListUtils {
    
    public static final int MINARRAYLISTSIZE = 10000;
    
    public static ArrayListPosition contains(final ArrayList<?> arraylist, final Object object, final int threadPoolSize) {
        final Instant instant_start = Instant.now();
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        final ArrayListPosition alp = new ArrayListPosition();
        if(arraylist.size() >= threadPoolSize && arraylist.size() >= MINARRAYLISTSIZE) {
            final int steps = arraylist.size() / threadPoolSize;
            for(int i = 0; i < threadPoolSize; i++) {
                final int u = i;
                Runnable run = new Runnable() {
                    
                    @Override
                    public void run() {
                        final int extra = ((u == threadPoolSize - 1) ? (steps * threadPoolSize) - arraylist.size() : 0);
                        for(int z = 0; z < steps + extra; z++) {
                            if(arraylist.get(u * steps + z).equals(object)) {
                                final Instant instant_stop = Instant.now();
                                final Duration duration = Duration.between(instant_start, instant_stop);
                                alp.setContains(true);
                                alp.setObject(object);
                                alp.setPosition((u * steps + z));
                                alp.setDuration(duration);
                                executor.shutdownNow();
                            }
                            //StaticStandard.log(String.format("[%s] Step: %d, Overall: %d, Percentage: %.2f", Thread.currentThread().getName(), z, (u * steps + z), ((z * 1.0) / (steps * 1.0) * 100.0)));
                        }
                    }
                    
                };
                executor.execute(run);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (Exception ex) {
            }
            return alp;
        } else {
            alp.setContains(arraylist.contains(object));
            alp.setObject(alp.isContains() ? object : null);
            alp.setPosition(alp.isContains() ? arraylist.indexOf(object) : -1);
            return alp;
        }
    }
    
    public static ArrayListPosition contains(final ArrayList<?> arraylist, final Object object, final int threadPoolSize, TimerPlus timer) {
        final Instant instant_start = Instant.now();
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        final ArrayListPosition alp = new ArrayListPosition();
        if(arraylist.size() >= threadPoolSize && arraylist.size() >= MINARRAYLISTSIZE) {
            final int steps = arraylist.size() / threadPoolSize;
            for(int i = 0; i < threadPoolSize; i++) {
                final int u = i;
                Runnable run = new Runnable() {
                    
                    @Override
                    public void run() {
                        final int extra = ((u == threadPoolSize - 1) ? (steps * threadPoolSize) - arraylist.size() : 0);
                        for(int z = 0; z < steps + extra; z++) {
                            timer.all++;
                            if(arraylist.get(u * steps + z).equals(object)) {
                                final Instant instant_stop = Instant.now();
                                final Duration duration = Duration.between(instant_start, instant_stop);
                                alp.setContains(true);
                                alp.setObject(object);
                                alp.setPosition((u * steps + z));
                                alp.setDuration(duration);
                                executor.shutdownNow();
                            }
                            //StaticStandard.log(String.format("[%s] Step: %d, Overall: %d, Percentage: %.2f", Thread.currentThread().getName(), z, (u * steps + z), ((z * 1.0) / (steps * 1.0) * 100.0)));
                        }
                    }
                    
                };
                executor.execute(run);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (Exception ex) {
            }
            return alp;
        } else {
            alp.setContains(arraylist.contains(object));
            alp.setObject(alp.isContains() ? object : null);
            alp.setPosition(alp.isContains() ? arraylist.indexOf(object) : -1);
            return alp;
        }
    }
    
}
