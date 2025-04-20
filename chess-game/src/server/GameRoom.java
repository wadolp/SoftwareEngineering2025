package server;

import ocsf.server.*;
import shared.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chess game room on the server
 */
public class GameRoom {
    private String gameId;
    private String whitePlayer;
    private String blackPlayer;
    private List<ConnectionToClient> players;
    private boolean gameInProgress;
    private boolean isWhiteTurn;
    
    /**
     * Create a new game room
     * 
     * @param gameId Unique identifier for the game
     * @param whitePlayer Username of the white player
     * @param blackPlayer Username of the black player
     */
    public GameRoom(String gameId, String whitePlayer, String blackPlayer) {
        this.gameId = gameId;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.players = new ArrayList<>();
        this.gameInProgress = false;
        this.isWhiteTurn = true; // White always starts
    }
    
    /**
     * Add a player to the game
     * 
     * @param client The client connection to add
     */
    public void addPlayer(ConnectionToClient client) {
        if (!players.contains(client)) {
            players.add(client);
            System.out.println("Added player to game " + gameId + ": " + client.getInfo("username"));
        }
    }
    
    
    /**
     * Start the game when both players are connected
     */
// In GameRoom.java
public void startGame() {
    gameInProgress = true;
    
    // Send game info to both players
    try {
        for (ConnectionToClient client : players) {
            client.sendToClient(new GameStartMessage(gameId, whitePlayer, blackPlayer));
        }
        
        System.out.println("Game " + gameId + " started: " + whitePlayer + " (White) vs " + blackPlayer + " (Black)");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    /**
     * Process a move from a player
     * 
     * @param move The move to process
     * @param sender The client that sent the move
     */
    public void processMove(GameMove move, ConnectionToClient sender) {
        if (!gameInProgress) {
            return;
        }
        
        String senderUsername = (String) sender.getInfo("username");
        
        // Verify it's player's turn
        if ((isWhiteTurn && senderUsername.equals(whitePlayer)) ||
            (!isWhiteTurn && senderUsername.equals(blackPlayer))) {
            
            // In a real implementation, you would validate the move here
            // For now, just broadcast the move to all players
            broadcastMove(move);
            
            // Switch turns
            isWhiteTurn = !isWhiteTurn;
            
            System.out.println("Move in game " + gameId + ": " + 
                              "(" + move.getFromRow() + "," + move.getFromCol() + ") to " +
                              "(" + move.getToRow() + "," + move.getToCol() + ")");
        } else {
            System.out.println("Ignored move from " + senderUsername + " - not their turn");
        }
    }
    
    /**
     * Broadcast a move to all players in the game
     * 
     * @param move The move to broadcast
     */
    private void broadcastMove(GameMove move) {
        try {
            for (ConnectionToClient client : players) {
                client.sendToClient(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    /**
     * Get the game ID
     * @return The game ID
     */
    public String getGameId() {
        return gameId;
    }
}