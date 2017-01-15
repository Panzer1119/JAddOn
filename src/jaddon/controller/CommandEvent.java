/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.controller;

import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class CommandEvent {
    
    private static final ArrayList<CommandListener> commandlisteners = new ArrayList<>();
    
    public static void addListener(CommandListener commandlistener) {
        commandlisteners.add(commandlistener);
    }
    
    public static void commandExecuted(CommandEvent commandevent) {
        for(CommandListener lommandlistener : commandlisteners) {
            try {
                lommandlistener.commandExecuted(commandevent);
            } catch (Exception ex) {
            }
        }
    }

    public static ArrayList<CommandListener> getCommandListeners() {
        return commandlisteners;
    }
    
    private final Class class_executing;
    private final Command command;
    private final String arguments;
    
    public CommandEvent(Class class_executing, Command command, String arguments) {
        this.class_executing = class_executing;
        this.command = command;
        this.arguments = arguments;
    }

    public Class getClass_executing() {
        return class_executing;
    }

    public Command getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
    
}
