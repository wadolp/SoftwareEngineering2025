package shared.messages;

import java.io.Serializable;

/**
 * Message sent from client to server for user registration
 */
public class RegistrationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    
    /**
     * Create a new registration message
     * 
     * @param username The username to register
     * @param password The password for the new account
     */
    public RegistrationMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Get the username
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the password
     * @return The password
     */
    public String getPassword() {
        return password;
    }
}