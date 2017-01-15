/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import jaddon.controller.StaticStandard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public class Server implements ActionListener, Serializable {
    
    public static final int STANDARDPORT = 1234;
    public static final int PORTMIN = 1000;
    public static final int PORTMAX = 0xFFFF;
    public static final int THREADSTOPWAITTIME = 100;
    public static final int MAXTHREADSTOPTIME = 1000;
    
    public static final ArrayList<Integer> PORTS = new ArrayList<>();
    
    public final Server server = this;
    private ServerSocket serversocket = null;
    private final HashMap<Client, Thread> clients_connected = new HashMap<>();
    private int port = -1;
    private boolean started = false;
    private boolean running = false;
    private InputProcessor inputprocessor = null;
    private int max_client_reloading_time = 1000;
    private int max_client_reloading_tries = 3;
    private Duration max_reload_duration = Duration.ofMillis(1000);
    private final Timer timer = new Timer(1000, this);
    
    public Server(int port) {
        setPort(port);
        start();
    }
    
    public final Thread startWThread() {
        Runnable run = new Runnable() {
            
            @Override
            public void run() {
                start();
            }
            
        };
        return StaticStandard.execute(run);
    }
    
    public final Server start() {
        try {
            boolean port_valid = checkPortAvailable(port);
            if(port_valid) {
                stopThread(thread_receive);
                serversocket = new ServerSocket(port);
                started = true;
                registerServerPort(port);
                StaticStandard.log("Started server on port: " + port);
                startThread(thread_receive);
                timer.start();
            } else {
                StaticStandard.logErr("Failed starting server on port: " + port + ", port already binded");
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while starting server: " + ex, ex);
        }
        return this;
    }
    
    public final Server stop() {
        timer.stop();
        stopThread(thread_receive);
        //FIXME DO THIS MIT entfernung des aktiven ports
        started = false;
        running = false;
        return this;
    }
    
    public final Server setPort(int port) {
        if(!checkPort(port)) {
            port = STANDARDPORT;
        }
        this.port = port;
        return this;
    }
    
    public final int getPort() {
        return port;
    }
    
    public final ServerSocket getServerSocket() {
        return serversocket;
    }

    public final int getMaxClientReloadingTime() {
        return max_client_reloading_time;
    }

    public final Server setMaxClientReloadingTime(int max_client_reloading_time) {
        this.max_client_reloading_time = max_client_reloading_time;
        return this;
    }

    public final int getMaxClientReloadingTies() {
        return max_client_reloading_tries;
    }

    public final Server setMaxClientReloadingTries(int max_client_reloading_tries) {
        this.max_client_reloading_tries = max_client_reloading_tries;
        return this;
    }
    
    public final Server setInputProcessor(InputProcessor inputprocessor) {
        this.inputprocessor = inputprocessor;
        return this;
    }

    public final Duration getMaxReloadDuration() {
        return max_reload_duration;
    }

    public final Server setMaxReloadDuration(Duration max_reload_duration) {
        this.max_reload_duration = max_reload_duration;
        return this;
    }
    
    public final InputProcessor getInputProcessor() {
        return inputprocessor;
    }
    
    public static final boolean checkPort(int port) {
        return (port >= PORTMIN && port <= PORTMAX);
    }
    
    public static final boolean checkPortAvailable(int port) {
        return !PORTS.contains(port);
    }
    
    private final Thread reloadClientsWThread() {
        Runnable run = new Runnable() {
            
            @Override
            public void run() {
                reloadClients();
            }
            
        };
        return StaticStandard.execute(run);
    }
    
    private final void reloadClients() {
        try {
            final Instant instant_now = Instant.now();
            final ArrayList<Client> clients_disconnected = new ArrayList<>();
            final ArrayList<Thread> threads = new ArrayList<>();
            for(Client client : clients_connected.keySet()) {
                if(client == null) {
                    continue;
                }
                Runnable run = new Runnable() {
                    
                    @Override
                    public void run() {
                        try {
                            boolean isconnected = false;
                            ObjectOutputStream oos = client.getObjectOutputStream();
                            for(int i = 0; i < max_client_reloading_tries; i++) {
                                oos.writeObject(MessageState.AREYOUALIVE);
                                int time = 10;
                                int count = 0;
                                Duration duration = Duration.between(instant_now, client.last_check);
                                while(((count * time) < max_client_reloading_time) && (duration.isNegative() || duration.compareTo(max_reload_duration) > 0)) {
                                    try {
                                        duration = Duration.between(instant_now, client.last_check);
                                        Thread.sleep(time);
                                    } catch (Exception ex) {
                                    }
                                }
                                if(!(duration.isNegative() || duration.compareTo(max_reload_duration) > 0)) {
                                    isconnected = true;
                                }
                                if(isconnected) {
                                    break;
                                }
                            }
                            if(!isconnected) {
                                StaticStandard.logErr(String.format("Client %s timed out (after %d tries with %d ms)", formatAddress(client.getInetaddress()), max_client_reloading_tries, max_client_reloading_time));
                                clients_disconnected.add(client);
                            }
                        } catch (Exception ex) {
                            StaticStandard.logErr("Error while reloding client " + formatAddress(client.getInetaddress()) + ": " + ex);
                        }
                    }
                    
                };
                threads.add(StaticStandard.execute(run));
            }
            boolean finished = false;
            while(!finished) {
                boolean alive = false;
                for(Thread thread : threads) {
                    if(thread.isAlive()) {
                        alive = true;
                        break;
                    }
                }
                if(!alive) {
                    finished = true;
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
            }
            for(Client client : clients_disconnected) {
                logout(client, true);
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while reloading clients: " + ex, ex);
        }
    }
    
    private final void logout(Client client, boolean remove) {
        stopThread(clients_connected.get(client));
        if(remove) {
            clients_connected.remove(client);
            client.stop();
        }
        StaticStandard.log(String.format("Client %s logged out", formatAddress(client.getInetaddress())));
    }
    
    private final void processInputRAW(final Client client, final Object object, Instant instant) {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    if(object instanceof MessageState) {
                        final MessageState message = (MessageState) object;
                        Object answer = null;
                        switch(message) {
                            case LOGIN:
                                answer = new ComMessage(MessageState.ANSWERNO);
                                break;
                            case LOGOUT:
                                answer = new ComMessage(MessageState.ANSWERYES);
                                logout(client, true);
                                break;
                            case BROADCAST:
                                answer = new ComMessage(MessageState.ANSWERNO);
                                break;
                            case ANSWERYES:
                                break;
                            case ANSWERNO:
                                break;
                            case PING:
                                answer = new ComMessage(MessageState.ANSWERYES);
                                break;
                            case AREYOUALIVE:
                                break;
                            case IMALIVE:
                                client.last_check = instant;
                                break;
                            default:
                                break;
                        }
                        if(answer != null) {
                            client.getObjectOutputStream().writeObject(answer);
                        }
                    } else {
                        inputprocessor.processInput(object);
                    }
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while processing on server raw inputs: " + ex, ex);
                }
            }
            
        };
        client.getExecutorInputProcessor().execute(run);
    }
    
    public final Client getClient(InetAddress inetaddress) {
        try {
            for(Client client : clients_connected.keySet()) {
                if(client.getInetaddress().equals(inetaddress)) {
                    return client;
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public final Thread processSocket(Socket socket) {
        if(socket == null) {
            return null;
        }
        try {
            final Client client = new Client(socket);
            client.isServerClient = false;
            Thread thread = new Thread(new Runnable() {

                @Override
                public synchronized void run() {
                    try {
                        ObjectOutputStream oos = client.getObjectOutputStream();
                        ObjectInputStream ois = client.getObjectInputStream();
                        Object object = null;
                        while(((object = ois.readObject()) != null)) {
                            processInputRAW(client, object, Instant.now());
                        }
                        StaticStandard.log(String.format("Connection to %s closed", client.getInetaddress().getHostAddress()));
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while processing socket: " + ex, ex);
                    } finally {
                        if(clients_connected.containsKey(client)) {
                            clients_connected.remove(client);
                        }
                    }
                }

            });
            clients_connected.put(client, thread);
            thread.start();
            return thread;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while processing socket: " + ex, ex);
            return null;
        }
    }
    
    private final Thread thread_receive = new Thread(new Runnable() {
        
        @Override
        public synchronized void run() {
            try {
                while(started && running) {
                    try {
                        final Socket socket = serversocket.accept();
                        StaticStandard.log(String.format("Socket connected from: %s", socket.getInetAddress().getHostAddress()));
                        processSocket(socket);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while socket connected to the server: " + ex, ex);
                    }
                }
            } catch (Exception ex) {
                StaticStandard.logErr("Error in the server receive thread: " + ex, ex);
            }
        }
    
    });
    
    public synchronized final static boolean registerServerPort(int port) {
        if(checkPort(port) && checkPortAvailable(port)) {
            PORTS.add(port);
            return true;
        } else {
            return false;
        }
    }
    
    public synchronized final static boolean unregisterServerPort(int port) {
        if(checkPort(port) && !checkPortAvailable(port)) {
            PORTS.remove(PORTS.indexOf(port));
            return true;
        } else {
            return false;
        }
    }
    
    public synchronized final static void stopThread(Thread thread) {
        if(thread == null) {
            return;
        }
        int times = 0;
        while(thread.isAlive() && (times * THREADSTOPWAITTIME) <= MAXTHREADSTOPTIME) {
            try {
                thread.interrupt();
                thread.stop();
                Thread.sleep(THREADSTOPWAITTIME);
            } catch (Exception ex) {
            }
            times++;
        }
    }
    
    public synchronized final static void startThread(Thread thread) {
        if(thread == null) {
            return;
        }
        thread.start();
    }
    
    public synchronized final static boolean clientConnected(Client client) {
        
        return false;
    }
    
    public final static String formatAddress(InetAddress inetaddress) {
        return String.format("\"%s", ((inetaddress != null) ? inetaddress.getHostAddress() : ""));
    }
    
    public final static String formatAddressAndPort(InetAddress inetaddress, int port) {
        return String.format("\"%s:%d\"", ((inetaddress != null) ? inetaddress.getHostAddress() : ""), port);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            reloadClientsWThread();
        }
    }
    
}
