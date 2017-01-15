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
import java.net.Socket;
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
    
    public Client(Socket socket) {
        setSocket(socket);
    }
    
    public Client(InetAddress inetaddress, int port) {
        setPort(port);
        setInetaddress(inetaddress);
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
    
    public final Client start() {
        connect();
        Server.startThread(thread_receive);
        timer.start();
        return this;
    }
    
    public final Client stop() {
        timer.stop();
        Server.stopThread(thread_receive);
        disconnect();
        return this;
    }
    
    private final Client connect() {
        try {
            StaticStandard.log("Started connecting to " + formatAddressAndPort());
            socket = new Socket(inetaddress, port);
            setSocket(socket);
            if(!isServerClient) {
                getObjectOutputStream().writeObject(new ComMessage(MessageState.LOGIN));
            }
            StaticStandard.log("Connected successfully to " + formatAddressAndPort());
        } catch (Exception ex) {
            StaticStandard.logErr("Error while connecting to server: " + ex, ex);
        }
        return this;
    }
    
    public final String formatAddress() {
        return Server.formatAddress(inetaddress);
    }
    
    public final String formatAddressAndPort() {
        return Server.formatAddressAndPort(inetaddress, port);
    }
    
    private final Client disconnect() {
        try {
            if(!isServerClient) {
                getObjectOutputStream().writeObject(new ComMessage(MessageState.LOGOUT));
            }
            socket.close();
        } catch (Exception ex) {
            StaticStandard.logErr("Error while disconnecting from server: " + ex, ex);
        }
        return this;
    }
    
    public final boolean isConnected() {
        if(socket != null) {
            connected = socket.isConnected();
        } else {
            connected = false;
        }
        return connected;
    }
    
    public final Socket getSocket() {
        return socket;
    }

    public final InetAddress getInetaddress() {
        return inetaddress;
    }
    
    private final void processInputRAW(final Object object) {
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
                                answer = new ComMessage(MessageState.ANSWERYES);
                                break;
                            case AREYOUALIVE:
                                answer = new ComMessage(MessageState.IMALIVE);
                                break;
                            case IMALIVE:
                                break;
                            default:
                                break;
                        }
                        if(answer != null) {
                            oos.writeObject(answer);
                        }
                    } else {
                        inputprocessor.processInput(object);
                    }
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while processing on client raw inputs: " + ex, ex);
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
            StaticStandard.logErr("Error while setting object output stream: " + ex, ex);
        }
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            StaticStandard.logErr("Error while setting object input stream: " + ex, ex);
        }
        return this;
    }
    
    private final Client closeStreams() {
        try {
            if(oos != null) {
                oos.close();
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while closing old object output stream: " + ex, ex);
        }
        try {
            if(ois != null) {
                ois.close();
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while closing old object input stream: " + ex, ex);
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

    public final Client setInetaddress(InetAddress inetaddress) {
        if(inetaddress != null) {
            this.inetaddress = inetaddress;
        } else {
            try {
                this.inetaddress = InetAddress.getLocalHost();
            } catch (Exception ex) {
                this.inetaddress = null;
                StaticStandard.logErr("Error while setting localhost as inetaddress: " + ex, ex);
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
    
    private final Thread thread_receive = new Thread(new Runnable() {
        
        @Override
        public synchronized void run() {
            try {
                while(connected) {
                    try {
                        final Object object = ois.readObject();
                        processInputRAW(object);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while receiving: " + ex, ex);
                    }
                }
            } catch (Exception ex) {
                StaticStandard.logErr("Error in the client receive thread: " + ex, ex);
            }
        }
        
    });

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            isConnected();
        }
    }
    
}
