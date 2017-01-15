/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class Message implements Serializable {
    
    private Object message = null;
    private Object command = null;
    private ArrayList<InetAddress> whitelist = new ArrayList<>();
    private ArrayList<InetAddress> blacklist = new ArrayList<>();
    private SocketPlus socketplus = null;
    private ServerSocketPlus serversocketplus = null;
    private InetAddress from_inetaddress = null;
    private int from_port = -1;
    private InetAddress to_inetaddress = null;
    private int to_port = -1;
    private Instant timestamp = Instant.now();
    
    public Message() {
        
    }
    
    public Message(Object message) {
        this(message, null);
    }
    
    public Message(Object message, SocketPlus socketplus) {
        this(message, socketplus, null);
    }
    
    public Message(Object message, SocketPlus socketplus, ServerSocketPlus serversocketplus) {
        this.message = message;
        this.socketplus = socketplus;
        this.serversocketplus = serversocketplus;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
        timestamp = Instant.now();
    }

    public SocketPlus getSocketplus() {
        return socketplus;
    }

    public void setSocketplus(SocketPlus socketplus) {
        this.socketplus = socketplus;
    }

    public ServerSocketPlus getServersocketplus() {
        return serversocketplus;
    }

    public void setServersocketplus(ServerSocketPlus serversocketplus) {
        this.serversocketplus = serversocketplus;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public InetAddress getFromInetAddress() {
        return from_inetaddress;
    }

    public void setFromInetAddress(InetAddress from_inetaddress) {
        this.from_inetaddress = from_inetaddress;
    }

    public int getFromPort() {
        return from_port;
    }

    public void setFromPort(int from_port) {
        this.from_port = from_port;
    }

    public InetAddress getToInetAddress() {
        return to_inetaddress;
    }

    public void setToInetAddress(InetAddress to_inetaddress) {
        this.to_inetaddress = to_inetaddress;
    }

    public int getToPort() {
        return to_port;
    }

    public void setToPort(int to_port) {
        this.to_port = to_port;
    }
    
    public void setFrom(InetAddress from_inetaddress, int from_port) {
        setFromInetAddress(from_inetaddress);
        setFromPort(from_port);
    }
    
    public void setTo(InetAddress to_inetaddress, int to_port) {
        setToInetAddress(to_inetaddress);
        setToPort(to_port);
    }

    public Object getCommand() {
        return command;
    }

    public void setCommand(Object command) {
        this.command = command;
    }

    public ArrayList<InetAddress> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(ArrayList<InetAddress> whitelist) {
        this.whitelist = whitelist;
    }

    public ArrayList<InetAddress> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(ArrayList<InetAddress> blacklist) {
        this.blacklist = blacklist;
    }
    
}
