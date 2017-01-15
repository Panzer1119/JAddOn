/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.controller;

import jaddon.exceptions.UnsupportedCharacterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Paul
 */
public class Command {
    
    public static final HashMap<String, Command> COMMANDS = new HashMap<>();
    
    private String command = "";
    private String help = "";
    private boolean usesArguments = false;
    private boolean runAlways = false;
    
    public static final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public Command(String command) {
        try {
            setCommand(command);
        } catch (UnsupportedCharacterException uce) {
            StaticStandard.logErr(uce);
        }
    }
    
    public void run(String arguments) {
        StaticStandard.log(this);
    }
    
    private void preRun(String arguments) {
        if(!arguments.isEmpty() && !usesArguments) {
            StaticStandard.logErr("Command uses no arguments");
            return;
        }
        run(arguments);
    }
    
    public void delete() {
        COMMANDS.remove(command);
    }
    
    public static String[] getArguments(String arguments) {
        return Command.getArguments(arguments, " ");
    }
    
    private static String[] getArguments(String arguments, String delimiter) {
        boolean isArg = false;
        boolean conts_a = arguments.contains("\"");
        String temp = "";
        final ArrayList<String> args = new ArrayList<>();
        for(int i = 0; i < arguments.length(); i++) {
            char c = arguments.charAt(i);
            String c_string = "" + c;
            if(c_string.equals("\"")) {
                isArg = !isArg;
            } else {
                if((!c_string.equals(delimiter) || isArg)) {
                    temp += c;
                } else {
                    if(!temp.isEmpty()) {
                        args.add(temp);
                    }
                    temp = "";
                }
            }
        }
        if(!temp.isEmpty()) {
            args.add(temp);
        }
        String[] args_s = args.toArray(new String[args.size()]);
        return args_s;
    }
    
    public static boolean runCommand(String command) {
        for(String c : COMMANDS.keySet()) {
            try {
                if(((command.contains(" ") ? command.substring(0, command.indexOf(" ")) : command)).equals(c)) {
                    String arguments_temp = "";
                    if(command.contains(" ")) {
                        arguments_temp = command.substring(command.indexOf(" ") + 1);
                    }
                    final String arguments = arguments_temp;
                    Command cc = COMMANDS.get(c);
                    Runnable run = new Runnable() {
                        
                        @Override
                        public void run() {
                            try {
                                if(cc.isRunningAlways()) {
                                    COMMANDS.get(c).run(arguments);
                                } else {
                                    COMMANDS.get(c).preRun(arguments);
                                }
                            } catch (Exception ex) {
                            }
                        }
                        
                    };
                    Runnable run_2 = new Runnable() {
                        
                        @Override
                        public void run() {
                            try {
                                CommandEvent.commandExecuted(new CommandEvent(Command.class, COMMANDS.get(c), arguments));
                            } catch (Exception ex) {
                            }
                        }
                        
                    };
                    executor.execute(run);
                    executor.execute(run_2);
                    return true;
                }
            } catch (Exception ex) {
                StaticStandard.logErr("Error while searching for a command: " + ex);
            }
        }
        return false;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) throws UnsupportedCharacterException {
        if(command.contains(" ")) {
            StackTraceElement e = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];
            throw new UnsupportedCharacterException(String.format("%s.%s:%s UnsupportedCharacterException at %d", e.getClassName(), e.getMethodName(), e.getLineNumber(), command.indexOf(" ")));
        }
        delete();
        this.command = command;
        COMMANDS.put(command, this);
    }

    public boolean isUsingArguments() {
        return usesArguments;
    }

    public void setUseArguments(boolean usesArguments) {
        this.usesArguments = usesArguments;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public boolean isRunningAlways() {
        return runAlways;
    }

    public void setRunAlways(boolean runAlways) {
        this.runAlways = runAlways;
    }
    
    @Override
    public String toString() {
        return command + ": " + help;
    }
    
    @Deprecated
    public static String[] getArgumentsOld(String arguments) {
        return getArguments(arguments, " ");
    }
    
    @Deprecated
    public static String[] getArgumentsOld(String arguments, String split) {
        return arguments.split(split);
    }
    
}
