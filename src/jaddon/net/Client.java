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
    
    private final Client client = this;
    private Socket socket = null;
    private InetAddress inetaddress = null;
    private int port = -1;
    private boolean connected = false;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private final ExecutorService executor_input_processor = Executors.newFixedThreadPool(1);
    private InputProcessor inputprocessor = null;
    public Instant last_check = Instant.now();
    public boolean isServerClient = false;
    private final Timer timer = new Timer(1000, this);
    private int max_server_reloading_time = 1000;
    private int max_server_reloading_tries = 3;
    private Duration max_reload_duration = Duration.ofMillis(1000);
    private Instant last_server_check = Instant.now();
    private boolean ischecking_server = false;
    private boolean disconnected = false;
    private boolean reconnect_after_connection_loss = false;
    private int reconnection_tries = 3;
    
    public Client(Socket socket) {
        init();
        setSocket(socket);
    }
    
    public Client(InetAddress inetaddress, int port) {
        init();
        setPort(port);
        setInetaddress(inetaddress);
    }
    
    private final void init() {
        resetThreadReceive();
    }
    
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
    
    public final Client start() {
        startIntern(0);
        return this;
    }
    
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
            return times;
        }
        try {
            StaticStandard.log(String.format("[CLIENT] Started connecting to %s (Try: %d)", formatAddressAndPort(), times));
            socket = new Socket(inetaddress, port);
            setSocket(socket);
            connected = true;
            if(!isServerClient) {
                send(MessageState.LOGIN);
            }
            disconnected = false;
            StaticStandard.log("[CLIENT] Connected successfully to " + formatAddressAndPort());
        } catch (Exception ex) {
            StaticStandard.logErr("[CLIENT] Error while connecting to server: " + ex);
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
            connected = false;
            StaticStandard.log("[CLIENT] Disconnected successfully from " + formatAddressAndPort());
            disconnected = true;
        } catch (Exception ex) {
            StaticStandard.logErr("[CLIENT] Error while disconnecting from server: " + ex, ex);
        }
        return this;
    }
    
    public final int getMaxServerReloadingTime() {
        return max_server_reloading_time;
    }

    public final Client setMaxServerReloadingTime(int max_server_reloading_time) {
        this.max_server_reloading_time = max_server_reloading_time;
        return this;
    }

    public final int getMaxServerReloadingTies() {
        return max_server_reloading_tries;
    }

    public final Client setMaxServerReloadingTries(int max_server_reloading_tries) {
        this.max_server_reloading_tries = max_server_reloading_tries;
        return this;
    }

    public final Duration getMaxReloadDuration() {
        return max_reload_duration;
    }

    public final Client setMaxReloadDuration(Duration max_reload_duration) {
        this.max_reload_duration = max_reload_duration;
        return this;
    }

    public boolean isReconnectingAfterConnectionLoss() {
        return reconnect_after_connection_loss;
    }

    public void setReconnectAfterConnectionLoss(boolean reconnect_after_connection_loss) {
        this.reconnect_after_connection_loss = reconnect_after_connection_loss;
    }

    public int getReconnectionTries() {
        return reconnection_tries;
    }

    public void setReconnectionTries(int reconnection_tries) {
        this.reconnection_tries = reconnection_tries;
    }
    
    public final String formatAddress() {
        return Server.formatAddress(inetaddress);
    }
    
    public final String formatAddressAndPort() {
        return Server.formatAddressAndPort(inetaddress, port);
    }
    
    private final void isConnected(int times) {
        if(ischecking_server) {
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
    
    public final Socket getSocket() {
        return socket;
    }

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
    
    public final Client setSocket(Socket socket) {
        if(socket != null) {
            this.socket = socket;
            setStreams(socket);
            setPort(socket.getPort());
            setInetaddress(socket.getInetAddress());
        }
        return this;
    }
    
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
    
    public final boolean send(Object object) {
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

    public final Client setInetaddress(InetAddress inetaddress) {
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

    public final int getPort() {
        return port;
    }

    public final Client setPort(int port) {
        if(!Server.checkPort(port)) {
            port = Server.STANDARDPORT;
        }
        this.port = port;
        return this;
    }
    
    public final Client setInputProcessor(InputProcessor inputprocessor) {
        this.inputprocessor = inputprocessor;
        return this;
    }
    
    public final InputProcessor getInputProcessor() {
        return inputprocessor;
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
                            StaticStandard.logErr("[CLIENT] Error while receiving: " + ex);
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
