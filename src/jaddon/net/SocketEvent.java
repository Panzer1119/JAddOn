/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class SocketEvent {
    
    private static final ArrayList<SocketListener> socketlisteners = new ArrayList<>();
    
    public static void addListener(SocketListener socketlistener) {
        socketlisteners.add(socketlistener);
    }
    
    public static void socketConnected(SocketEvent socketevent) {
        for(SocketListener socketlistener : socketlisteners) {
            try {
                socketlistener.socketConnected(socketevent);
            } catch (Exception ex) {
            }
        }
    }

    public static ArrayList<SocketListener> getSocketListeners() {
        return socketlisteners;
    }
    
    private final Socket socket;
    private SocketPlus socketplus = null;
    private final ServerSocketPlus serversocketplus;
    
    public SocketEvent(Socket socket, ServerSocketPlus serversocketplus) {
        this.socket = socket;
        this.serversocketplus = serversocketplus;
    }
    
    public SocketEvent(SocketPlus socketplus, ServerSocketPlus serversocketplus) {
        this.socket = socketplus.getSocket();
        this.socketplus = socketplus;
        this.serversocketplus = serversocketplus;
    }

    public Socket getSocket() {
        return socket;
    }

    public ServerSocketPlus getServersocketplus() {
        return serversocketplus;
    }

    public SocketPlus getSocketPlus() {
        return socketplus;
    }

    public void setSocketPlus(SocketPlus socketplus) {
        this.socketplus = socketplus;
    }
    
}
