/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlog;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Paul
 */
public class LogEntry implements Serializable {
    
    public static final int LEVELERROR = -1;
    public static final int LEVELLOW = 0;
    public static final int LEVELNORMAL = 1;
    public static final int LEVELINPUT = 2;
    public static final int LEVELCOMMAND = 3;
    
    public static final String[] LEVELNAMES = new String[]{"ERROR", "LOW", "NORMAL", "INPUT", "COMMAND"};
    
    private Object logentry = null;
    private Instant timestamp = null;
    private Thread thread = null;
    private StackTraceElement stacktraceelement = null;
    private Exception exception = null;
    private String datetimeformat = "dd.MM.yyyy HH:mm:ss";
    private int level = -2;
    private boolean printTimestamp = false;
    private boolean printExtraInformation = false;
    private boolean printLevel = false;
    private boolean debug = false;

    public LogEntry(Object logentry, Instant timestamp, int level, String datetimeformat, Thread thread, StackTraceElement stacktraceelement) {
        this.logentry = logentry;
        this.timestamp = timestamp;
        this.level = level;
        this.datetimeformat = datetimeformat;
        this.thread = thread;
        this.stacktraceelement = stacktraceelement;
    }

    public Object getLogEntry() {
        return logentry;
    }

    public void setLogEntry(Object logentry) {
        this.logentry = logentry;
    }

    public Instant getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public StackTraceElement getStackTraceElement() {
        return stacktraceelement;
    }

    public void setStackTraceElement(StackTraceElement stacktraceelement) {
        this.stacktraceelement = stacktraceelement;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDateTimeFormat() {
        return datetimeformat;
    }

    public void setDateTimeFormat(String datetimeformat) {
        this.datetimeformat = datetimeformat;
    }

    public boolean isPrintTimestamp() {
        return printTimestamp;
    }

    public void setPrintTimestamp(boolean printTimestamp) {
        this.printTimestamp = printTimestamp;
    }

    public boolean isPrintExtraInformation() {
        return printExtraInformation;
    }

    public void setPrintExtraInformation(boolean printExtraInformation) {
        this.printExtraInformation = printExtraInformation;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isPrintLevel() {
        return printLevel;
    }

    public void setPrintLevel(boolean printLevel) {
        this.printLevel = printLevel;
    }
    
    public String getLevelName() {
        return LEVELNAMES[level + 1];
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
    
    @Override
    public String toString() {
        String temp_datetime = String.format("[%s]", LocalDateTime.ofInstant(getTimeStamp(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(datetimeformat)));
        String temp_extrainformation_format = "";
        String temp_extrainformation = "";
        if(getThread() != null) {
            temp_extrainformation_format = "[%s] [%s.%s(%s:%s)]";
            temp_extrainformation = String.format(temp_extrainformation_format, thread.getName(), getStackTraceElement().getClassName(), getStackTraceElement().getMethodName(), getStackTraceElement().getFileName(), getStackTraceElement().getLineNumber());
        } else {
            temp_extrainformation_format = "[%s.%s(%s:%s)]";
            temp_extrainformation = String.format(temp_extrainformation_format, getStackTraceElement().getClassName(), getStackTraceElement().getMethodName(), getStackTraceElement().getFileName(), getStackTraceElement().getLineNumber());
        }
        String temp_level = String.format("[%s]", getLevelName());
        Object msg = getLogEntry();
        String output = "";
        if(printTimestamp || debug) {
            output += temp_datetime;
        }
        if(printExtraInformation || debug) {
            if(printTimestamp || debug) {
                output += " ";
            }
            output += temp_extrainformation;
        }
        if(printLevel || debug) {
            if(printExtraInformation || printTimestamp || debug) {
                output += " ";
            }
            output += temp_level;
        }
        if(printTimestamp || printExtraInformation || printLevel || debug) {
            output += ": ";
        }
        output += msg;
        if(level == LEVELERROR && exception != null) {
            for(StackTraceElement e : exception.getStackTrace()) {
                output += "\n" + e;
            }
        }
        return output;
    }
    
}
