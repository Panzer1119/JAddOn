/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.net;

import jaddon.controller.StaticStandard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
    
    /**
     * Integer Standard port
     */
    public static final int STANDARDPORT = 1234;
    /**
     * Integer Minimum port
     */
    public static final int PORTMIN = 1000;
    /**
     * Integer Maximum port
     */
    public static final int PORTMAX = 0xFFFF;
    /**
     * Integer Delay for the thread stop function
     */
    public static final int THREADSTOPWAITTIME = 10;
    /**
     * Integer Maximum time to be wait for of stopping a thread
     */
    public static final int MAXTHREADSTOPTIME = 5000;
    
    /**
     * ArrayList Integer All used ports
     */
    public static final ArrayList<Integer> PORTS = new ArrayList<>();
    
    /**
     * Server Instance of this object
     */
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
    private Instant instant_started = null;
    private Instant instant_stopped = null;
    
    /**
     * Constructor for the Server
     * @param port Integer Port
     */
    public Server(int port) {
        init();
        setPort(port);
    }
    
    private final void init() {
        resetThreadReceive();
    }
    
    /**
     * Starts the server within an extra thread
     * @return Thread The Thread that is starting the Server
     */
    public final Thread startWThread() {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                start();
            }
            
        };
        Thread thread = StaticStandard.execute(run);
        thread.setName("Thread-Server-Start");
        return thread;
    }
    
    /**
     * Starts the server
     * @return Server This Server
     */
    public final Server start() {
        try {
            boolean port_valid = checkPortAvailable(port);
            if(port_valid) {
                stopThread(thread_receive);
                serversocket = new ServerSocket(port);
                instant_started = Instant.now();
                started = true;
                registerServerPort(port);
                StaticStandard.log("[SERVER] Started server on port: " + port);
                resetThreadReceive();
                startThread(thread_receive);
                timer.start();
            } else {
                instant_started = null;
                StaticStandard.logErr("[SERVER] Failed starting server on port: " + port + ", port already binded");
            }
        } catch (Exception ex) {
            instant_started = null;
            StaticStandard.logErr("[SERVER] Error while starting server: " + ex, ex);
        }
        return this;
    }
    
    /**
     * Stops the server
     * @return Server This Server
     */
    public final Server stop() {
        if(!started) {
            return this;
        }
        try {
            timer.stop();
            for(Client client : clients_connected.keySet()) {
                stopThread(clients_connected.get(client));
            }
            clients_connected.clear();
            serversocket.close();
            started = !serversocket.isClosed();
            resetThreadReceive();
            boolean unregistered = unregisterServerPort(port);
            if(unregistered && (!started && !running)) {
                instant_stopped = Instant.now();
                StaticStandard.log("[SERVER] Stopped server on port: " + port);
            } else if(started || running) {
                instant_stopped = null;
                StaticStandard.logErr("[SERVER] Failed stopping server on port: " + port + ", server is running anyway");
            } else if(!unregistered) {
                instant_stopped = null;
                StaticStandard.logErr("[SERVER] Failed stopping server on port: " + port + ", port already unregistered");
            } else {
                instant_stopped = null;
                StaticStandard.logErr("[SERVER] Failed stopping server on port: " + port);
            }
        } catch (Exception ex){
            instant_stopped = null;
            StaticStandard.logErr("[SERVER] Error while stopping server: " + ex, ex);
        } 
        return this;
    }
    
    /**
     * Sets the port
     * @param port Intger Port
     * @return Server This Server
     */
    public final Server setPort(int port) {
        if(!checkPort(port)) {
            port = STANDARDPORT;
        }
        this.port = port;
        return this;
    }
    
    /**
     * Returns the port
     * @return Integer Port
     */
    public final int getPort() {
        return port;
    }
    
    /**
     * Returns the used serversocket
     * @return ServerSocket ServerSocket
     */
    public final ServerSocket getServerSocket() {
        return serversocket;
    }

    /**
     * Returns the maximum waiting time for a client to respond
     * @return Integer Maximum client responding time
     */
    public final int getMaxClientReloadingTime() {
        return max_client_reloading_time;
    }

    /**
     * Sets the maximum waiting time for a client to respond
     * @param max_client_reloading_time Integer Maximum client responding time
     * @return Server This Server
     */
    public final Server setMaxClientReloadingTime(int max_client_reloading_time) {
        this.max_client_reloading_time = max_client_reloading_time;
        return this;
    }

    /**
     * Returns the maximum tries for server responds
     * @return Integer Maximum server responding tries
     */
    public final int getMaxClientReloadingTies() {
        return max_client_reloading_tries;
    }

    /**
     * Sets the maximum tries for client responds
     * @param max_client_reloading_tries Integer Maximum client responding tries
     * @return Server This Server
     */
    public final Server setMaxClientReloadingTries(int max_client_reloading_tries) {
        this.max_client_reloading_tries = max_client_reloading_tries;
        return this;
    }
    
    /**
     * Returns the used inputprocessor used to process all inputs
     * @return InputProcessor InputProcessor
     */
    public final InputProcessor getInputProcessor() {
        return inputprocessor;
    }
    
    /**
     * Sets the inputprocessor used to process all inputs
     * @param inputprocessor InputProcessor InputProcessor
     * @return Server This Server
     */
    public final Server setInputProcessor(InputProcessor inputprocessor) {
        this.inputprocessor = inputprocessor;
        return this;
    }

    /**
     * Return the maximum duration to be waited for a client
     * @return Integer Maximum duration to be waited for a client
     */
    public final Duration getMaxReloadDuration() {
        return max_reload_duration;
    }

    /**
     * Sets the maximum duration to be waited for a client
     * @param max_reload_duration Integer Maximum duration to be waited for a client
     * @return Server This Server
     */
    public final Server setMaxReloadDuration(Duration max_reload_duration) {
        this.max_reload_duration = max_reload_duration;
        return this;
    }

    /**
     * Returns the timestamp when the client was started
     * @return Instant Timestamp when the client was started
     */
    public Instant getInstantStarted() {
        return instant_started;
    }

    /**
     * Returns the timestamp when the client was stopped
     * @return Instant Timestamp when the client was stopped
     */
    public Instant getInstantStopped() {
        return instant_stopped;
    }
    
    private final Thread reloadClientsWThread() {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                reloadClients();
            }
            
        };
        Thread thread = StaticStandard.execute(run);
        thread.setName("Thread-Server-Reload");
        return thread;
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
                    public synchronized void run() {
                        try {
                            boolean isconnected = false;
                            for(int i = 0; i < max_client_reloading_tries; i++) {
                                try {
                                    client.send(MessageState.AREYOUALIVE);
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
                                } catch (Exception ex) {
                                }
                            }
                            if(!isconnected) {
                                StaticStandard.logErr(String.format("[SERVER] Client %s timed out (after %d tries with %d ms)", formatAddress(client.getInetaddress()), max_client_reloading_tries, max_client_reloading_time));
                                clients_disconnected.add(client);
                            }
                        } catch (Exception ex) {
                            StaticStandard.logErr("[SERVER] Error while reloding client " + formatAddress(client.getInetaddress()) + ": " + ex);
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
                logout(client, true, instant_now);
            }
        } catch (Exception ex) {
            StaticStandard.logErr("[SERVER] Error while reloading clients: " + ex, ex);
        }
    }
    
    private final Thread login(final Client client, final Instant instant_now) {
        if(client == null) {
            return null;
        }
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    inputprocessor.clientLoggedIn(client, instant_now);
                } catch (Exception ex) {
                    StaticStandard.logErr(String.format("[SERVER] Error while logging in client %s: %s", formatAddress(client.getInetaddress()), ex), ex);
                }
            }
            
        };
        return StaticStandard.execute(run);
    }
    
    private final Thread logout(Client client, boolean remove, Instant instant_now) {
        if(client == null) {
            return null;
        }
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    stopThread(clients_connected.get(client));
                    if(remove) {
                        clients_connected.remove(client);
                        client.stop();
                    }
                    StaticStandard.log(String.format("[SERVER] Client %s logged out", formatAddress(client.getInetaddress())));
                    inputprocessor.clientLoggedOut(client, instant_now);
                } catch (Exception ex) {
                    StaticStandard.logErr(String.format("[SERVER] Error while logging out client %s: %s", formatAddress(client.getInetaddress()), ex), ex);
                }
            }
            
        };
        return StaticStandard.execute(run);
    }
    
    private final void processInputRAW(final Client client, final Object object, final Instant instant) {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    if(object instanceof MessageState) {
                        final MessageState message = (MessageState) object;
                        Object answer = null;
                        switch(message) {
                            case LOGIN:
                                answer = MessageState.ANSWERNO;
                                break;
                            case LOGOUT:
                                answer = MessageState.ANSWERYES;
                                logout(client, true, instant);
                                break;
                            case BROADCAST:
                                answer = MessageState.ANSWERNO;
                                break;
                            case ANSWERYES:
                                break;
                            case ANSWERNO:
                                break;
                            case PING:
                                answer = MessageState.ANSWERYES;
                                break;
                            case AREYOUALIVE:
                                answer = MessageState.IMALIVE;
                                //StaticStandard.logErr("[SERVER] Client asks me alive");
                                break;
                            case IMALIVE:
                                client.last_check = instant;
                                //StaticStandard.logErr("[SERVER] Client is alive");
                                break;
                            default:
                                break;
                        }
                        try {
                            if(answer != null) {
                                client.send(answer);
                            }
                        } catch (Exception ex) {
                            StaticStandard.logErr("[SERVER] Error while sending answer: " + ex); //TODO Soll das so sein damit die Konsole schön aussieht?
                        }
                    } else {
                        inputprocessor.processInput(object, client, instant);
                    }
                } catch (Exception ex) {
                    StaticStandard.logErr("[SERVER] Error while processing on server raw inputs: " + ex, ex);
                }
            }
            
        };
        client.getExecutorInputProcessor().execute(run);
    }
    
    /**
     * Returns the client for an inetaddress
     * @param inetaddress InetAddress InetAddress
     * @return Client The client if it is connected
     */
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
    
    private final Thread processSocket(Socket socket) {
        if(socket == null) {
            return null;
        }
        try {
            final Client client = new Client(socket);
            final Instant instant_now = Instant.now();
            client.isServerClient = true;
            Thread thread = new Thread(new Runnable() {

                @Override
                public synchronized void run() {
                    try {
                        StaticStandard.log(String.format("[SERVER] Connection to %s established", formatAddress(client.getInetaddress())));
                        login(client, instant_now);
                        ObjectOutputStream oos = client.getObjectOutputStream();
                        ObjectInputStream ois = client.getObjectInputStream();
                        Object object = null;
                        while(((object = ois.readObject()) != null)) {
                            processInputRAW(client, object, Instant.now());
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        if(ex != null && !ex.toString().startsWith("java.io.EOFException")) {
                            StaticStandard.logErr("[SERVER] Error while processing socket: " + ex, ex);
                        }
                    }                        
                    try {
                        if(clients_connected.containsKey(client)) {
                            clients_connected.remove(client);
                        }
                        StaticStandard.log(String.format("[SERVER] Connection to %s closed", formatAddress(client.getInetaddress())));
                    } catch (Exception ex) {
                    }
                }

            });
            clients_connected.put(client, thread);
            thread.start();
            return thread;
        } catch (Exception ex) {
            StaticStandard.logErr("[SERVER] Error while processing socket: " + ex, ex);
            return null;
        }
    }
    
    private final Thread resetThreadReceive() {
        stopThread(thread_receive);
        running = false;
        thread_receive = new Thread(new Runnable() {

            @Override
            public synchronized void run() {
                try {
                    running = true;
                    while(started && running) {
                        try {
                            final Socket socket = serversocket.accept();
                            StaticStandard.log(String.format("[SERVER] Socket connected from: \"%s\"", socket.getInetAddress().getHostAddress()));
                            processSocket(socket);
                        } catch (Exception ex) {
                            StaticStandard.logErr("[SERVER] Error while socket connected to the server: " + ex, ex);
                        }
                    }
                    StaticStandard.log("[SERVER] Server listener stopped");
                } catch (Exception ex) {
                    StaticStandard.logErr("[SERVER] Error in the server receive thread: " + ex, ex);
                }
                running = false;
            }

        });
        thread_receive.setName("Thread-Server-Receiver");
        return thread_receive;
    }
    
    private Thread thread_receive = null;
    
    /**
     * Registers a port to be used by a server
     * @param port Integer Port
     * @return Boolean True if the port was registered, False if not
     */
    public synchronized final static boolean registerServerPort(int port) {
        if(checkPort(port) && checkPortAvailable(port)) {
            PORTS.add(port);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Unregisters a port to be no longer used by a server
     * @param port Integer Port
     * @return Boolean True if the port was unregistered, False if not
     */
    public synchronized final static boolean unregisterServerPort(int port) {
        if(checkPort(port) && !checkPortAvailable(port)) {
            PORTS.remove(PORTS.indexOf(port));
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Stops a thread completely
     * @param thread Thread Thread to be stopped
     */
    public synchronized final static void stopThread(Thread thread) {
        if(thread == null) {
            return;
        }
        int times = 0;
        while((thread.isAlive() && !thread.isInterrupted()) && (times * THREADSTOPWAITTIME) <= MAXTHREADSTOPTIME) {
            try {
                thread.interrupt();
                thread.stop();
                Thread.sleep(THREADSTOPWAITTIME);
            } catch (Exception ex) {
                StaticStandard.logErr(String.format("[%s] Error while killing thread: %s", thread.getName(), ex), ex);
            }
            times++;
        }
        //StaticStandard.logErr(String.format("%s living: %b", thread.getName(), (thread.isAlive() && !thread.isInterrupted())));
    }
    
    /**
     * Starts a thread completely
     * @param thread Thread Thread to be started
     */
    public synchronized final static void startThread(Thread thread) {
        if(thread == null) {
            return;
        }
        thread.start();
    }
    
    private synchronized final static boolean clientConnected(Client client) {
        //TODO Client connection einzel abfrage basteln
        return false;
    }
    
    /**
     * Formats an inetaddress
     * @param inetaddress InetAddress InetAddress to be formatted
     * @return String the formatted inetaddress
     */
    public final static String formatAddress(InetAddress inetaddress) {
        return String.format("\"%s\"", ((inetaddress != null) ? inetaddress.getHostAddress() : ""));
    }
    
    /**
     * Formats an inetaddress and a port
     * @param inetaddress InetAddress InetAddress to be formatted
     * @param port Integer Port to be formatted
     * @return String the formatted inetaddress and port
     */
    public final static String formatAddressAndPort(InetAddress inetaddress, int port) {
        return String.format("\"%s:%d\"", ((inetaddress != null) ? inetaddress.getHostAddress() : ""), port);
    }
    
    /**
     * Checks if a port is in the valid range
     * @param port Integer Port
     * @return Boolean True if the port is valid, False if not
     */
    public static final boolean checkPort(int port) {
        return (port >= PORTMIN && port <= PORTMAX);
    }
    
    /**
     * Checks if a port is available (not used by a server already)
     * @param port Integer Port
     * @return Boolean True if the port is free, False if not
     */
    public static final boolean checkPortAvailable(int port) {
        return !PORTS.contains(port);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            reloadClientsWThread();
        }
    }
    
}
