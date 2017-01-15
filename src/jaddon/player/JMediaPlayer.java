///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package jaddon.player;
//
//import java.awt.BorderLayout;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import javax.media.CannotRealizeException;
//import javax.media.Manager;
//import javax.media.NoPlayerException;
//import javax.media.Player;
//import javax.swing.JPanel;
//
///**
// * This is a simple to use media player for java swing
// * @author Paul Hagedorn
// */
//public class JMediaPlayer {
//    
//    Player mp;
//    JPanel panel = new JPanel();
//    File file = null;
//    URL url = null;
//    
//    /**
//     * Basic constructor for the media player
//     */
//    private JMediaPlayer() {
//        init();
//    }
//    
//    /**
//     * This constructs the media player with a file
//     * @param file File File to view
//     * @throws MalformedURLException Error if wrong url 
//     */
//    public JMediaPlayer(File file) throws MalformedURLException {
//        this(file.toURI().toURL());
//    }
//    
//    /**
//     * This constructs the JMediaPlayer with the mediaUrl
//     * @param mediaUrl URL to the media you want to see
//     */
//    public JMediaPlayer(URL mediaUrl) {
//        try {
//            mp = Manager.createRealizedPlayer(mediaUrl);
//        } catch (IOException | NoPlayerException | CannotRealizeException ex) {
//            System.err.println("FATAL ERROR: " + ex);
//        }
//        mp.stop();
//        init();
//    }
//    
//    /**
//     * Inits the components
//     */
//    private void init() {
//        panel.setLayout(new BorderLayout());
//        panel.add(mp.getVisualComponent(), BorderLayout.CENTER);
//        panel.add(mp.getControlPanelComponent(), BorderLayout.SOUTH);
//    }
//    
//    /**
//     * Returns the JPanel from the player
//     * @return JPanel Video player
//     */
//    public JPanel getPanel() {
//        return panel;
//    }
//    
//    /**
//     * Returns the media player
//     * @return Player media Player
//     */
//    public Player getPlayer() {
//        return mp;
//    }
//    
//    /**
//     * Starts the player
//     */
//    public void start() {
//        mp.start();
//    }
//    
//    /**
//     * Stops the player
//     */
//    public void stop() {
//        mp.stop();
//    }
//    
//    /**
//     * Closes the media file
//     */
//    public void close() {
//        mp.close();
//    }
//    
//    /**
//     * Sets the played file
//     * @param newFile File new File
//     * @throws java.net.MalformedURLException java.net.MalformedURLException
//     * @throws javax.media.CannotRealizeException javax.media.CannotRealizeException
//     * @throws javax.media.NoPlayerException javax.media.NoPlayerException
//     */
//    public void setFile(File newFile) throws MalformedURLException, IOException, NoPlayerException, CannotRealizeException {
//        if(newFile.exists() && newFile.isFile()) {
//            this.file = newFile;
//            setURL(newFile.toURI().toURL());
//        } else {
//            throw new IOException("File does not exist");
//        }
//    }
//    
//    /**
//     * Sets the played media URL
//     * @param newURL URL new URL
//     * @throws java.io.IOException java.io.IOException
//     * @throws javax.media.NoPlayerException javax.media.NoPlayerException
//     * @throws javax.media.CannotRealizeException javax.media.CannotRealizeException
//     */
//    public void setURL(URL newURL) throws IOException, NoPlayerException, CannotRealizeException {
//        stop();
//        close();
//        mp = Manager.createRealizedPlayer(newURL);
//        this.url = newURL;
//    }
//
//    /**
//     * Returns the actual played file if no custom URL was set
//     * @return File actual played file if no custom URL was set
//     */
//    public File getFile() {
//        try {
//            if(file.toURI().toURL() == url) {
//                return file;
//            } else {
//                return null;
//            }
//        } catch (MalformedURLException ex) {
//            System.err.println(ex);
//            return null;
//        }
//    }
//
//    /**
//     * Returns the actual played URL
//     * @return URL actual played URL
//     */
//    public URL getUrl() {
//        return url;
//    }
//}
