/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.dialog;

import jaddon.icons.IconPlus;
import jaddon.net.Server;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Paul
 */
public class JWaitingDialog {
    
    private static final String PATHLOADINGDEFAULT = "/jaddon/icons/loading/loading_%s_%%dpx.gif";
    
    public static enum Loading {
        DEFAULT,
        GEAR,
        GEARS,
        GPS,
        HOURGLASS,
        RELOAD,
        RINGALT,
        RIPPLE,
        ROLLING,
        SPIN,
        SQUARES
    }
    
    public static final int SIZESMALL = 64;
    public static final int SIZESTANDARD = 96;
    public static final int SIZEBIG = 128;
    
    public static final int RUNNING_OPTION = -2;
    public static final int CLOSED_OPTION = -1;
    public static final int CANCEL_OPTION = 2;
    public static final int STOPPED_OPTION = 3;
    
    private JOptionPane pane = null;
    private Component parentComponent = null;
    private String message = "";
    private String title = "";
    private boolean running = false;
    private boolean stopped = false;
    private final Object[] options = new Object[] {"Cancel"};
    private JDialog dialog = null;
    private int defaultCloseOperation = JDialog.HIDE_ON_CLOSE;
    private int loadingIconSize = SIZESTANDARD;
    private Loading loadingIcon = Loading.SPIN;
    private IconPlus iconplus = null;
    
    public JWaitingDialog(Component parentComponent, String message, String title) {
        this.parentComponent = parentComponent;
        this.message = message;
        this.title = title;
        reloadIcon();
    }
    
    public void close() {
        try {
            stopped = true;
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
        pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, iconplus.getImageIcon(), options, null);
    }
    
    public int showWaitingDialog() {
        if(running) {
            return RUNNING_OPTION;
        }
        stopped = false;
        thread_showing.start();
        try {
            thread_showing.join();
        } catch (Exception ex) {
        }
        if(stopped) {
            return STOPPED_OPTION;
        }
        Object selected_value = pane.getValue();
        if(selected_value == null) {
            return CLOSED_OPTION;
        } else if(options != null) {
            if(options.length > 0 && options[0].equals(selected_value)) {
                return CANCEL_OPTION;
            } else {
                for(int i = 0; i < options.length; i++) {
                    if(options[i].equals(selected_value)) {
                        return i;
                    }
                }
            }
        } else if(options == null) {
            if(selected_value instanceof Integer) {
                return ((Integer) selected_value).intValue();
            } else {
                return CLOSED_OPTION;
            }
        }
        return CLOSED_OPTION;
    }
    
    public final void reloadIcon() {
        iconplus = new IconPlus(String.format(PATHLOADINGDEFAULT, loadingIcon.toString().toLowerCase()), loadingIconSize);
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

    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    public JWaitingDialog setDefaultCloseOperation(int defaultCloseOperation) {
        this.defaultCloseOperation = defaultCloseOperation;
        return this;
    }

    public int getLoadingIconSize() {
        return loadingIconSize;
    }

    public JWaitingDialog setLoadingIconSize(int loadingIconSize) {
        this.loadingIconSize = loadingIconSize;
        iconplus.setSize(loadingIconSize);
        return this;
    }

    public Loading getLoadingIcon() {
        return loadingIcon;
    }

    public JWaitingDialog setLoadingIcon(Loading loadingIcon) {
        this.loadingIcon = loadingIcon;
        reloadIcon();
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
            dialog.setDefaultCloseOperation(defaultCloseOperation);
            dialog.setVisible(true);
            running = false;
        }
        
    });
    
}
