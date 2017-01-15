/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import jaddon.controller.StaticStandard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public class ServerSocketPlus implements ActionListener {
    
    public static final int STANDARDPORT = 1234;
    public static final int PORTMIN = 1000;
    public static final int PORTMAX = 0xFFFF;
    
    public static final String DISCOVERREQUEST = "DISCOVER_SERVER_REQUEST";
    public static final String DISCOVERRESPONSEPORT = "DISCOVER_SERVER_RESPONSE_PORT_";
    public static final String DISCOVERRESPONSEEMPTY = "DISCOVER_SERVER_RESPONSE_EMPTY";
    
    public static final ComMessage ALIVEMESSAGE = new ComMessage("ARE_YOU_STILL_ALIVE");
    public static final Object COMMANDBROADCAST = "BROADCAST";
    public static final Object COMMANDBROADCASTED = "THIS_WAS_BROADCASTED";
    
    public static final int PORTFORLOCALNETWORK = 8888;
    
    public static final ArrayList<Integer> PORTS = new ArrayList<>();
    protected static final HashMap<Integer, ServerSocketPlus> SERVERS = new HashMap<>();
    
    private ServerSocket serversocket = null;
    private int port = -1;
    private int delay = 100;
    private boolean run = true;
    private boolean run_update = false;
    private boolean process_inputs = false;
    private final ArrayList<Socket> sockets_accepted = new ArrayList<>(); //TODO Methode um sich auszuloggen oder so machen
    private final ArrayList<SocketPlus> sockets_monitored = new ArrayList<>();
    private ExecutorService executor = null;
    private final int threadPoolSize;
    private Thread thread_server = null;
    private final ServerSocketPlus serversocketplus = this;
    private boolean visible = true;
    private boolean reloading_sockets = false;
    protected static final Thread thread_discovery = new Thread(new Runnable() {

        DatagramSocket socketudp;

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                socketudp = new DatagramSocket(PORTFORLOCALNETWORK, InetAddress.getByName("0.0.0.0"));
                StaticStandard.log("Started UDP server on local network");
                while(true) {
                    try {
                        byte[] recBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recBuf, recBuf.length);
                        socketudp.receive(packet);
                        StaticStandard.log(String.format("UDP Server received packet data: \"%s\" from \"%s:%d\"", new String(packet.getData()).trim(), packet.getAddress().getHostAddress(), packet.getPort()));
                        String message = new String(packet.getData()).trim();
                        if(message.equals(DISCOVERREQUEST)) {
                            if(PORTS.size() > 0) {
                                for(int port : PORTS) {
                                    if(SERVERS.get(port).isVisible()) {
                                        try {
                                            byte[] sendData = (DISCOVERRESPONSEPORT + port).getBytes();
                                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                                            socketudp.send(sendPacket);
                                            StaticStandard.log(String.format("UDP Server sent packet data \"%s\" to \"%s:%d\"", new String(sendPacket.getData()).trim(), packet.getAddress().getHostAddress(), packet.getPort()));
                                        } catch (Exception ex) {
                                            StaticStandard.logErr("ERROR: " + ex, ex);
                                        }
                                    }
                                }
                            } else {
                                try {
                                    byte[] sendData = (DISCOVERRESPONSEEMPTY).getBytes();
                                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                                    socketudp.send(sendPacket);
                                    StaticStandard.log(String.format("UDP Server sent packet data \"%s\" to \"%s:%d\"", new String(sendPacket.getData()).trim(), packet.getAddress().getHostAddress(), packet.getPort()));
                                } catch (Exception ex) {
                                    StaticStandard.logErr("ERROR: " + ex, ex);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
                StaticStandard.logErr("Error while creating DatagramSocket: " + ex, ex);
                try {
                    Thread.sleep(1000);
                } catch (Exception ex2) {
                }
            }
            try {
                socketudp.close();
            } catch (Exception ex) {
            }
            StaticStandard.log("Stopped UDP server on local network");
            thread_discovery.start();
        }

    });
    private final Timer timer_reload_sockets = new Timer(2000, this); //FIXME change this to 100 (standard) or 500 maybe?
    
    public ServerSocketPlus(int port, boolean start, int threadPoolSize) {
        if(!thread_discovery.isAlive()) {
            try {
                thread_discovery.start();
            } catch (Exception ex) {
            }
        }
        this.threadPoolSize = threadPoolSize;
        executor = Executors.newFixedThreadPool(threadPoolSize);
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                setPort(port, start);
                thread_server = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        run = true;
                        while(run) {
                            try {
                                while(!run_update) {
                                    final Socket socket = serversocket.accept();
                                    Runnable run = new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                while(reloading_sockets) {
                                                    Thread.sleep(1);
                                                }
                                                sockets_accepted.add(socket);
                                                StaticStandard.log(String.format("Socket connected from: %s", socket.getInetAddress().getHostAddress()));
                                                if(process_inputs) {
                                                    monitor(socket);
                                                } else {
                                                    SocketEvent.socketConnected(new SocketEvent(socket, serversocketplus));
                                                }
                                            } catch (Exception ex) {
                                            }
                                        }

                                    };
                                    executor.execute(run);
                                }
                            } catch (Exception ex) {
                            }
                            boolean connected = reloadConnection();
                            while(run_update || !connected) {
                                try {
                                    int time = 100;
                                    if(!connected) {
                                        time *= 250;
                                    }
                                    Thread.sleep(time);
                                } catch (Exception ex) {
                                }
                            }
                            try {
                                Thread.sleep(delay);
                            } catch (Exception ex) {
                            }
                        }
                        StaticStandard.log("Finished server thread");
                        reloadThreads();
                    }

                });
                reloadThreads();
            }
        });
        thread.start();
    }
    
    public void monitor(Socket socket) {
        SocketPlus socketplus = new SocketPlus(socket, threadPoolSize) {
            
            @Override
            public void processInput(Object object) {
                if(object instanceof ComMessage) {
                    final ComMessage message = (ComMessage) object;
                    if(message.equals(SocketPlus.ALIVEMESSAGE)) {
                        //GOOD
                    }
                } else if(object instanceof Message) {
                    final Message message = (Message) object;
                    if(message.getCommand().equals(COMMANDBROADCAST)) {
                        final Message remessage = new Message(message.getMessage());
                        remessage.setFromInetAddress(this.getInetAddress());
                        remessage.setCommand(COMMANDBROADCASTED);
                        for(Socket socket : sockets_accepted) {
                            if((!message.getBlacklist().isEmpty() && message.getBlacklist().contains(socket.getInetAddress())) || (!message.getWhitelist().isEmpty() && !message.getWhitelist().contains(socket.getInetAddress()))) {
                                continue;
                            }
                            for(SocketPlus socketplus : sockets_monitored) {
                                if(socketplus.getSocket() != null && socketplus.getSocket().equals(socket)) {
                                    try {
                                        remessage.setTo(socket.getInetAddress(), socket.getLocalPort());
                                        socketplus.getObjectOutputStream().writeObject(remessage);
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    serversocketplus.processInput(object, this);
                }
            }
            
        };
        socketplus.setServerSocket(true);
        sockets_monitored.add(socketplus);
        SocketEvent.socketConnected(new SocketEvent(socketplus, serversocketplus));
    }
    
    public void processInput(Object object, SocketPlus socketplus) {
        //Do something...
    }
    
    public void addSocketListener(SocketListener socketlistener) {
        SocketEvent.addListener(socketlistener);
    }
    
    private boolean isConnected(SocketPlus socketplus) {
        if(socketplus == null) {
            return false;
        } else {
            try {
                socketplus.getObjectOutputStream().writeObject(ALIVEMESSAGE);
                socketplus.getObjectOutputStream().flush();
                return true;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while isConnecting socketplus: " + ex, ex);
                return false;
            }
        }
    }
    
    private boolean isConnected(Socket socket) {
        if(socket == null) {
            return false;
        } else {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(ServerSocketPlus.ALIVEMESSAGE);
                oos.flush();
                oos.close();
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    public void reloadSockets() {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                reloading_sockets = true;
                final ArrayList<Socket> sockets_accepted_temp = new ArrayList<>();
                final ArrayList<SocketPlus> sockets_monitored_temp = new ArrayList<>();
                for(Socket socket : sockets_accepted) {
                    SocketPlus socketplus = null;
                    for(SocketPlus socketplus_ : sockets_monitored) {
                        if(socketplus_ != null && socketplus_.getSocket() != null && socketplus_.getSocket().equals(socket)) {
                            socketplus = socketplus_;
                            break;
                        }
                    }
                    final boolean isConnected = ((socketplus != null) ? isConnected(socketplus) : isConnected(socket));
                    if(isConnected) {
                        sockets_accepted_temp.add(socket);
                        if(socketplus != null) {
                            sockets_monitored_temp.add(socketplus);
                        }
                        //StaticStandard.log(String.format("Client \"%s\" is still connected", socket.getInetAddress().getHostAddress()));
                    } else {
                        try {
                            if(socketplus != null) {
                                socketplus.disconnect();
                            }
                            socket.close();
                        } catch (Exception ex) {
                        }
                        StaticStandard.log(String.format("Client \"%s\" disconnected from the server", socket.getInetAddress().getHostAddress()));
                    }
                }
                sockets_accepted.clear();
                for(Socket socket : sockets_accepted_temp) {
                    sockets_accepted.add(socket);
                }
                sockets_monitored.clear();
                for(SocketPlus socketplus : sockets_monitored_temp) {
                    sockets_monitored.add(socketplus);
                }
                reloading_sockets = false;
            }
            
        });
        thread.start();
    }
    
    private void reloadThreads() {
        try {
            thread_server.start();
        } catch (Exception ex) {

        }
    }
    
    public boolean close() {
        run_update = true;
        timer_reload_sockets.stop();
        if(executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        final boolean isRunning_1 = (thread_server != null && thread_server.isAlive()) || (serversocket != null && !serversocket.isClosed());
        while((thread_server != null && thread_server.isAlive()) || (serversocket != null && !serversocket.isClosed())) {
            try {
                thread_server.stop();
            } catch (Exception ex) {
            }
            try {
                serversocket.close();
            } catch (Exception ex) {
            }
        }
        final boolean isRunning_2 = (thread_server != null && thread_server.isAlive()) || (serversocket != null && !serversocket.isClosed());
        if(PORTS.contains(this.port)) {
            PORTS.remove(PORTS.indexOf(this.port));
        }
        if(SERVERS.containsKey(this.port)) {
            SERVERS.remove(this.port);
        }
        if(isRunning_1 && !isRunning_2) {
            StaticStandard.log("Stopped server on port " + port);
        } else if(isRunning_1 && isRunning_2) {
            StaticStandard.log("Failed to stop server on port " + port);
        } else if(!isRunning_1 && !isRunning_2) {
            StaticStandard.log("Server already stopped on port " + port);
        } else if(!isRunning_1 && isRunning_2) {
            StaticStandard.log("Started server on port " + port);
        }
        run_update = false;
        return !reloadConnection();
    }
    
    public ServerSocket connectServer(boolean force) {
        run_update = true;
        if(!force && reloadConnection()) {
            StaticStandard.logErr("Error while connecting server socket, it is already connected!");
            run_update = false;
            return null;
        } else {
            boolean closed = close();
            if(!force && !closed) {
                StaticStandard.logErr("Error while connecting server socket, it is already connected!");
                run_update = false;
                return null;
            }
        }
        try {
            if(executor != null) {
                executor.shutdownNow();
                executor = null;
            }
            executor = Executors.newFixedThreadPool(threadPoolSize);
            serversocket = new ServerSocket(port);
            SERVERS.put(this.port, this);
            if(!PORTS.contains(this.port)) {
                PORTS.add(this.port);
            }
            timer_reload_sockets.start();
            StaticStandard.log(String.format("Started server on port %d with %d threads", port, threadPoolSize));
        } catch (Exception ex) {
            serversocket = null;
            StaticStandard.logErr("Error while creating server socket: " + ex, ex);
        }
        run_update = false;
        return serversocket;
    }
    
    public boolean reloadConnection() {
        try {
            boolean connected = !serversocket.isClosed();
            if(!connected) {
                sockets_accepted.clear();
                sockets_monitored.clear();
            }
            return connected;
        } catch (Exception ex) {
            sockets_accepted.clear();
            sockets_monitored.clear();
            return false;
        }
    }
    
    public int getAllThreadPoolSize() {
        int active_threads = threadPoolSize;
        for(SocketPlus socketplus : sockets_monitored) {
            active_threads += socketplus.getThreadPoolSize();
        }
        return active_threads;
    }
    
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public ServerSocket getServersocket() {
        return serversocket;
    }

    public void setServersocket(ServerSocket serversocket) {
        run_update = true;
        this.serversocket = serversocket;
        run_update = false;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port, boolean reconnect) {
        if(port < 0 || port < PORTMIN || port > PORTMAX) {
            port = STANDARDPORT;
        }
        if(PORTS.contains(port) || SERVERS.get(port) != null) {
            StaticStandard.logErr("A server is already running on this port");
            return;
        }
        this.port = port;
        if(reconnect) {
            connectServer(true);
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isProcessInputs() {
        return process_inputs;
    }

    public void setProcessInputs(boolean process_inputs) {
        this.process_inputs = process_inputs;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer_reload_sockets) {
            reloadSockets();
        }
    }
    
}
