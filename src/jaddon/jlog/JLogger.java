/*
 * To doFinal this license header, choose License Headers in Project Properties.
 * To doFinal this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlog;

import jaddon.controller.Command;
import jaddon.controller.CommandEvent;
import jaddon.controller.StaticStandard;
import jaddon.controller.Update;
import jaddon.jlang.JLang;
import jaddon.utils.JUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;

/**
 * This is an object to log data
 * @author Paul
 */
public class JLogger implements ActionListener, KeyListener, Update, WindowListener {
    
    public static final String COMMANDSTART = "/";
    
    private final ArrayList<LogEntry> log = new ArrayList<>();
    private final ArrayList<LogEntry> log_output = new ArrayList<>();
    private final ArrayList<LogEntry> log_error = new ArrayList<>();
    private final ArrayList<LogEntry> log_input = new ArrayList<>();
    private final ArrayList<LogEntry> log_command = new ArrayList<>();
    private final ArrayList<LogEntry> log_low = new ArrayList<>();
    private final ArrayList<JMenuItem> menuitems = new ArrayList<>();
    private final ArrayList<JCheckBoxMenuItem> menucheckboxitems = new ArrayList<>();
    private final Console console = new Console();
    private int depth = 0;
    private File log_file = null;
    private boolean loggingOnFile = false;
    private boolean logOnlyIfIDE = false;
    private boolean logSaveOnlyIfIDE = false;
    private boolean logTime = false;
    private boolean isIDE = false;
    private boolean doUpdate = false;
    private boolean printTimestamp = false;
    private boolean printExtraInformation = false;
    private boolean printLevel = false;
    private boolean debug = false;
    private boolean showNormal = true;
    private boolean showError = true;
    private boolean showInput = false;
    private boolean showLow = false;
    private boolean showCommand = false;
    private boolean console_visible = false;
    private boolean console_showed = false;
    private LogEntry START = null;
    private final WizardSaveAs wizardsaveas = new WizardSaveAs();
    private Properties lang = JLang.getLangProp();
    private String datetimeformat = "dd.MM.yyyy HH:mm:ss";
    private PrintStream system_old_out = null;
    private PrintStream system_old_err = null;
    private InputStream system_old_in = null;
    private PrintStream system_new_out = null;
    private PrintStream system_new_err = null;
    private SystemInputStream system_new_in = null;
    private final Command command_debug = new Command("debug") {

                @Override
                public void run(String arguments) {
                    if(arguments.isEmpty()) {
                        console.M2C4.setSelected(!console.M2C4.isSelected());
                        debug = console.M2C4.isSelected();
                        reloadConsole(true);
                        log(lang.getProperty("toggled_debug_mode", "Toggled Debug Mode"));
                    }
                }

            };
    public static final Command command_exit = new Command("exit") {

                @Override
                public void run(String arguments) {
                    if(arguments.isEmpty()) {
                        if(CommandEvent.getCommandListeners().isEmpty()) {
                            StaticStandard.exit();
                        } else {
                            CommandEvent.commandExecuted(new CommandEvent(JLogger.class, this, ""));
                        }
                    }
                }

            };
    
    /**
     * Constructor for a basic logger which only loggs to the arraylist and the console
     * @param isIDE if it is in ide
     */
    public JLogger(boolean isIDE) {
        this(null, isIDE);
        loggingOnFile = false;
    }
    
    /**
     * Constructor for the advanced logger which also loggs to a file
     * @param log_file File Log file
     * @param isIDE if it is in ide
     */
    public JLogger(File log_file, boolean isIDE) {
        this.log_file = log_file;
        this.isIDE = isIDE;
        StaticStandard.setLogger(this);
        if(StaticStandard.getLang() == null) {
            StaticStandard.setLang(new JLang(Locale.getDefault().getLanguage().toUpperCase()));
        }
        loggingOnFile = true;
        console.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        console.addWindowListener(this);
        reloadLang();
        init();
        printStart();
    }
    
    private void init() {
        console.setSize(new Dimension(1200, 500));
        console.textfield.addKeyListener(this);
        console.button.addActionListener(this);
        console.M2C1.setSelected(false);
        console.M2C2.setSelected(false);
        console.M2C3.setSelected(false);
        console.M2C4.setSelected(false);
        console.M2C5.setSelected(false);
        console.M2C6.setSelected(false);
        console.M2C7.setSelected(false);
        console.M1I1.addActionListener(this);
        console.M1I2.addActionListener(this);
        console.M1I3.addActionListener(this);
        console.M1I4.addActionListener(this);
        console.M2C1.addActionListener(this);
        console.M2C2.addActionListener(this);
        console.M2C3.addActionListener(this);
        console.M2C4.addActionListener(this);
        console.M2C5.addActionListener(this);
        console.M2C6.addActionListener(this);
        console.M2C7.addActionListener(this);
        console.M2C8.addActionListener(this);
        console.M2C9.addActionListener(this);
    }
    
    private void printStart() {
        START = new LogEntry(lang.getProperty("program_started", "Program started"), StaticStandard.INSTANTSTART, LogEntry.LEVELLOW, datetimeformat, Thread.currentThread(), getStackTraceElement());
        START.setPrintExtraInformation(false);
        START.setPrintLevel(false);
        START.setPrintTimestamp(true);
        logLogEntry(START, false, false, false);
        addToConsole(START, false);
    }
    
    /**
     * Shows the console
     * @param c Component Location
     */
    public void showConsole(Component c) {
        if(!console_visible && !console_showed) {
            reloadLang();
            console.M2C1.setSelected(printTimestamp);
            console.M2C2.setSelected(printExtraInformation);
            console.M2C3.setSelected(printLevel);
            console.M2C4.setSelected(debug);
            console.M2C5.setSelected(showNormal);
            console.M2C6.setSelected(showError);
            console.M2C7.setSelected(showInput);
            console.M2C8.setSelected(showCommand);
            console.M2C9.setSelected(showLow);
            console.setLocationRelativeTo(c);
            console.setVisible(true);
            console_visible = true;
            console_showed = true;
        }
        reloadConsole();
    }
    
    /**
     * Shows the console
     * @param c Component Location
     */
    public void reShowConsole(Component c) {
        if(!console_visible && console_showed) {
            reloadLang();
            console.M2C1.setSelected(printTimestamp);
            console.M2C2.setSelected(printExtraInformation);
            console.M2C3.setSelected(printLevel);
            console.M2C4.setSelected(debug);
            console.M2C5.setSelected(showNormal);
            console.M2C6.setSelected(showError);
            console.M2C7.setSelected(showInput);
            console.M2C8.setSelected(showCommand);
            console.M2C9.setSelected(showLow);
            console.setVisible(true);
            console_visible = true;
            console_showed = true;
        } else if(!console_showed) {
            showConsole(c);
        }
        reloadConsole();
    }
    
    /**
     * Closes the console
     */
    public void closeConsole() {
        if(console_visible || console_showed) {
            console.dispose();
            console_visible = false;
            console_showed = false;
        }
        reloadConsole();
    }
    
    /**
     * Hides the console
     */
    public void hideConsole() {
        if(console_visible && console_showed) {
            console.setVisible(false);
            console_visible = false;
            console_showed = true;
        }
        reloadConsole();
    }
    
    /**
     * Return the jmenuitem to get added for a menubar
     * @param c Component Location
     * @return JMenuItem
     */
    public JMenuItem getJMenuItem(Component c) {
        JMenuItem mi = new JMenuItem("Show Console");
        mi.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                reShowConsole(c);
            }
            
        });
        menuitems.add(mi);
        return mi;
    }
    
    /**
     * Return the jcheckboxmenuitem to get added for a menubar
     * @param c Component Location
     * @return JCheckBoxMenuItem
     */
    public JCheckBoxMenuItem getJCheckBoxMenuItem(Component c) {
        JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem("Show Console");
        cbmi.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cbmi.isSelected()) {
                    reShowConsole(c);
                } else {
                    hideConsole();
                }
            }
            
        });
        menucheckboxitems.add(cbmi);
        return cbmi;
    }
    
    /**
     * Activates the custom system output stream
     * @return PrintStream new system output stream
     */
    public PrintStream enableOutputStream() {
        if(system_new_out == null) {
            system_old_out = System.out;
            system_new_out = new SystemOutputStream(System.out, this) {
                
                @Override
                public String doFinal(Object o, Instant instant, Thread thread, StackTraceElement e) {
                    return logOutputStream(o, instant, thread, e);
                }
                
            };
            System.setOut(system_new_out);
            return system_new_out;
        } else {
            return null;
        }
    }
    
    /**
     * Disables the custom system output stream
     * @return Boolean True if it worked, False if not
     */
    public boolean disableOutputStream() {
        if(system_old_out != null) {
            System.setOut(system_old_out);
            system_new_out = null;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Activates the custom system error output stream
     * @return PrintStream new system error output stream
     * */
    public PrintStream enableErrorOutputStream() {
        if(system_new_err == null) {
            system_old_err = System.err;
            system_new_err = new SystemOutputStream(System.err, this) {
                
                @Override
                public String doFinal(Object o, Instant instant, Thread thread, StackTraceElement e) {
                    return logErrorOutputStream(o, instant, thread, e);
                }
                
            };
            System.setErr(system_new_err);
            return system_new_err;
        } else {
            return null;
        }
    }
    
    /**
     * Disables the custom system error output stream
     * @return Boolean True if it worked, False if not
     */
    public boolean disableErrorOutputStream() {
        if(system_old_err != null) {
            System.setErr(system_old_err);
            system_new_err = null;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Activates the custom system input output stream
     * @return InputStream new system input output stream
     * */
    public InputStream enableInputStream() {
        if(system_new_in == null) {
            system_old_in = System.in;
            system_new_in = new SystemInputStream(System.in, this);
            //System.setIn(system_new_in);
            //return system_new_in;
            return System.in;
        } else {
            return null;
        }
    }
    
    /**
     * Disables the custom system input output stream
     * @return Boolean True if it worked, False if not
     */
    public boolean disableInputStream() {
        if(system_old_in != null) {
            system_new_in.stop();
            System.setIn(system_old_in);
            system_old_in = null;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Logs from the output stream
     * @param o Object to log
     * @param Instant Instant Timestamp
     * @param e
     * @return Object that got logged
     */
    private String logOutputStream(Object o, Instant instant, Thread thread, StackTraceElement e) {
        LogEntry logentry = getLogEntry(o, instant, LogEntry.LEVELNORMAL, datetimeformat, thread, e);
        logLogEntry(logentry, false, false, false);
        return logentry.toString();
    }
    
    /**
     * Logs from the error output stream
     * @param o Object to error log
     * @param Instant Instant Timestamp
     * @param e
     * @return Object that got error logged
     */
    private String logErrorOutputStream(Object o, Instant instant, Thread thread, StackTraceElement e) {
        LogEntry logentry = getLogEntry(o, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logLogEntry(logentry, false, false, false);
        return logentry.toString();
    }
    
    /**
     * Logs from the input stream
     * @param i Integer to be logged
     * @param instant Instant Timestamp
     * @param e
     * @return Integer that got logged
     */
    private int logInputStream(int i, Instant instant, Thread thread, StackTraceElement e) {
        LogEntry logentry = getLogEntry(i, instant, LogEntry.LEVELINPUT, datetimeformat, thread, e);
        logLogEntry(logentry, false, false, false);
        return Integer.valueOf(logentry.toString());
    }
    
    private LogEntry getLogEntry(Object o, Instant instant, int level, String datetimeformat, Thread thread, StackTraceElement e) {
        LogEntry logentry = new LogEntry(o, instant, level, datetimeformat, thread, e);
        logentry.setDebug(debug);
        logentry.setPrintExtraInformation(printExtraInformation);
        logentry.setPrintTimestamp(printTimestamp);
        logentry.setPrintLevel(printLevel);
        return logentry;
    }
    
    private void updateLogEntry(LogEntry logentry) {
        logentry.setDebug(debug);
        logentry.setPrintExtraInformation(printExtraInformation);
        logentry.setPrintTimestamp(printTimestamp);
        logentry.setPrintLevel(printLevel);
    }
    
    private void logLogEntry(LogEntry logentry, boolean update, boolean show, boolean speak) {
        if(update) {
            updateLogEntry(logentry);
        }
        log.add(logentry);
        if(speak) {
            try {
                JUtils.speak(logentry.getLogEntry().toString());
            } catch (Exception ex) {

            }
        }
        switch(logentry.getLevel()) {
            case LogEntry.LEVELERROR:
                log_error.add(logentry);
                if(system_old_err != null) {
                    system_old_err.println(logentry);
                } else {
                    System.err.println(logentry);
                }
                if(show) {
                    JOptionPane.showMessageDialog(null, logentry.getLogEntry(), lang.getProperty("error", "Error"), JOptionPane.ERROR_MESSAGE);
                }
                break;
            case LogEntry.LEVELINPUT:
                log_input.add(logentry);
                if(system_old_out != null) {
                    system_old_out.println(logentry);
                } else {
                    System.out.println(logentry);
                }
                if(show) {
                    JOptionPane.showMessageDialog(null, logentry.getLogEntry(), lang.getProperty("message", "Message"), JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case LogEntry.LEVELLOW:
                log_low.add(logentry);
                if(isIDE || (!isIDE && !logOnlyIfIDE)) {
                    if(system_old_out != null) {
                        system_old_out.println(logentry);
                    } else {
                        System.out.println(logentry);
                    }
                }
                if(show) {
                    JOptionPane.showMessageDialog(null, logentry.getLogEntry(), lang.getProperty("message", "Message"), JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case LogEntry.LEVELNORMAL:
                log_output.add(logentry);
                if(isIDE || (!isIDE && !logOnlyIfIDE)) {
                    if(system_old_out != null) {
                        system_old_out.println(logentry);
                    } else {
                        System.out.println(logentry);
                    }
                }
                if(show) {
                    JOptionPane.showMessageDialog(null, logentry.getLogEntry(), lang.getProperty("message", "Message"), JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case LogEntry.LEVELCOMMAND:
                log_command.add(logentry);
                if(system_old_out != null) {
                    system_old_out.println(logentry);
                } else {
                    System.out.println(logentry);
                }
                if(show) {
                    JOptionPane.showMessageDialog(null, logentry.getLogEntry(), lang.getProperty("message", "Message"), JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }
        addToConsole(logentry, true);
    }
    
    /**
     * Loggs simply an object without showing a dialog
     * @param msg Object Message to log
     * @param level Integer Log level
     */
    public void log(Object msg, int level) {
        log(msg, false, level, false);
    }
    
    /**
     * Loggs an object with settable showing dialog
     * @param msg Object Message to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param level Integer Log level
     */
    public void log(Object msg, boolean show, int level) {
        log(msg, show, level, false);
    }
    
    /**
     * Loggs an object with settable showing dialog
     * @param msg Object Message to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param level Integer Log level
     * @param speak Should speak the message
     */
    public void log(Object msg, boolean show, int level, boolean speak) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, level, datetimeformat, thread, e);
        logLogEntry(logentry, false, show, speak);
        if(loggingOnFile) {
            logFile(logentry, false, instant);
        }
    }
    
    /**
     * Loggs simply an object without showing a dialog
     * @param msg Object Message to log
     */
    public void log(Object msg) {
        log(msg, false, false);
    }
    
    
    /**
     * Loggs an object with settable showing dialog
     * @param msg Object Message to log
     * @param show Boolean True if a dialog should be shown, False if not
     */
    public void log(Object msg, boolean show) {
        log(msg, show, false);
    }
    
    /**
     * Loggs an object with settable showing dialog
     * @param msg Object Message to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param speak Should speak the message
     */
    public void log(Object msg, boolean show, boolean speak) {
        log(msg, show, LogEntry.LEVELNORMAL, speak);
    }
    
    protected StackTraceElement[] getStackTraceElements() {
        return Thread.currentThread().getStackTrace();
    }
    
    protected Thread getThread() {
        return Thread.currentThread();
    }
    
    protected StackTraceElement getStackTraceElement() {
        return getStackTraceElement(Thread.currentThread());
    }
    
    protected StackTraceElement getStackTraceElement(Thread thread) {
        int i = 1;
        final String[] forbidden_names = new String[] {this.getClass().getName(), JLogger.class.getName(), StaticStandard.class.getName(), SystemOutputStream.class.getName(), SystemInputStream.class.getName(), PrintStream.class.getName(), InputStream.class.getName()};
        while(containsArray(thread.getStackTrace()[i].getClassName(), forbidden_names)) {
            i++;
        }
        return thread.getStackTrace()[i];
    }
    
    private boolean containsArray(String g, String[] array) {
        for(String gg : array) {
            if(g.equals(gg)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Error loggs simply an object without showing a dialog
     * @param msg Object Error to log
     */
    public void logErr(Object msg) {
        logErr(msg, false, false);
    }
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param show Boolean True if a dialog should be shown, False if not
     */
    public void logErr(Object msg, boolean show) {
        logErr(msg, show, false);
    }
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param speak Should speak the message
     */
    public void logErr(Object msg, boolean show, boolean speak) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logLogEntry(logentry, false, show, speak);
        if(loggingOnFile) {
            logFile(logentry, true, instant);
        }
    }
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param ex Exception to be logged
     */
    public void logErr(Object msg, Exception ex) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logentry.setException(ex);
        logLogEntry(logentry, false, false, false);
        if(loggingOnFile) {
            logFile(logentry, true, instant);
        }
    }
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param ex Exception to be logged
     * @param speak Should speak the message
     */
    public void logErr(Object msg, Exception ex, boolean speak) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logentry.setException(ex);
        logLogEntry(logentry, false, false, speak);
        if(loggingOnFile) {
            logFile(logentry, true, instant);
        }
    }
    
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param ex Exception to be logged
     */
    public void logErr(Object msg, boolean show, Exception ex) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logentry.setException(ex);
        logLogEntry(logentry, false, show, false);
        if(loggingOnFile) {
            logFile(logentry, true, instant);
        }
    }
    
    /**
     * Error loggs an object with settable showing dialog
     * @param msg Object Error to log
     * @param show Boolean True if a dialog should be shown, False if not
     * @param ex Exception to be logged
     * @param speak Should speak the message
     */
    public void logErr(Object msg, boolean show, Exception ex, boolean speak) {
        Instant instant = Instant.now();
        Thread thread = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(msg, instant, LogEntry.LEVELERROR, datetimeformat, thread, e);
        logentry.setException(ex);
        logLogEntry(logentry, false, show, speak);
        if(loggingOnFile) {
            logFile(logentry, true, instant);
        }
    }
    
    /**
     * Writes an object to a file if it is converting toString()
     * @param msg Object Message to be written
     * @param isError Boolean True if it is an error message, False if not
     * */
    private void logFile(Object msg, boolean isError) {
        logFile(msg, isError, Instant.now());
    }
    
    /**
     * Writes an object to a file if it is converting toString()
     * @param msg Object Message to be written
     * @param isError Boolean True if it is an error message, False if not
     * @param instant Instant printTimestamp of the log
     */
    private void logFile(Object msg, boolean isError, Instant instant) {
        if(!log_file.exists()) {
            try {
                log_file.createNewFile();
            } catch (Exception ex) {
                logErr(String.format(lang.getProperty("error_while_creating_log_file", "Error while creating log file: %s"), ex));
                return;
            }
        }
        try {
            String message = msg.toString();
            if(isError) {
                message = "[" + lang.getProperty("error", "Error") + "]: " + message;
            }
            if(logTime) {
                message = "[" + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(datetimeformat)) + "]" + ((isError) ? "" : ":") + " " + message;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
            bw.write(message);
            bw.newLine();
            bw.close();
        } catch (Exception ex) {
            logErr(String.format(lang.getProperty("error_while_writing_to_the_log_file", "Error while writing to the log file: %s"), ex));
        }
    }
    
    /**
     * Sets the file where the log will be written to
     * @param log_file File File where the log goes
     */
    public void setLogFile(File log_file) {
        this.log_file = log_file;
    }
    
    /**
     * Returns the file where the log is written to
     * @return File Log file
     */
    public File getLogFile() {
        return log_file;
    }

    /**
     * Returns if the logger loggs in a file or not
     * @return Boolean True if it loggs on file, False if not
     */
    public boolean isLoggingOnFile() {
        return loggingOnFile;
    }

    /**
     * Sets wheter the logger loggs on file or not
     * @param loggingOnFile Boolean True if it loggs on file, False if not
     */
    public void setLoggingOnFile(boolean loggingOnFile) {
        this.loggingOnFile = loggingOnFile;
    }

    /**
     * Returns wheter the logger logs also the time in the file or not
     * @return Boolean True if the logger writes the time to the file, False if not
     */
    public boolean isLogTime() {
        return logTime;
    }

    /**
     * Sets if the logger should log also the time in the file
     * @param logTime Boolean True if the logger should log time to file, False if not
     */
    public void setLogTime(boolean logTime) {
        this.logTime = logTime;
    }
    
    /**
     * Returns the date time format as string
     * @return String Date time format
     */
    public String getDateTimeFormat() {
        return datetimeformat;
    }
    
    /**
     * Sets the date time format as string
     * @param datetimeformat String New date time format
     */
    public void setDataTimeFormat(String datetimeformat) {
        this.datetimeformat = datetimeformat;
    }
    
    /**
     * Returns the date time format as datetimeformatter
     * @return DateTimeFormatter Date time format
     */
    public DateTimeFormatter getDataTimeFormat() {
        return DateTimeFormatter.ofPattern(datetimeformat);
    }
    
    /**
     * Sets the date time format as datetimeformatter
     * @param dtf DateTimeFormatter Date time format
     */
    private void setDateTimeFormat(DateTimeFormatter dtf) {
        //This doesnt work
        datetimeformat = dtf.toString();
    }

    /**
     * Returns if the program only logs if it is in ide
     * @return Boolean True if it loggs only in ide, False if not
     */
    public boolean isLoggingOnlyIfIDE() {
        return logOnlyIfIDE;
    }

    /**
     * Sets if the program only logs if it is in ide
     * @param logOnlyIfIDE Boolean True if it loggs only in ide, False if not
     */
    public void setLoggingOnlyIfIDE(boolean logOnlyIfIDE) {
        this.logOnlyIfIDE = logOnlyIfIDE;
    }
    
    /**
     * Returns if the program only save logs if it is in ide
     * @return Boolean True if it save loggs only in ide, False if not
     */
    public boolean isLogSaveOnlyIfIDE() {
        return logSaveOnlyIfIDE;
    }
    
    /**
     * Sets if the program only save logs if it is in ide
     * @param logSaveOnlyIfIDE Boolean True if it save loggs only in ide, False if not
     */
    public void setLogSaveOnlyIfIDE(boolean logSaveOnlyIfIDE) {
        this.logSaveOnlyIfIDE = logSaveOnlyIfIDE;
    }

    /**
     * Gets the isIDE variable
     * @return Boolean isIDE variable
     */
    public boolean isIDE() {
        return isIDE;
    }

    /**
     * Sets if the program runs through an ide or not
     * @param isIDE Boolean isIDE
     */
    public void setIsIDE(boolean isIDE) {
        this.isIDE = isIDE;
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
    }

    public ArrayList<LogEntry> getLogOutput() {
        return log_output;
    }

    public ArrayList<LogEntry> getLogError() {
        return log_error;
    }

    public ArrayList<LogEntry> getLogInput() {
        return log_input;
    }
    
    public ArrayList<LogEntry> getLog() {
        return log;
    }

    public boolean isPrintTimestamp() {
        return printTimestamp;
    }
    
    public PrintStream getOutputStream() {
        if(system_old_out != null) {
            return system_old_out;
        } else {
            return System.out;
        }
    }
    
    public PrintStream getErrorStream() {
        if(system_old_err != null) {
            return system_old_err;
        } else {
            return System.err;
        }
    }
    
    public InputStream getInputStream() {
        if(system_old_in != null) {
            return system_old_in;
        } else {
            return System.in;
        }
    }

    /**
     * Sets if the logger should print the printTimestamp of the things that gets logged
     * @param printTimestamp Boolean
     */
    public void setPrintTimestamp(boolean printTimestamp) {
        this.printTimestamp = printTimestamp;
        reloadConsole(true);
    }

    public boolean isPrintExtraInformation() {
        return printExtraInformation;
    }

    /**
     * Sets if the logger should print the class which called the logger to log
     * @param printExtraInformation Boolean
     */
    public void setPrintExtraInformation(boolean printExtraInformation) {
        this.printExtraInformation = printExtraInformation;
        reloadConsole(true);
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug mode
     * @param debug Boolean
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        reloadConsole(true);
    }

    public boolean isPrintLevel() {
        return printLevel;
    }

    public void setPrintLevel(boolean printLevel) {
        this.printLevel = printLevel;
        reloadConsole(true);
    }

    public boolean isShowNormal() {
        return showNormal;
    }

    public void setShowNormal(boolean showNormal) {
        this.showNormal = showNormal;
        reloadConsole(true);
    }

    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
        reloadConsole(true);
    }

    public boolean isShowInput() {
        return showInput;
    }

    public void setShowInput(boolean showInput) {
        this.showInput = showInput;
        reloadConsole(true);
    }

    public boolean isShowLow() {
        return showLow;
    }

    public void setShowLow(boolean showLow) {
        this.showLow = showLow;
        reloadConsole(true);
    }

    public boolean isShowCommand() {
        return showCommand;
    }

    public void setShowCommand(boolean showCommand) {
        this.showCommand = showCommand;
        reloadConsole(true);
    }
    
    public static ArrayList<LogEntry> sortLogEntries(ArrayList<LogEntry> array, boolean ascending) {
        array.sort(new Comparator() {
            
            @Override
            public int compare(Object o1, Object o2) {
                LogEntry le1 = (LogEntry) o1;
                LogEntry le2 = (LogEntry) o2;
                long le1epochmilli = le1.getTimeStamp().toEpochMilli();
                long le2epochmilli = le2.getTimeStamp().toEpochMilli();
                if(le1epochmilli == le2epochmilli) {
                    return 0;
                } else if(le1epochmilli > le2epochmilli) {
                    return 1 * ((ascending) ? 1 : -1);
                } else if(le1epochmilli < le2epochmilli) {
                    return -1 * ((ascending) ? 1 : -1);
                }
                return 0;
            }
            
        });
        return array;
    }
    
    private void reloadConsole() {
        for(JCheckBoxMenuItem cbmi : menucheckboxitems) {
            cbmi.setSelected(console_visible);
        }
    }
    
    private ArrayList<LogEntry> getLogEntriesSorted(boolean update) {
        return getLogEntriesSorted(update, showError, showNormal, showInput, showLow, showCommand, debug);
    }
    
    private ArrayList<LogEntry> getLogEntriesSorted(boolean update, boolean showError, boolean showNormal, boolean showInput, boolean showLow, boolean showCommand, boolean debug) {
        ArrayList<LogEntry> logentries = new ArrayList<>();
        if(showError && showNormal && showInput && showLow && showCommand || debug) {
            logentries = log;
        } else {
            if(showError) {
                for(LogEntry logentry : log_error) {
                    logentries.add(logentry);
                }
            }
            if(showNormal) {
                for(LogEntry logentry : log_output) {
                    logentries.add(logentry);
                }
            }
            if(showInput) {
                for(LogEntry logentry : log_input) {
                    logentries.add(logentry);
                }
            }
            if(showCommand) {
                for(LogEntry logentry : log_command) {
                    logentries.add(logentry);
                }
            }
            if(showLow) {
                for(LogEntry logentry : log_low) {
                    logentries.add(logentry);
                }
            }
            JLogger.sortLogEntries(logentries, true);
            for(LogEntry logentry : logentries) {
                if(update || debug) {
                    updateLogEntry(logentry);
                }
            }
        }
        return logentries;
    }
    
    private void reloadConsole(boolean update) {
        ArrayList<LogEntry> logentries = getLogEntriesSorted(update);
        reloadConsole(logentries, false);
    }
    
    private void reloadConsole(ArrayList<LogEntry> logentries, boolean update) {
        StyledDocument doc = console.textpane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            logErr(ex, false);
        }
        Style style = doc.addStyle("Style", null);
        for(LogEntry logentry : logentries) {
            if(update) {
                updateLogEntry(logentry);
            }
            switch(logentry.getLevel()) {
                case LogEntry.LEVELERROR:
                    StyleConstants.setBackground(style, Color.WHITE);
                    StyleConstants.setForeground(style, Color.red);
                    break;
                case LogEntry.LEVELINPUT:
                    StyleConstants.setBackground(style, Color.WHITE);
                    StyleConstants.setForeground(style, Color.BLUE);
                    break;
                case LogEntry.LEVELLOW:
                    StyleConstants.setBackground(style, Color.WHITE);
                    StyleConstants.setForeground(style, Color.GRAY);
                    break;
                case LogEntry.LEVELNORMAL:
                    StyleConstants.setBackground(style, Color.WHITE);
                    StyleConstants.setForeground(style, Color.BLACK);
                    break;
            }
            try {
                doc.insertString(doc.getLength(), logentry.toString() + "\n", style);
            } catch (Exception ex) {
                logErr(String.format(lang.getProperty("error_while_adding_to_the_console", "Error while adding to the console: %s"), ex));
            }
        }
    }
    
    public File saveAs() {
        File file = null;
        try {
            wizardsaveas.checkbox_save_infos_appearance.setSelected(printExtraInformation);
            wizardsaveas.checkbox_save_infos_level.setSelected(printLevel);
            wizardsaveas.checkbox_save_infos_timestamp.setSelected(printTimestamp);
            wizardsaveas.checkbox_save_level_command.setSelected(showCommand);
            wizardsaveas.checkbox_save_level_error.setSelected(showError);
            wizardsaveas.checkbox_save_level_input.setSelected(showInput);
            wizardsaveas.checkbox_save_level_low.setSelected(showLow);
            wizardsaveas.checkbox_save_level_normal.setSelected(showNormal);
            LogEntry logentry_temp = wizardsaveas.showSaveAsDialog((console_visible) ? console : null);
            if(logentry_temp == null || logentry_temp.getLogEntry() == null || !(logentry_temp.getLogEntry() instanceof File)) {
                return null;
            }
            file = (File) logentry_temp.getLogEntry();
            if(file == null) {
                return null;
            }
            try {
                if(!file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception ex) {
                
            }
            ArrayList<LogEntry> logentries = getLogEntriesSorted(false, wizardsaveas.checkbox_save_level_error.isSelected(), wizardsaveas.checkbox_save_level_normal.isSelected(), wizardsaveas.checkbox_save_level_input.isSelected(), wizardsaveas.checkbox_save_level_low.isSelected(), wizardsaveas.checkbox_save_level_command.isSelected(), false);
            for(LogEntry logentry : logentries) {
                logentry.setPrintTimestamp(wizardsaveas.checkbox_save_infos_timestamp.isSelected());
                logentry.setPrintExtraInformation(wizardsaveas.checkbox_save_infos_appearance.isSelected());
                logentry.setPrintLevel(wizardsaveas.checkbox_save_infos_level.isSelected());
            }
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(LogEntry logentry : logentries) {
                    try {
                    bw.write(logentry.toString());
                    bw.newLine();
                } catch (Exception ex) {
                    //StaticStandard.logErr("Error while writing log to file: " + ex, ex);
                }
            }
            bw.close();
            fw.close();
            bw = null;
            fw = null;
            return file;
        } catch (Exception ex) {
            //StaticStandard.logErr("Error while saving log to file: " + ex, ex);
            return null;
        }
    }
    
    private void addToConsole(LogEntry logentry, boolean update) {
        StyledDocument doc = console.textpane.getStyledDocument();
        Style style = doc.addStyle("Style", null);
        if(update) {
            updateLogEntry(logentry);
        }
        boolean doesLog = true;
        switch(logentry.getLevel()) {
            case LogEntry.LEVELERROR:
                doesLog = showError;
                StyleConstants.setBackground(style, Color.WHITE);
                StyleConstants.setForeground(style, Color.red);
                break;
            case LogEntry.LEVELINPUT:
                doesLog = showInput;
                StyleConstants.setBackground(style, Color.WHITE);
                StyleConstants.setForeground(style, Color.BLUE);
                break;
            case LogEntry.LEVELLOW:
                doesLog = showLow;
                StyleConstants.setBackground(style, Color.WHITE);
                StyleConstants.setForeground(style, Color.GRAY);
                break;
            case LogEntry.LEVELNORMAL:
                doesLog = showNormal;
                StyleConstants.setBackground(style, Color.WHITE);
                StyleConstants.setForeground(style, Color.BLACK);
                break;
            case LogEntry.LEVELCOMMAND:
                doesLog = showCommand;
                StyleConstants.setBackground(style, Color.BLACK);
                StyleConstants.setForeground(style, Color.YELLOW);
                break;
        }
        if(doesLog) {
            try {
                doc.insertString(doc.getLength(), logentry.toString() + "\n", style);
            } catch (Exception ex) {
                logErr(String.format(lang.getProperty("error_while_adding_to_the_console", "Error while adding to the console: %s"), ex));
            }
            console.textpane.setCaretPosition(doc.getLength());
        }
    }
    
    protected boolean sendCommand(String command) {
        if(command == null || command.isEmpty()) {
            return false;
        }
        Thread thread_ = getThread();
        StackTraceElement e = getStackTraceElement();
        LogEntry logentry = getLogEntry(command, Instant.now(), LogEntry.LEVELCOMMAND, datetimeformat, thread_, e);
        if(command.startsWith(COMMANDSTART)) {
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        boolean done = Command.runCommand(command.substring(1));
                    } catch (Exception ex) {
                        logErr(String.format(lang.getProperty("error_while_executing_a_command", "Error while executing a command: %s"), ex));
                    }
                }
                
            });
            thread.start();
        } else {
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        if(system_old_in != null) {
                            system_old_in.read(command.getBytes());//FIXME WTF This does not sends data to the system_old_in stream, it reads data from it?!
                        } else {
                            System.in.read(command.getBytes()); //FIXME WTF This does not sends data to the System.in stream, it reads data from it?!
                        }
                    } catch (Exception ex) {
                        logErr(String.format(lang.getProperty("error_while_sending_a_command", "Error while sending a command: %s"), ex));
                    }
                }
                
            });
            thread.start();
        }
        depth = 0;
        logLogEntry(logentry, true, false, false);
        return true;
    }
    
    private void consoleCommand(int go) {
        depth += go;
        if(depth < 0) {
            depth = 0;
        } else if(depth > log_command.size()) {
            depth = log_command.size();
        }
        if(depth == 0) {
            console.textfield.setText("");
        } else {
            console.textfield.setText((String) log_command.get(log_command.size() - depth).getLogEntry());
        }
    }
    
    public Console getConsole() {
        return console;
    }
    
    private void reloadLang() {
        lang = JLang.getLangProp();
        console.setTitle(lang.getProperty("console", "Console"));
        console.button.setText(lang.getProperty("enter", "Enter"));
        console.M1.setText(lang.getProperty("file", "File"));
        console.M2.setText(lang.getProperty("options", "Options"));
        console.M1I1.setText(lang.getProperty("exit", "Exit"));
        console.M1I2.setText(lang.getProperty("restart", "Restart"));
        console.M1I3.setText(lang.getProperty("reload", "Reload"));
        console.M1I4.setText(lang.getProperty("save_as", "Save As"));
        console.M2C1.setText(lang.getProperty("print_timestamp", "Show Timestamp"));
        console.M2C2.setText(lang.getProperty("print_extrainformation", "Show Appearance"));
        console.M2C3.setText(lang.getProperty("print_level", "Show Level"));
        console.M2C4.setText(lang.getProperty("debug_mode", "Enable Debug Mode"));
        console.M2C5.setText(lang.getProperty("show_normal", "Show Normal"));
        console.M2C6.setText(lang.getProperty("show_error", "Show Error"));
        console.M2C7.setText(lang.getProperty("show_input", "Show Input"));
        console.M2C8.setText(lang.getProperty("show_command", "Show Command"));
        console.M2C9.setText(lang.getProperty("show_low", "Show Low"));
        wizardsaveas.setTitle(lang.getProperty("save_as", "Save As"));
        wizardsaveas.button_bottom_back.setText(lang.getProperty("back_a", "< Back"));
        wizardsaveas.button_bottom_next.setText(lang.getProperty("next_a", "Next >"));
        wizardsaveas.button_bottom_cancel.setText(lang.getProperty("cancel", "Cancel"));
        wizardsaveas.button_bottom_finish.setText(lang.getProperty("finish", "Finish"));
        wizardsaveas.button_center_path.setText(lang.getProperty("search_computer", "Search Computer"));
        wizardsaveas.label_center_path.setText(lang.getProperty("file", "File"));
        wizardsaveas.label_save_infos.setText(lang.getProperty("save_infos", "Save Infos"));
        wizardsaveas.label_save_level.setText(lang.getProperty("save_levels", "Save Level"));
        wizardsaveas.checkbox_save_infos_appearance.setText(lang.getProperty("appearance", "Appearance"));
        wizardsaveas.checkbox_save_infos_level.setText(lang.getProperty("level", "Level"));
        wizardsaveas.checkbox_save_infos_timestamp.setText(lang.getProperty("timestamp", "Timestamp"));
        wizardsaveas.checkbox_save_level_command.setText(lang.getProperty("command", "Command"));
        wizardsaveas.checkbox_save_level_error.setText(lang.getProperty("error", "Error"));
        wizardsaveas.checkbox_save_level_input.setText(lang.getProperty("input", "Input"));
        wizardsaveas.checkbox_save_level_low.setText(lang.getProperty("low", "Low"));
        wizardsaveas.checkbox_save_level_normal.setText(lang.getProperty("normal", "Normal"));
        wizardsaveas.jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), lang.getProperty("settings", "Settings")));
        if(START != null) {
            START.setLogEntry(lang.getProperty("program_started", "Program started"));
        }
        for(JMenuItem mi : menuitems) {
            mi.setText(lang.getProperty("show_console", "Show Console"));
        }
        for(JCheckBoxMenuItem cbmi : menucheckboxitems) {
           cbmi.setText(lang.getProperty("show_console", "Show Console"));
        }
        reloadConsole(false);
    }

    public boolean update() {
        if(!doUpdate) {
            return false;
        }
        isIDE = StaticStandard.isIsIDE();
        reloadLang();
        return true;
    }

    @Override
    public void setDoUpdate(boolean doUpdate) {
        this.doUpdate = doUpdate;
    }

    @Override
    public boolean isDoingUpdate() {
        return doUpdate;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getSource() == console.textfield) {
            if(e.getKeyChar() == (char) 10) {
                boolean done = sendCommand(console.textfield.getText());
                if(done) {
                    console.textfield.setText("");
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getSource() == console.textfield) {
            if(e.getKeyCode() == KeyEvent.VK_UP) {
                consoleCommand(1);
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                consoleCommand(-1);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == console.button) {
            boolean done = sendCommand(console.textfield.getText());
            if(done) {
                console.textfield.setText("");
            }
        } else if(e.getSource() == console.M1I1) {
            closeConsole();
        } else if(e.getSource() == console.M1I2) {
            closeConsole();
            showConsole(null);
        } else if(e.getSource() == console.M1I3) {
            reloadConsole(true);
        } else if(e.getSource() == console.M2C1) {
            if(console.M2C1.isSelected() != printTimestamp) {
                printTimestamp = console.M2C1.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C2) {
            if(console.M2C2.isSelected() != printExtraInformation) {
                printExtraInformation = console.M2C2.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C3) {
            if(console.M2C3.isSelected() != printLevel) {
                printLevel = console.M2C3.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C4) {
            if(console.M2C4.isSelected() != debug) {
                debug = console.M2C4.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C5) {
            if(console.M2C5.isSelected() != showNormal) {
                showNormal = console.M2C5.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C6) {
            if(console.M2C6.isSelected() != showError) {
                showError = console.M2C6.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C7) {
            if(console.M2C7.isSelected() != showInput) {
                showInput = console.M2C7.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C8) {
            if(console.M2C8.isSelected() != showCommand) {
                showCommand = console.M2C8.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M2C9) {
            if(console.M2C9.isSelected() != showLow) {
                showLow = console.M2C9.isSelected();
                reloadConsole(true);
            }
        } else if(e.getSource() == console.M1I4) {
            saveAs();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == console) {
            hideConsole();
        }
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
    
}
