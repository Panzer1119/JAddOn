/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import java.io.Serializable;

/**
 *
 * @author Paul
 */
public class ComMessage implements Serializable {
    
    private Object message = null;
    
    public ComMessage(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
    
}
