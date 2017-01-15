/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Paul
 * @param <T> Data Type
 */
public class ArrayListProcess<T> {
    
    public static final int MINARRAYLISTSIZE = 0;
    
    ArrayList<T> arraylist = null;
    
    @Deprecated
    ArrayListProcessMethod<T> alpm = null;
    
    public ArrayListProcess() {
        
    }
    
    public ArrayListProcess(ArrayList<T> arraylist) {
        this.arraylist = arraylist;
    }
    
    @Deprecated
    public ArrayListProcess(ArrayList<T> arraylist, ArrayListProcessMethod alpm) {
        this.arraylist = new ArrayList<>(arraylist);
        this.alpm = alpm;
    }
    
    @Deprecated
    public final ArrayList<T> run() {
        for(int i = 0; i < arraylist.size(); i++) {
            arraylist.set(i, alpm.process(arraylist.get(i)));
        }
        return arraylist;
    }
    
    /**
     * To be overridden
     * @param object object to get processed
     * @return Object processed Object
     */
    public Object process(Object object) {
        return object;
    }
    
    /*  
        Example
        ArrayList<String> test = new ArrayList<>();
        test = new ArrayListProcess(test, new ArrayListProcessMethod<String>() {

            @Override
            public String process(String g) {
                return g;
            }

        }).run();
     */
    
    public final void process(final int threadPoolSize) {
        process((ArrayList<Object>) arraylist, threadPoolSize);
    }
    
    public final void process(final ArrayList<Object> arraylist, final int threadPoolSize) {
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        if(arraylist.size() >= threadPoolSize && arraylist.size() >= MINARRAYLISTSIZE) {
            final int steps = arraylist.size() / threadPoolSize;
            for(int i = 0; i < threadPoolSize; i++) {
                final int u = i;
                Runnable run = new Runnable() {
                    
                    @Override
                    public void run() {
                        final int extra = ((u == threadPoolSize - 1) ? (steps * threadPoolSize) - arraylist.size() : 0);
                        for(int z = 0; z < steps + extra; z++) {
                            final int pos = (u * steps + z);
                            arraylist.set(pos, process(arraylist.get(pos)));
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
        } else {
            for(int i = 0; i < arraylist.size(); i++) {
                arraylist.set(i, process(arraylist.get(i)));
            }
        }
    }
}
