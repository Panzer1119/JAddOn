/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jtree;

import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class TreeArrayList<V> extends ArrayList<V> {
    
    public Object object = null;
    
    public TreeArrayList() {
        this(null);
    }
    
    public TreeArrayList(Object object) {
        this(object, null);
    }
    
    public TreeArrayList(Object object, ArrayList<V> arraylist) {
        super();
        this.object = object;
        if(arraylist != null) {
            for(V v : arraylist) {
                this.add(v);
            }
        }
    }
    
    public ArrayList<V> toArrayList() {
        ArrayList<V> arraylist = new ArrayList<>();
        for(V v : this) {
            arraylist.add(v);
        }
        return arraylist;
    }
    
    public void addArrayList(ArrayList<V> data) {
        if(data != null) {
            for(V v : data) {
                add(v);
            }
        }
    }
    
}
