/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlog;

import jaddon.controller.StaticStandard;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Paul
 */
public class SystemInputStream {
    
    private InputStream inputstream = null;
    private Scanner scanner = null;
    private JLogger logger = null;
    private final Thread thread = new Thread(new Runnable() {
        
        @Override
        public void run() {
            try {
                while(scanner != null && scanner.hasNextLine()) {
                    try {
                        logger.sendCommand(scanner.nextLine());
                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
            }
            StaticStandard.log("SystemInputSteam closed");
        }
        
    });
    
    public SystemInputStream(InputStream inputstream, JLogger logger) {
        this.inputstream = inputstream;
        this.logger = logger;
        updateScanner();
    }
    
    public void updateScanner() {
        try {
            stop();
            scanner = new Scanner(inputstream);
            thread.start();
        } catch (Exception ex) {
            StaticStandard.logErr("Error while updating scanner: " + ex, ex);
        }
    }
    
    public void stop() {
        while(thread.isAlive()) {
            try {
                thread.stop();
            } catch (Exception ex) {
            }
        }
    }
    
}
