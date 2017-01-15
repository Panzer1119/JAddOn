/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlogin;

import jaddon.controller.StaticStandard;
import static jaddon.jlang.JLang.getLangProp;
import static jaddon.utils.JUtils.setActionListener;
import static jaddon.utils.JUtils.setKeyListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This is a login formular based on the java swing paket
 * @author Paul Hagedorn
 */
public class JLogin {
    public static int LOGIN_OPTION = 0;
    public static int ERROR_OPTION = -1;
    public static int CANCEL_OPTION = 1;
    
    private JDialog dialog = new JDialog();
    private Properties lang = getLangProp();
    private JPanel pw_panel = new JPanel();
    private JPanel user_panel = new JPanel();
    private JPanel input_panel = new JPanel();
    private JPanel button_panel = new JPanel();
    private JTextField user = new JTextField();
    private JPasswordField pw = new JPasswordField();
    private JLabel pw_label = new JLabel("ERROR");
    private JLabel user_label = new JLabel("ERROR");
    private JButton login = new JButton("ERROR");
    private JButton cancel = new JButton("ERROR");
    private Component c = null;
    private boolean visible = false;
    private String pw_title = "ERROR";
    private String user_title = "ERROR";
    private String pw_eingabe = "";
    private String user_eingabe = "";
    private String login_title = "ERROR";
    private String cancel_title = "ERROR";
    private String title = "ERROR";
    private int result = -1;
    
    public JLogin(Component c) {
        StaticStandard.setLogin(this);
        dialog.setModal(true);
        dialog.setPreferredSize(new Dimension(400, 150));
        setStandardNames();
        setStandardActionListeners();
        //input_panel.setLayout(new BoxLayout(input_panel, BoxLayout.Y_AXIS));
        input_panel.setLayout(new GridLayout(2, 2));
        pw_panel.setLayout(new BoxLayout(pw_panel, BoxLayout.X_AXIS));
        user_panel.setLayout(new BoxLayout(user_panel, BoxLayout.X_AXIS));
        
        /*
        pw_panel.add(pw_label);
        pw_panel.add(pw);
        user_panel.add(user_label);
        user_panel.add(user);
        */
        
        input_panel.add(user_label);
        input_panel.add(user);
        input_panel.add(pw_label);
        input_panel.add(pw);
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        
        button_panel.add(login);
        button_panel.add(cancel);
        
        //input_panel.add(user_panel);
        //input_panel.add(pw_panel);
        dialog.add(input_panel, BorderLayout.CENTER);
        dialog.add(button_panel, BorderLayout.SOUTH);
        dialog.pack();
        setRelativeTo(c);
    }
    
    public int showLoginDialog() {
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setUsername(user.getText());
                setPassword(pw.getText());
            }
        });
        dialog.show();
        dialog.getContentPane().removeAll();
        dialog.dispose();
        dialog = null;
        return result;
    }
    
    public void setStandardActionListeners() {
        setLoginActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result = JLogin.LOGIN_OPTION;
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });
        setCancelActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               result = JLogin.CANCEL_OPTION;
               dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
           } 
        });
        setPWKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == (char) 10) {
                    login.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        setUserKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == (char) 10) {
                    pw.grabFocus();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
    
    public void setStandardNames() {
        setPasswordTitle(lang.getProperty("password", "Password") + ":");
        setUsernameTitle(lang.getProperty("username", "Username") + ":");
        setTitle(lang.getProperty("login_formula", "Login"));
        setLoginButtonText(lang.getProperty("login", "Login"));
        setCancelButtonText(lang.getProperty("cancel", "Cancel"));
    }
    
    private void setLoginActionListener(ActionListener al) {
        setActionListener(login, al);
    }
    
    private void setCancelActionListener(ActionListener al) {
        setActionListener(cancel, al);
    }
    
    private void setPWKeyListener(KeyListener kl) {
        setKeyListener(pw, kl);
    }
    
    private void setUserKeyListener(KeyListener kl) {
        setKeyListener(user, kl);
    }

    public Component getRelativeTo() {
        return c;
    }

    public void setRelativeTo(Component c) {
        this.c = c;
        dialog.setLocationRelativeTo(this.c);
    }

    public boolean isVisible() {
        return visible;
    }

    public String getPasswordTitle() {
        return pw_title;
    }

    public void setPasswordTitle(String pw_title) {
        this.pw_title = pw_title;
        pw_label.setText(this.pw_title);
    }

    public String getUsernameTitle() {
        return user_title;
    }

    public void setUsernameTitle(String user_title) {
        this.user_title = user_title;
        user_label.setText(this.user_title);
    }

    public String getPassword() {
        setPassword(pw.getText());
        return pw_eingabe;
    }

    public void setPassword(String pw_eingabe) {
        this.pw_eingabe = pw_eingabe;
        pw_label.setText(this.pw_title);
    }

    public String getUsername() {
        setUsername(user.getText());
        return user_eingabe;
    }

    public void setUsername(String user_eingabe) {
        this.user_eingabe = user_eingabe;
        user.setText(this.user_eingabe);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        dialog.setTitle(this.title);
    }

    public String getLoginButtonText() {
        return login_title;
    }

    public void setLoginButtonText(String login_title) {
        this.login_title = login_title;
        login.setText(this.login_title);
    }

    public String getCancelButtonText() {
        return cancel_title;
    }

    public void setCancelButtonText(String cancel_title) {
        this.cancel_title = cancel_title;
        cancel.setText(this.cancel_title);
    }
}
