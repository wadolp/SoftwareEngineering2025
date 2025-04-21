package shared.messages;

import java.io.Serializable;

/**
 * Message sent from client to server to request a new game
 */
public class NewGameRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    
    /**
     * Create a new game request
     * 
     * @param username Username of the player requesting a new game
     */
    public NewGameRequest(String username) {
        this.username = username;
    }
    
    /**
     * Get the username of the player requesting a new game
     * @return The player's username
     */
    public String getUsername() {
        return username;
    }
}