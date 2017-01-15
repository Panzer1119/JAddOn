/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import java.time.Instant;

/**
 *
 * @author Paul
 */
public interface InputProcessor {
    
    public void processInput(Object object, Instant timestamp);
    public void processInput(Object object, Client client, Instant timestamp);
    public void clientLoggedIn(Client client, Instant timestamp);
    public void clientLoggedOut(Client client, Instant timestamp);
    
}
