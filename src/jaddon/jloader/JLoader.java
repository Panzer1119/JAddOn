/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jloader;

import jaddon.controller.StaticStandard;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import jaddon.jlang.JLang;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * This is an add-on for java swing
 * @author Paul Hagedorn
 */
public class JLoader {
    JFrame frame = new JFrame("JLoader");
    Component c = null;
    JPanel panel_text = new JPanel();
    JPanel panel_loadbar = new JPanel();
    JTextArea ta = new JTextArea(10, 30);
    JScrollPane sp = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JLabel label = new JLabel("Progress:");
    JProgressBar pb = new JProgressBar(0, 100);
    JPanel panel_buttons = new JPanel();
    JButton b_ok = new JButton("Ok");
    JButton b_pause = new JButton("Pause");
    JButton b_cancel = new JButton("Cancel");
    private int value = 0;
    private int maxValue = 0;
    private int waitTime = 100;
    private String borderTitle = "Log:";
    private String labelTitle = "Progress";
    private String title = "JLoader";
    private String b_pause_text_pause = "Pause";
    private String b_pause_text_play = "Continue";
    private boolean visible = false;
    private boolean buttons = false;
    private boolean pause = false;
    private boolean closed = false;
    private boolean exitOnOk = false;
    private boolean finish = false;
    private boolean autoPause = false;
    private boolean useLang = true;
    private boolean lang_b = false;
    private Properties lang = JLang.getLangProp();
    
    /**
     * Constructs the standard JLoader object with default values
     * @param title String title
     */
    public JLoader(String title) {
        this(false, null, true, false, "Log:", "Progress:", title, false, true);
    }
    
    /**
     * Constructs the standard JLoader object with default values
     * @param title String title
     * @param visible Boolean frame visibility
     */
    public JLoader(String title, boolean visible) {
        this(visible, null, true, false, "Log:", "Progress:", title, false, true);
    }
    
    /**
     * Constructs the JLoader object with default values and new values
     * @param c Component where it is relative to
     * @param borderTitle String title from the border
     * @param labelTitle String title from the label
     * @param title String title
     * @param lang_b Boolen True if the system language should be used, False if not
     * @param visible Boolean frame visibility
     */
    public JLoader(Component c, String borderTitle, String labelTitle, String title, boolean lang_b, boolean visible) {
        this(visible, c, true, false, borderTitle, labelTitle, title, false, lang_b);
    }
    
    /**
     * Constructs the JLoader object with complete new values
     * @param visible Boolean frame visibility
     * @param c Component where it is relative to
     * @param stringPainted Boolean if the progressbar should be painted with text
     * @param editable Boolean if the textarea should be editable
     * @param borderTitle String title from the border
     * @param labelTitle String title from the label
     * @param title String title
     * @param buttons Boolean True if the buttons should be shown, False if not
     * @param lang_b Boolen True if the system language should be used, False if not
     */
    public JLoader(boolean visible, Component c, boolean stringPainted, boolean editable, String borderTitle, String labelTitle, String title, boolean buttons, boolean lang_b) {
        StaticStandard.setLoader(this);
        setStandardActionListener(b_ok, b_pause, b_cancel);
        panel_buttons.add(b_ok);
        panel_buttons.add(b_pause);
        panel_buttons.add(b_cancel);
        frame.setTitle(title);
        label.setText(labelTitle);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pb = new JProgressBar(0, maxValue);
        pb.setValue(value);
        pb.setStringPainted(stringPainted);
        ta.setEditable(editable);
        panel_text.setLayout(new BoxLayout(panel_text, 1));
        panel_text.setBorder(new TitledBorder(new EtchedBorder(), borderTitle));
        panel_text.add(sp);
        panel_loadbar.add(label);
        panel_loadbar.add(pb);
        DefaultCaret caret = (DefaultCaret) ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.add(panel_text, BorderLayout.CENTER);
        frame.add(panel_loadbar, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(c);
        frame.setVisible(visible);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
            
        });
        this.lang_b = lang_b;
        if(lang_b) {
            b_pause_text_pause = lang.getProperty("b_pause_pause", "Pause");
            b_pause_text_play = lang.getProperty("b_pause_play", "Continue");
            b_ok.setText(lang.getProperty("b_ok", "Ok"));
            b_cancel.setText(lang.getProperty("b_cancel", "Cancel"));
            setPause(pause);
        }
        setVisible(visible);
        setComponent(c);
        setBorderTitle(borderTitle);
        setLabelTitle(labelTitle);
        setTitle(title);
        setButtons(buttons);
    }
    
    /**
     * Adds text to the textarea
     * @param msg String text to be added
     */
    public void addText(String msg) {
        String oldText = ta.getText();
        String newText = oldText + ((oldText.equals("") ? "" : "\n")) + msg;
        ta.setText(newText);
        int k = 0;
        String[] split = ta.getText().split("\n");
        if(split.length > 0) {
            k = ta.getText().length() - split[split.length - 1].length();
            ta.setCaretPosition(k);
        }
        frame.revalidate();
        frame.repaint();
    }
    
    /**
     * Sets the text from the textarea
     * @param msg String text to be set
     */
    public void setText(String msg) {
        ta.setText(msg);
    }
    
    /**
     * Reloads the language
     */
    public void reloadLang() {
        if(lang_b) {
            b_pause_text_pause = lang.getProperty("b_pause_pause", "Pause");
            b_pause_text_play = lang.getProperty("b_pause_play", "Continue");
            b_ok.setText(lang.getProperty("b_ok", "Ok"));
            b_cancel.setText(lang.getProperty("b_cancel", "Cancel"));
        }
    }
    
    /**
     * Removes the last line in the textarea
     */
    public void removeLastLine() {
        String test = getLastLine();
        if(test != null) {
            String[] split = ta.getText().split("\n");
            if(split.length > 0) {
                ta.setText("");
                for(String g : split) {
                    if(!g.equals(test))
                    ta.setText(ta.getText() + ((ta.getText().equals("")) ? "" : "\n") + g);
                }
            }
        }
    }
    
    /**
     * Gets the last line of the textarea
     * @return String last line of the textarea
     */
    public String getLastLine() {
        String[] split = ta.getText().split("\n");
        if(split.length > 0) {
            return split[split.length - 1];
        }
        return null;
    }
    
    /**
     * Sets the standard action listeners to given buttons
     * @param butts JButton Set it to only them
     */
    public void setStandardActionListener(JButton ... butts) {
        for(JButton g : butts) {
            if(g == b_ok) {
                setOkActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(finish) {
                            closed = true;
                            if(exitOnOk) {
                                System.exit(0);
                            } else {
                                //frame.dispose();
                                setVisible(false);
                            }
                        }
                    }
                });
            } else if(g == b_pause) {
                setPauseActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setPause(!pause);
                        if(pause && autoPause) {
                            waitFor();
                        }
                    }
                });
            } else if(g == b_cancel) {
                setCancelActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                });
            }
        }
    }
    
    /**
     * Clears the complete text
     */
    public void clearText() {
        while(ta.getText().length() > 0) {
            ta.setText("");
        }
    }
    
    /**
     * Waits until pause is false
     */
    public void waitFor() {
            while(pause && buttons) {
                try {
                    Thread.sleep(waitTime);
                } catch (Exception ex) {
                    //System.err.println(ex);
                }
            }
    }
    
    /**
     * Disposes the frame 
     */
    public void dispose() {
        frame.dispose();
    }
    
    /**
     * Sets frames visibility
     * @param visible Boolean frames visibility
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        frame.setVisible(visible);
    }
    
    /**
     * Gets the JFrame
     * @return JFrame frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Sets the frame
     * @param frame JFrame frame
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
    /**
     * Returns the properties with the language
     * @return Properties Language
     */
    public Properties getLang() {
        return lang;
    }

    /**
     * Sets the language properties
     * @param lang Properties Language
     */
    public void setLang(Properties lang) {
        this.lang = lang;
        reloadLang();
    }

    public JPanel getPanel_text() {
        return panel_text;
    }

    public void setPanel_text(JPanel panel_text) {
        this.panel_text = panel_text;
    }

    public JPanel getPanel_loadbar() {
        return panel_loadbar;
    }

    public void setPanel_loadbar(JPanel panel_loadbar) {
        this.panel_loadbar = panel_loadbar;
    }

    public JTextArea getTa() {
        return ta;
    }

    public void setTa(JTextArea ta) {
        this.ta = ta;
    }

    public JScrollPane getSp() {
        return sp;
    }

    public void setSp(JScrollPane sp) {
        this.sp = sp;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public JProgressBar getPb() {
        return pb;
    }

    public void setPb(JProgressBar pb) {
        this.pb = pb;
    }

    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the progressbar
     * @param value Int new value
     */
    public void setValue(final int value) {
        if(value < 0) {
            //value = 0;
        }
        this.value = value;
        pb.setIndeterminate(false);
        pb.setValue(this.value);
        if(this.value >= this.maxValue) {
            this.value = this.maxValue;
            b_pause.setEnabled(false);
            finish = true;
        } else {
            b_pause.setEnabled(true);
            finish = false;
        }
        frame.revalidate();
        frame.repaint();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        if(maxValue < 0) {
            maxValue = 0;
        }
        this.maxValue = maxValue;
        pb.setMaximum(this.maxValue);
    }

    public String getBorderTitle() {
        return borderTitle;
    }

    public void setBorderTitle(String borderTitle) {
        this.borderTitle = borderTitle;
        panel_text.setBorder(new TitledBorder(new EtchedBorder(), this.borderTitle));
    }

    public String getLabelTitle() {
        return labelTitle;
    }

    public void setLabelTitle(String labelTitle) {
        this.labelTitle = labelTitle;
        label.setText(this.labelTitle);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        frame.setTitle(this.title);
    }

    public boolean isButtons() {
        return buttons;
    }

    /**
     * De- Activates the button panel
     * @param buttons Boolean if the button panel should be shown
     */
    public void setButtons(boolean buttons) {
        this.buttons = buttons;
        if(this.buttons) {
            if(frame.getComponentCount() < 3) {
                frame.add(panel_buttons, BorderLayout.SOUTH);
            }
        } else {
            if(frame.getComponentCount() > 2) {
                frame.remove(panel_buttons);
            }
        }
        frame.pack();
        frame.setLocationRelativeTo(c);
        frame.setVisible(this.visible);
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        if(this.pause) {
            b_pause.setText(b_pause_text_play);
        } else {
            b_pause.setText(b_pause_text_pause);
        }
    }
    
    public void setComponent(Component c) {
        this.c = c;
        frame.setLocationRelativeTo(c);
        frame.pack();
        frame.setVisible(this.visible);
    }
    
    /**
     * Sets a custom actionlistener for the ok button
     * @param al_ok ActionListener new ActionListener for the ok button
     */
    public void setOkActionListener(ActionListener al_ok) {
        if(b_ok.getActionListeners().length > 0) {
            for(ActionListener g : b_ok.getActionListeners()) {
                b_ok.removeActionListener(g);
            }
        }
        b_ok.addActionListener(al_ok);
    }
    
    /**
     * Sets a custom actionlistener for the pause button
     * @param al_pause ActionListener new ActionListener for the pause button
     */
    public void setPauseActionListener(ActionListener al_pause) {
        if(b_pause.getActionListeners().length > 0) {
            for(ActionListener g : b_pause.getActionListeners()) {
                b_pause.removeActionListener(g);
            }
        }
        b_pause.addActionListener(al_pause);
    }
    
    /**
     * Sets a custom actionlistener for the cancel button
     * @param al_cancel ActionListener new ActionListener for the cancel button
     */
    public void setCancelActionListener(ActionListener al_cancel) {
        if(b_cancel.getActionListeners().length > 0) {
            for(ActionListener g : b_cancel.getActionListeners()) {
                b_cancel.removeActionListener(g);
            }
        }
        b_cancel.addActionListener(al_cancel);
    }
    
    public ActionListener[] getOkActionListener() {
        return b_ok.getActionListeners();
    }
    
    public ActionListener[] getPauseActionListener() {
        return b_pause.getActionListeners();
    }
    
    public ActionListener[] getCancelActionListener() {
        return b_cancel.getActionListeners();
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public boolean isClosed() {
        return closed;
    }
    
    public void setExitOnClose(boolean exitOnClose) {
        this.exitOnOk = exitOnClose;
    }
    
    public boolean getExitOnClose() {
        return this.exitOnOk;
    }

    public boolean isAutoPause() {
        return autoPause;
    }

    public void setAutoPause(boolean autoPause) throws Exception {
        //this.autoPause = autoPause;
        throw new Exception(lang.getProperty("error_autoPause", "AutoPause is currently disabled!"));
    }

    public String getB_pause_text_pause() {
        return b_pause_text_pause;
    }

    public void setB_pause_text_pause(String b_pause_text_pause) {
        this.b_pause_text_pause = b_pause_text_pause;
    }

    public String getB_pause_text_play() {
        return b_pause_text_play;
    }

    public void setB_pause_text_play(String b_pause_text_play) {
        this.b_pause_text_play = b_pause_text_play;
    }

    public boolean isUseLang() {
        return useLang;
    }

    public void setUseLang(boolean useLang) {
        this.useLang = useLang;
    }
}
