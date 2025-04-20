package client;

import java.io.Serializable;

/**
 * Class representing a chess game
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String gameId;
    private String whitePlayer;
    private String blackPlayer;
    
    /**
     * Create a new game
     * 
     * @param gameId Unique identifier for the game
     * @param whitePlayer Username of the white player
     * @param blackPlayer Username of the black player
     */
    public Game(String gameId, String whitePlayer, String blackPlayer) {
        this.gameId = gameId;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }
    
    /**
     * Get the game ID
     * @return The game ID
     */
    public String getGameId() {
        return gameId;
    }
    
    /**
     * Get the white player's username
     * @return The white player's username
     */
    public String getWhitePlayer() {
        return whitePlayer;
    }
    
    /**
     * Get the black player's username
     * @return The black player's username
     */
    public String getBlackPlayer() {
        return blackPlayer;
    }
}