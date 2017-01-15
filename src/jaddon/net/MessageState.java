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
public enum MessageState implements Serializable {
    
    LOGIN, LOGOUT, BROADCAST, ANSWERYES, ANSWERNO, PING, AREYOUALIVE, IMALIVE
    
}
