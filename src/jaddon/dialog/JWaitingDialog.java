/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.dialog;

import jaddon.icons.IconPlus;
import jaddon.net.Server;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Paul
 */
public class JWaitingDialog {
    
    public static final String PATHLOADINGSPIN = "/jaddon/icons/loading_spin.gif";
    public static final ImageIcon LOADINGSPIN = IconPlus.getImageIcon(PATHLOADINGSPIN);
    
    private JOptionPane pane = null;
    private Component parentComponent = null;
    private String message = "";
    private String title = "";
    private boolean running = false;
    private final Object[] options = new Object[] {"Cancel"};
    private JDialog dialog = null;
    
    public JWaitingDialog(Component parentComponent, String message, String title) {
        this.parentComponent = parentComponent;
        this.message = message;
        this.title = title;
    }
    
    public void close() {
        try {
            Server.stopThread(thread_showing);
            dialog.setVisible(false);
            dialog.dispose();
        } catch (Exception ex) {
        }
        running = false;
    }
    
    public void createDialog() {
        if(running) {
            return;
        }
        pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, LOADINGSPIN, options, null);
    }
    
    public int showWaitingDialog() {
        if(running) {
            return -1;
        }
        thread_showing.start();
        try {
            thread_showing.join();
        } catch (Exception ex) {
        }
        Object selected_value = pane.getValue();
        if(selected_value == null) {
            return JOptionPane.CLOSED_OPTION;
        } else if(options != null) {
            for(int i = 0; i < options.length; i++) {
                if(options[i].equals(selected_value)) {
                    return i;
                }
            }
        } else if(options == null) {
            if(selected_value instanceof Integer) {
                return ((Integer) selected_value).intValue();
            } else {
                return JOptionPane.CLOSED_OPTION;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

    public JOptionPane getPane() {
        return pane;
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    public JWaitingDialog setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JWaitingDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JWaitingDialog setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    private final Thread thread_showing = new Thread(new Runnable() {
        
        @Override
        public void run() {
            createDialog();
            running = true;
            dialog = pane.createDialog(parentComponent, title);
            dialog.setVisible(true);
            running = false;
        }
        
    });
    
}
