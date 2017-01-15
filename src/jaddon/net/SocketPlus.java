/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import jaddon.controller.StaticStandard;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Paul
 */
public class SocketPlus implements Serializable {
   
    public static final int STANDARDPORT = 1234;
    public static final int PORTMIN = 1000;
    public static final int PORTMAX = 0xFFFF;
    
    public static final ComMessage ALIVEMESSAGE = new ComMessage("YES_IM_STILL_ALIVE");
    
    private Socket socket = null;
    private InetAddress inetaddress = null;
    private int port = -1;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private ExecutorService executor = null;
    private final int threadPoolSize;
    private Thread thread = null;
    private int maxConnectionTimes = 10;
    private int delayTime = 1000;
    private boolean stopped = false;
    private boolean server_socket = false;
    private boolean run_ = false;
    private final SocketPlus socketplus = this;
    //UDP SERVER FINDING
    private static Instant instant_last = Instant.now();
    
    public SocketPlus(Socket socket, int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.socket = socket;
        StaticStandard.log(String.format("Started client with %d threads connecting to %s:%d", threadPoolSize, socket.getInetAddress().getHostAddress(), socket.getPort()));
        setInetaddress(socket.getInetAddress());
        setPort(socket.getPort());
        connect(false);
    }
    
    public SocketPlus(InetAddress inetaddress, int port, boolean connect, int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        StaticStandard.log(String.format("Started client with %d threads to connect to %s:%d", threadPoolSize, inetaddress.getHostAddress(), port));
        setInetaddress(inetaddress);
        setPort(port);
        if(connect) {
            connect(true);
        }
    }
    
    public void processInput(Object object) {
        
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InetAddress getInetAddress() {
        return inetaddress;
    }

    public void setInetaddress(InetAddress inetaddress) {
        if(inetaddress != null) {
            this.inetaddress = inetaddress;
        } else {
            try {
                this.inetaddress = InetAddress.getLocalHost();
            } catch (Exception ex) {
                this.inetaddress = null;
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if(port < PORTMIN || port > PORTMAX) {
            port = STANDARDPORT;
        }
        this.port = port;
    }

    public void disconnect() {
        stopped = true;
        run_ = false;
        if(executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        try {
            oos.close();
        } catch (Exception ex) {
        }
        try {
            ois.close();
        } catch (Exception ex) {
        }
        try {
            socket.close();
            while(!socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ex) {
        }
        oos = null;
        ois = null;
        while(thread != null && thread.isAlive()) {
            try {
                thread.interrupt();
                thread.stop();
            } catch (Exception ex) {
            }
        }
        thread = null;
        stopped = true;
        run_ = false;
        StaticStandard.log("Successfully stopped client");
    }
    
    public void sendMessage(Object object) {
        if(socket == null || !socket.isConnected() || socket.isClosed() || oos == null) {
            return;
        }
        Runnable run = new Runnable() {
            
            @Override
            public void run() {
                try {
                    Message message = new Message(object, socketplus);
                    try {
                        message.setFrom(InetAddress.getLocalHost(), port);
                    } catch (Exception ex) {
                    }
                    message.setTo(inetaddress, port);
                    oos.writeObject(message);
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while sending message: " + ex, ex);
                }
            }
            
        };
        executor.execute(run);
    }
    
    public void connect(boolean createNew) {
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                stopped = false;
                run_ = true;
                int i = 0;
                while(run_) {
                    try {
                        if((!server_socket && createNew) || socket == null) {
                            if(executor != null) {
                                executor.shutdownNow();
                                executor = null;
                            }
                            executor = Executors.newFixedThreadPool(threadPoolSize);
                            StaticStandard.log("Started new connecting to \"" + inetaddress.getHostAddress() + ":" + port + "\"");
                            socket = new Socket(inetaddress, port);
                            StaticStandard.log("Connected new successfully to \"" + inetaddress.getHostAddress() + "\"");
                        }
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        ois = new ObjectInputStream(socket.getInputStream());
                        try {
                            boolean ioexception = false;
                            Object object = null;
                            while(!ioexception) {
                                try {
                                    final Object object_temp = ois.readObject();
                                    Runnable run = new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                if(object_temp instanceof ComMessage) {
                                                    final ComMessage message = (ComMessage) object_temp;
                                                    if(message.equals(ServerSocketPlus.ALIVEMESSAGE)) {
                                                        oos.writeObject(SocketPlus.ALIVEMESSAGE);
                                                    }
                                                } else if(object_temp instanceof Message) {
                                                    final Message message = (Message) object_temp;
                                                    if(message.getCommand().equals(ServerSocketPlus.COMMANDBROADCASTED)) {
                                                        StaticStandard.log(String.format("[%s] Broadcast from \"%s\": \"%s\"", inetaddress.getHostAddress(), message.getFromInetAddress().getHostAddress(), message.getMessage()));
                                                    }
                                                } else {
                                                    processInput(object_temp);
                                                }
                                            } catch (Exception ex) {
                                            }
                                        }

                                    };
                                    executor.execute(run);
                                } catch (IOException ex) {
                                    StaticStandard.logErr("Disconnected from server: " + ex, ex);
                                    ioexception = true;
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            if(!stopped) {
                                connect(true);
                            }
                            run_ = false;
                        }
                        run_ = false;
                        break;
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while connecting to \"" + inetaddress.getHostAddress() + ":" + port + "\": " + ex/*, ex*/);
                    }
                    try {
                        Thread.sleep(delayTime);
                    } catch (Exception ex) {
                    }
                    i++;
                    if(i > maxConnectionTimes || server_socket) {
                        break;
                    }
                }
            }

        });
        thread.start();
    }
    
    public ObjectOutputStream getObjectOutputStream() {
        int i = 0;
        while(oos == null) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                
            }
            i++;
            if(i > 100) {
                break;
            }
        }
        return oos;
    }

    public ObjectInputStream getObjectInputStream() {
        int i = 0;
        while(oos == null) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                
            }
            i++;
            if(i > 100) {
                break;
            }
        }
        return ois;
    }

    public int getMaxConnectionTimes() {
        return maxConnectionTimes;
    }

    public void setMaxConnectionTimes(int maxConnectionTimes) {
        this.maxConnectionTimes = maxConnectionTimes;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    
    public static HashMap<InetAddress, ArrayList<Integer>> getLocalServer() {
        HashMap<InetAddress, ArrayList<Integer>> server = new HashMap<>();
        try {
            final DatagramSocket socketudp = new DatagramSocket();
            socketudp.setBroadcast(true);
            final byte[] sendData = ServerSocketPlus.DISCOVERREQUEST.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), ServerSocketPlus.PORTFORLOCALNETWORK);
                socketudp.send(sendPacket);
            } catch (Exception ex) {
            }
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                try {
                    NetworkInterface networkinterface = (NetworkInterface) interfaces.nextElement();
                    if(networkinterface.isLoopback() || !networkinterface.isUp()) {
                        continue;
                    }
                    for(InterfaceAddress interfaceaddress : networkinterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceaddress.getBroadcast();
                        if(broadcast == null) {
                            continue;
                        }
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, ServerSocketPlus.PORTFORLOCALNETWORK);
                            socketudp.send(sendPacket);
                            StaticStandard.log(String.format("Request packet sent to: \"%s\"; Interface: \"%s\"", broadcast.getHostAddress(), networkinterface.getDisplayName()));
                        } catch (Exception ex) {
                        }
                    }
                } catch (Exception ex) {
                }
            }
            final Instant instant_start = Instant.now();
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    boolean run_ = true;
                    while(run_) {
                        try {
                            byte[] recBuf = new byte[15000];
                            DatagramPacket receivePacket = new DatagramPacket(recBuf, recBuf.length);
                            socketudp.receive(receivePacket);
                            String message = new String(receivePacket.getData()).trim();
                            instant_last = Instant.now();
                            if(message.equals(ServerSocketPlus.DISCOVERRESPONSEEMPTY)) {
                                StaticStandard.log("No server available!");
                                break;
                            } else if(message.startsWith(ServerSocketPlus.DISCOVERRESPONSEPORT)) {
                                try {
                                    String port_string = message.substring(ServerSocketPlus.DISCOVERRESPONSEPORT.length());
                                    int port = Integer.parseInt(port_string);
                                    if(server.containsKey(receivePacket.getAddress()) && !server.get(receivePacket.getAddress()).contains(port)) {
                                        server.get(receivePacket.getAddress()).add(port);
                                    } else {
                                        ArrayList<Integer> ports = new ArrayList<>();
                                        ports.add(port);
                                        server.put(receivePacket.getAddress(), ports);
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        } catch (Exception ex) {
                            
                        }
                    }
                    socketudp.close();
                }
                
            });
            thread.start();
            while(thread.isAlive()) {
                if(Duration.between(instant_last, Instant.now()).toMillis() > 100) {
                    try {
                        thread.stop();
                        socketudp.close();
                    } catch (Exception ex) {
                        
                    }
                }
            }
            return server;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while finding local server: " + ex);
            return null;
        }
    }
    
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public boolean isServerSocket() {
        return server_socket;
    }

    public void setServerSocket(boolean server_socket) {
        this.server_socket = server_socket;
    }
    
}
