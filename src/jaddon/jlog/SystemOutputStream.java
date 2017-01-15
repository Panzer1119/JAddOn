/*
 * To doFinal this license header, choose License Headers in Project Properties.
 * To doFinal this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlog;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Paul
 */
public class SystemOutputStream extends PrintStream {
    
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private JLogger logger = null;

    public SystemOutputStream(OutputStream out, JLogger logger) {
        super(out);
        this.logger = logger;
    }
    
    public String doFinal(Object o, Instant instant, Thread thread, StackTraceElement e) {
        String prefix = "[" + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(dtf) + "]: ";
        String suffix = "";
        String output = prefix + o + suffix;
        return output;
    }
    
    @Override
    public void print(char c) {
        print("" + c);
    }
    
    @Override
    public void print(long l) {
        print("" + l);
    }
    
    @Override
    public void print(double d) {
        print("" + d);
    }
    
    @Override
    public void print(float f) {
        print("" + f);
    }
    
    @Override
    public void print(boolean b) {
        print("" + b);
    }
    
    @Override
    public void print(int i) {
        print("" + i);
    }
    
    @Override
    public void print(char[] c) {
        print(new String(c));
    }
    
    @Override
    public void print(Object o) {
        print((String) o);
    }
    
    @Override
    public void print(String g) {
        super.print(doFinal(g, Instant.now(), logger.getThread(), logger.getStackTraceElement()));
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dtf;
    }

    public void setDateTimeFormatter(DateTimeFormatter dtf) {
        this.dtf = dtf;
    }
    
}
