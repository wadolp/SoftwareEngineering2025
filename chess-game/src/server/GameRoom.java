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
    public void startGame() {
        gameInProgress = true;  // Set this flag to true
        
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
     * Process a game state message from a player
     * 
     * @param stateMsg The game state message to process
     * @param sender The client that sent the game state
     */
    public void processGameState(GameStateMessage stateMsg, ConnectionToClient sender) {
        // Ensure the game is marked as in progress
        this.gameInProgress = true;
        
        if (!gameInProgress) {
            System.out.println("State update ignored - game not in progress");
            return;
        }
        
        String senderUsername = (String) sender.getInfo("username");
        System.out.println("Processing game state from " + senderUsername + " in game " + gameId);
        
        // Verify it's player's turn before updating
        if ((stateMsg.isWhiteTurn() && senderUsername.equals(blackPlayer)) ||
            (!stateMsg.isWhiteTurn() && senderUsername.equals(whitePlayer))) {
            
            System.out.println("Valid state update - broadcasting to " + players.size() + " players");
            
            // Update server's record of whose turn it is
            isWhiteTurn = stateMsg.isWhiteTurn();
            
            // Broadcast game state to ALL players
            for (ConnectionToClient player : players) {
                try {
                    String playerName = (String) player.getInfo("username");
                    System.out.println("  Sending game state to player: " + playerName);
                    player.sendToClient(stateMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("Game state processed in game " + gameId + 
                              ". Next turn: " + (isWhiteTurn ? "White" : "Black"));
        } else {
            System.out.println("Ignored game state from " + senderUsername + " - not their turn or incorrect turn state");
        }
    }
    
    /**
     * Process a move from a player (DEPRECATED - kept for backward compatibility)
     * 
     * @param move The move to process
     * @param sender The client that sent the move
     */
    public void processMove(GameMove move, ConnectionToClient sender) {
        System.out.println("WARNING: Using deprecated processMove method instead of processGameState");
        
        // Ensure the game is marked as in progress
        this.gameInProgress = true;
        
        if (!gameInProgress) {
            System.out.println("Move ignored - game not in progress");
            return;
        }
        
        String senderUsername = (String) sender.getInfo("username");
        System.out.println("Processing move from " + senderUsername + " in game " + gameId);
        System.out.println("Move's gameId: " + move.getGameId() + ", expected: " + this.gameId);
        
        // Verify it's player's turn
        if ((isWhiteTurn && senderUsername.equals(whitePlayer)) ||
            (!isWhiteTurn && senderUsername.equals(blackPlayer))) {
            
            System.out.println("Valid move - broadcasting to " + players.size() + " players");
            
            // Broadcast move to ALL players
            for (ConnectionToClient player : players) {
                try {
                    String playerName = (String) player.getInfo("username");
                    System.out.println("  Sending move to player: " + playerName);
                    player.sendToClient(move);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            // Switch turns
            isWhiteTurn = !isWhiteTurn;
            
            System.out.println("Move processed in game " + gameId + ": " + 
                              "(" + move.getFromRow() + "," + move.getFromCol() + ") to " +
                              "(" + move.getToRow() + "," + move.getToCol() + ")");
        } else {
            System.out.println("Ignored move from " + senderUsername + " - not their turn");
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