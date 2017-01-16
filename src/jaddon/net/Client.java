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
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public class Client implements ActionListener, Serializable {
    
    /**
     * Client Instance of this object
     */
    public final Client client = this;
    private Socket socket = null;
    private InetAddress inetaddress = null;
    private int port = -1;
    private boolean connected = false;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private final ExecutorService executor_input_processor = Executors.newFixedThreadPool(1);
    private InputProcessor inputprocessor = null;
    /**
     * Instant last check from the server
     */
    public Instant last_check = Instant.now();
    /**
     * Boolean True if this is a client from a server, False if it is a "standalone" client
     */
    public boolean isServerClient = false;
    private final Timer timer = new Timer(1000, this);
    private int max_server_reloading_time = 1000;
    private int max_server_reloading_tries = 3;
    private Instant last_server_check = Instant.now();
    private boolean ischecking_server = false;
    private boolean disconnected = false;
    private boolean reconnect_after_connection_loss = false;
    private int reconnection_tries = 3;
    private Instant instant_started = null;
    private Instant instant_stopped = null;
    
    /**
     * Constructor for the Client
     * @param socket Socket Socket
     */
    public Client(Socket socket) {
        init();
        setSocket(socket);
    }
    
    /**
     * Constructor for the Client
     * @param inetaddress InetAddress Internet address
     * @param port Integer Port
     */
    public Client(InetAddress inetaddress, int port) {
        init();
        setPort(port);
        setInetAddress(inetaddress);
    }
    
    private final void init() {
        resetThreadReceive();
    }
    
    /**
     * Starts the connection within an extra thread
     * @return Thread The thread that is starting the Client
     */
    public final Thread startWThread() {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                start();
            }
            
        };
        Thread thread = StaticStandard.execute(run);
        thread.setName("Thread-Client-Start");
        return thread;
    }
    
    /**
     * Starts the connection
     * @return Client This Client
     */
    public final Client start() {
        startIntern(0);
        return this;
    }
    
    /**
     * Stops the connection
     * @return Client This Client
     */
    public final Client stop() {
        timer.stop();
        resetThreadReceive();
        disconnect();
        return this;
    }
    
    private final int startIntern(int times) {
        timer.start();
        times = connect(times);
        resetThreadReceive();
        Server.startThread(thread_receive);
        return times;
    }
    
    private final int connect(int times) {
        times++;
        if(times > reconnection_tries) {
            instant_started = null;
            return times;
        }
        try {
            StaticStandard.log(String.format("[CLIENT] Started connecting to %s (Try: %d)", formatAddressAndPort(), times));
            socket = new Socket(inetaddress, port);
            setSocket(socket);
            instant_started = Instant.now();
            connected = true;
            if(!isServerClient) {
                send(MessageState.LOGIN);
            }
            disconnected = false;
            StaticStandard.log("[CLIENT] Connected successfully to " + formatAddressAndPort());
        } catch (Exception ex) {
            StaticStandard.logErr("[CLIENT] Error while connecting to server: " + ex);
            instant_started = null;
        }
        isConnected(times);
        return times;
    }
    
    private final Client disconnect() {
        if(disconnected) {
            return this;
        }
        try {
            try {
                if(!isServerClient) {
                    send(MessageState.LOGOUT);
                }
            } catch (Exception ex) {
                //StaticStandard.logErr("[CLIENT] Error while sending logout message: " + ex); //TODO Soll das so sein damit die Konsole schön aussieht?
            }
            socket.close();
            instant_stopped = Instant.now();
            connected = false;
            StaticStandard.log("[CLIENT] Disconnected successfully from " + formatAddressAndPort());
            disconnected = true;
        } catch (Exception ex) {
            instant_stopped = null;
            StaticStandard.logErr("[CLIENT] Error while disconnecting from server: " + ex, ex);
        }
        return this;
    }
    
    /**
     * Returns the maximum waiting time for a server to respond
     * @return Integer Maximum server responding time
     */
    public final int getMaxServerReloadingTime() {
        return max_server_reloading_time;
    }

    /**
     * Sets the maximum waiting time for a server to respond
     * @param max_server_reloading_time Integer Maximum server responding time
     * @return Client This Client
     */
    public final Client setMaxServerReloadingTime(int max_server_reloading_time) {
        this.max_server_reloading_time = max_server_reloading_time;
        return this;
    }

    /**
     * Returns the maximum tries for server responds
     * @return Integer Maximum server responding tries
     */
    public final int getMaxServerReloadingTies() {
        return max_server_reloading_tries;
    }

    /**
     * Sets the maximum tries for server responds
     * @param max_server_reloading_tries Integer Maximum server responding tries
     * @return Client This Client
     */
    public final Client setMaxServerReloadingTries(int max_server_reloading_tries) {
        this.max_server_reloading_tries = max_server_reloading_tries;
        return this;
    }

    /**
     * Returns if the Client tries to reconnect to the server if it loses the connection
     * @return Boolean True if the client tries to reconnect to the server if it loses the connection, False if not
     */
    public final boolean isReconnectingAfterConnectionLoss() {
        return reconnect_after_connection_loss;
    }

    /**
     * Sets if the program should try to reconnect to the server if it loses the connection
     * @param reconnect_after_connection_loss Boolean True if the client should try to reconnect to the server if it loses the connection, False if not
     * @return Client This Client
     */
    public final Client setReconnectAfterConnectionLoss(boolean reconnect_after_connection_loss) {
        this.reconnect_after_connection_loss = reconnect_after_connection_loss;
        return this;
    }

    /**
     * Returns the maximum reconnection tries
     * @return Integer Maximum reconnection tries
     */
    public final int getReconnectionTries() {
        return reconnection_tries;
    }

    /**
     * Sets the maximum reconnection tries
     * @param reconnection_tries Integer Maximum reconnection tries
     * @return Client This Client
     */
    public final Client setReconnectionTries(int reconnection_tries) {
        this.reconnection_tries = reconnection_tries;
        return this;
    }
    
    /**
     * Returns the current bound address formatted
     * @return String Current bound address formatted
     */
    public final String formatAddress() {
        return Server.formatAddress(inetaddress);
    }
    
    /**
     * Returns the current bound address and port formatted
     * @return String Current bound address and port formatted
     */
    public final String formatAddressAndPort() {
        return Server.formatAddressAndPort(inetaddress, port);
    }
    
    private final void isConnected(int times) {
        if(ischecking_server || times > reconnection_tries) {
            return;
        }
        Runnable run_ = new Runnable() {
          
            @Override
            public void run() {
                ischecking_server = true;
                try {
                    final Instant instant_now = Instant.now();
                    final Instant instant_old = last_server_check;
                    boolean isconnected = true;
                    //StaticStandard.logErr("[CLIENT] Checking connection to the server");
                    for(int i = 0; i < max_server_reloading_tries; i++) {
                        send(MessageState.AREYOUALIVE);
                        while(instant_old.equals(last_server_check)) {
                            Duration duration = Duration.between(instant_now, Instant.now());
                            if(duration.toMillis() > max_server_reloading_time) {
                                isconnected = false;
                                break;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (Exception ex) {
                            }
                        }
                        if(isconnected) {
                            break;
                        } else {
                            try {
                                Thread.sleep(500);
                            } catch (Exception ex) {
                            }
                        }
                    }
                    connected = isconnected;
                     //StaticStandard.logErr("[CLIENT] CONNECTION: " + connected);
                } catch (Exception ex) {
                    StaticStandard.logErr("[CLIENT] Error while checking connection: " + ex, ex);
                }
                ischecking_server = false;
                if(!connected) {
                    StaticStandard.logErr(String.format("[CLIENT] Server %s timed out (after %d tries with %d ms)", Server.formatAddressAndPort(inetaddress, port), max_server_reloading_tries, max_server_reloading_time));
                    stop();
                    if(reconnect_after_connection_loss) {
                        startIntern(times);
                    }
                } else {
                    //StaticStandard.logErr("[CLIENT] Server is connected");
                }
            }
            
        };
        Thread thread_ = StaticStandard.execute(run_);
        thread_.setName("Thread-Client-Reload");
    }
    
    /**
     * Returns the socket
     * @return Socket Socket
     */
    public final Socket getSocket() {
        return socket;
    }

    /**
     * Returns the inetaddress
     * @return InetAddress Inetaddress
     */
    public final InetAddress getInetaddress() {
        return inetaddress;
    }
    
    private final void processInputRAW(final Object object, final Instant instant) {
        Runnable run = new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    if(object instanceof MessageState) {
                        final MessageState message = (MessageState) object;
                        Object answer = null;
                        switch(message) {
                            case LOGIN:
                                break;
                            case LOGOUT:
                                break;
                            case BROADCAST:
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
                                //StaticStandard.logErr("[CLIENT] Server asks me alive");
                                break;
                            case IMALIVE:
                                last_server_check = instant;
                                //StaticStandard.logErr("[CLIENT] Server is alive");
                                break;
                            default:
                                break;
                        }
                        try {
                            if(answer != null) {
                                send(answer);
                            }
                        } catch (Exception ex) {
                            StaticStandard.logErr("[CLIENT] Error while sending answer: " + ex); //TODO Soll das so sein damit die Konsole schön aussieht?
                        }
                    } else {
                        inputprocessor.processInput(object, instant);
                    }
                } catch (Exception ex) {
                    StaticStandard.logErr("[CLIENT] Error while processing on client raw inputs: " + ex, ex);
                }
            }
            
        };
        executor_input_processor.execute(run);
    }
    
    /**
     * Returns the thread pool that manages all inputs
     * @return ExecutorService ThreadPool that manages all inputs
     */
    public final ExecutorService getExecutorInputProcessor() {
        return executor_input_processor;
    }
    
    private final Client setStreams(Socket socket) {
        closeStreams();
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            StaticStandard.logErr("[CLIENT] Error while setting object output stream: " + ex);
        }
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            StaticStandard.logErr("[CLIENT] Error while setting object input stream: " + ex);
        }
        return this;
    }
    
    private final Client closeStreams() {
        try {
            if(oos != null) {
                oos.close();
            }
        } catch (Exception ex) {
            //StaticStandard.logErr("[CLIENT] Error while closing old object output stream: " + ex);
        }
        try {
            if(ois != null) {
                ois.close();
            }
        } catch (Exception ex) {
            //StaticStandard.logErr("[CLIENT] Error while closing old object input stream: " + ex);
        }
        return this;
    }
    
    /**
     * Sets the socket
     * @param socket Socket Socket
     * @return Client This Client
     */
    public final Client setSocket(Socket socket) {
        if(socket != null) {
            this.socket = socket;
            setStreams(socket);
            setPort(socket.getPort());
            setInetAddress(socket.getInetAddress());
        }
        return this;
    }
    
    /**
     * Returns the ObjectOutputStream of the socket
     * @return ObjectOutputStream ObjectOutputStream
     */
    public final ObjectOutputStream getObjectOutputStream() {
        int i = 0;
        while(oos == null) {
            try {
                Thread.sleep(Server.THREADSTOPWAITTIME);
            } catch (Exception ex) {
            }
            i++;
            if((i * Server.THREADSTOPWAITTIME) > Server.MAXTHREADSTOPTIME) {
                break;
            }
        }
        return oos;
    }

    /**
     * Returns the ObjectInputStream of the socket
     * @return ObjectInputStream ObjectInputStream
     */
    public final ObjectInputStream getObjectInputStream() {
        int i = 0;
        while(oos == null) {
            try {
                Thread.sleep(Server.THREADSTOPWAITTIME);
            } catch (Exception ex) {
            }
            i++;
            if((i * Server.THREADSTOPWAITTIME) > Server.MAXTHREADSTOPTIME) {
                break;
            }
        }
        return ois;
    }
    
    /**
     * Sends an object to the connected server
     * @param object Object Object
     * @return Boolean True if it worked, False if not
     */
    public final boolean send(Object object) {
        if(!connected && !isServerClient) {
            StaticStandard.logErr("[CLIENT] Cannot send object, server is not connected");
            return false;
        }
        try {
            ObjectOutputStream oos = getObjectOutputStream();
            oos.writeObject(object);
            oos.flush();
            return true;
        } catch (Exception ex) {
            //StaticStandard.logErr("[CLIENT] Error while sending: " + ex, ex); //TODO sieht heslig aus?
            return false;
        }
    }

    /**
     * Sets the inetaddress
     * @param inetaddress InetAddress InetAddress
     * @return Client This Client
     */
    public final Client setInetAddress(InetAddress inetaddress) {
        if(inetaddress != null) {
            this.inetaddress = inetaddress;
        } else {
            try {
                this.inetaddress = InetAddress.getLocalHost();
            } catch (Exception ex) {
                this.inetaddress = null;
                StaticStandard.logErr("[CLIENT] Error while setting localhost as inetaddress: " + ex, ex);
            }
        }
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
     * Sets the port
     * @param port Integer Port
     * @return Client This Client
     */
    public final Client setPort(int port) {
        if(!Server.checkPort(port)) {
            port = Server.STANDARDPORT;
        }
        this.port = port;
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
     * @return Client This Client
     */
    public final Client setInputProcessor(InputProcessor inputprocessor) {
        this.inputprocessor = inputprocessor;
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
    
    private final Thread resetThreadReceive() {
        Server.stopThread(thread_receive);
        thread_receive = new Thread(new Runnable() {

            @Override
            public synchronized void run() {
                try {
                    while(connected) {
                        try {
                            final Object object = ois.readObject();
                            processInputRAW(object, Instant.now());
                        } catch (IOException | ClassNotFoundException ex) {
                            if(ex != null && !ex.toString().startsWith("java.net.SocketException")) {
                                StaticStandard.logErr("[CLIENT] Error while receiving: " + ex);
                            } else {
                                break;
                            }
                        }
                    }
                    StaticStandard.log(String.format("[CLIENT] Connection to server %s closed", Server.formatAddressAndPort(inetaddress, port)));
                } catch (Exception ex) {
                    StaticStandard.logErr("[CLIENT] Error in the client receive thread: " + ex, ex);
                }
            }

        });
        thread_receive.setName("Thread-Client-Receiver");
        return thread_receive;
    }
    
    private Thread thread_receive = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            isConnected(0);
        }
    }
    
}
