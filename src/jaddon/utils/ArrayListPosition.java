/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.time.Duration;

/**
 *
 * @author Paul
 */
public class ArrayListPosition {
    
    private int position = -1;
    private Object object = null;
    private boolean contains = false;
    private Duration duration = Duration.ZERO;
    
    public ArrayListPosition() {
        this(-1, false);
    }
    
    public ArrayListPosition(int position, boolean contains) {
        this.position = position;
        this.contains = contains;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isContains() {
        return contains;
    }

    public void setContains(boolean contains) {
        this.contains = contains;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    @Override
    public String toString() {
        return String.format("Contains: %s, Position: %d, Object: \"%s\"", contains, position, object);
    }
    
}
