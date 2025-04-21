package shared.messages;

import java.io.Serializable;

/**
 * Message sent from client to server when a player resigns
 */
public class ResignGameMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String gameId;
    private String resigningPlayer;
    
    /**
     * Create a new resign game message
     * 
     * @param gameId The ID of the game
     * @param resigningPlayer Username of the player who is resigning
     */
    public ResignGameMessage(String gameId, String resigningPlayer) {
        this.gameId = gameId;
        this.resigningPlayer = resigningPlayer;
    }
    
    /**
     * Get the game ID
     * @return The game ID
     */
    public String getGameId() {
        return gameId;
    }
    
    /**
     * Get the username of the resigning player
     * @return The resigning player's username
     */
    public String getResigningPlayer() {
        return resigningPlayer;
    }
}