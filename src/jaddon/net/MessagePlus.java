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
public class MessagePlus implements Serializable {
    
    private Object message = null;
    private MessageState state = null;
    
    public MessagePlus() {
        this(null, null);
    }
    
    public MessagePlus(Object message, MessageState state) {
        setMessage(message);
        setState(state);
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }
    
}
