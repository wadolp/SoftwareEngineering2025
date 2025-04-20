package client;

import shared.messages.LoginMessage;
import shared.messages.RegistrationMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Panel for user login interface
 */
public class LoginPanel extends JPanel {
    private ChessClient client;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    /**
     * Create a new login panel
     * 
     * @param client The chess client
     */
    public LoginPanel(ChessClient client) {
        this.client = client;
        
        setLayout(new GridLayout(3, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> attemptLogin());
        add(loginBtn);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> attemptRegistration());
        add(registerBtn);
        
        // Add enter key listener for convenience
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
    
    /**
     * Attempt to log in with the entered credentials
     */
    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "Please enter both username and password", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        try {
            // Store the username in the client
            client.setUsername(username);
            
            // Make sure the registering flag is false for login attempts
            client.setRegistering(false);
            
            // Send login message to server
            client.sendToServer(new LoginMessage(username, password));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Error communicating with server", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Attempt to register a new user
     */
    private void attemptRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "Please enter both username and password", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        try {
            // Store the username in the client
            client.setUsername(username);
            
            // Set the registering flag to true
            client.setRegistering(true);
            
            // Send registration message to server
            client.sendToServer(new RegistrationMessage(username, password));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Error communicating with server", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Called when login is successful
     */
    public void loginSuccess() {
        // Nothing needed here - the client will handle showing the chess board
    }
    
    /**
     * Called when login fails
     */
    public void loginFailed() {
        JOptionPane.showMessageDialog(
            this, 
            "Invalid username or password", 
            "Login Failed", 
            JOptionPane.ERROR_MESSAGE
        );
        passwordField.setText("");
    }
}