package jaddon.utils;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>A class wich can flash your window's taskbar icon, if it is not focused.</p>
 * <p>Works great on Windows and Linux too.
 * (I tried it on Windows XP,Windows 7,KDE 4.)</p>
 **/
public class WindowFlasher {

    private Dialog d;
    private Window w;

    /** Creates a window flasher object,
     *  wich can flash the window's taskbar icon.
     * @param window Window Window
     **/
    public WindowFlasher(Window window) {
        w = window;
        d = new Dialog(w);
        d.setUndecorated(true);
        d.setSize(0, 0);
        d.setModal(false);
        d.addWindowFocusListener(new WindowAdapter() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                w.requestFocus();
                d.setVisible(false);
                super.windowGainedFocus(e);
            }

        });
        w.addWindowFocusListener(new WindowAdapter() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                d.setVisible(false);
                super.windowGainedFocus(e);
            }
        });
    }

    /** It flashes the window's taskbar icon if the window is not focused. 
     *  The flashing "stops" when the window becomes focused.
     **/
    public void flash() {
        if (!w.isFocused()) {
            if (d.isVisible()) {
                d.setVisible(false);
            }
            d.setLocation(0, 0);
            d.setLocationRelativeTo(w);
            d.setVisible(true);
        }
    }
}