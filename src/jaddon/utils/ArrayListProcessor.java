package jaddon.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Paul Hagedorn
 * @param <T> Data Type
 */
public class ArrayListProcessor<T> {
    
    public static final int MINARRAYLISTSIZE = 0;
    
    ArrayList<T> arraylist = null;
    
    /**
     * EXAMPLE:
     * This will create an ArrayListProcessor (which doesnt get saved)
     * and will process the complete arraylist immediately (instant)
     * 
            new ArrayListProcessor() {

                //@Override //Uncomment this!
                public Object process(Object object) {
                    //Do something with the object
                    return object;
                }

            }.setArrayList( YOUR ARRAYLIST (DOESNT MATTER WHICH TYPE) ).process( INT MAXIMUM OF THREADS );
     * 
     */
    public ArrayListProcessor() {
        
    }
    
    /**
     * EXAMPLE:
     * This will create an ArrayListProcessor (which doesnt get saved)
     * and will process the complete arraylist immediately (instant)
     * 
            new ArrayListProcessor( YOUR ARRAYLIST (DOESNT MATTER WHICH TYPE) ) {

                //@Override //Uncomment this!
                public Object process(Object object) {
                    //Do something with the object
                    return object;
                }

            }.process( INT MAXIMUM OF THREADS );
     * 
     * @param arraylist ArrayList to be processed
     */
    public ArrayListProcessor(ArrayList<T> arraylist) {
        this.arraylist = arraylist;
    }
    
    /**
     * To be overridden
     * @param object Object to be processed
     * @return Object object which got processed
     */
    public Object process(Object object) {
        return object;
    }
    
    /**
     * Processes the arraylist from this object
     * @param threadPoolSize Integer maximum of threads
     */
    public final void process(final int threadPoolSize) {
        process(arraylist, threadPoolSize);
    }
    
    /**
     * Processes the arraylist from this object
     * @param arraylist ArrayList to be processed
     * @param threadPoolSize Integer maximum of threads
     */
    public final void process(final ArrayList<?> arraylist, final int threadPoolSize) {
        processPrivate((ArrayList<Object>) arraylist, threadPoolSize);
    }
    
    /**
     * Just the run method
     * @param arraylist
     * @param threadPoolSize 
     */
    private final void processPrivate(final ArrayList<Object> arraylist, final int threadPoolSize) {
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

    public ArrayList<T> getArrayList() {
        return arraylist;
    }

    public void setArrayList(ArrayList<T> arraylist) {
        this.arraylist = arraylist;
    }
    
}
