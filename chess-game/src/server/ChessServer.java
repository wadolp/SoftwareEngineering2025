package server;

import ocsf.server.*;
import shared.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Server implementation for the networked chess game
 */
public class ChessServer extends AbstractServer {
    private Database db;
    private HashMap<String, GameRoom> activeGames;
    private int nextGameId = 1;
    private List<ConnectionToClient> waitingPlayers = new ArrayList<>();
    
    /**
     * Create a new chess server
     * 
     * @param port Port to listen on
     * @param db Database connection for user authentication
     */
    public ChessServer(int port, Database db) {
        super(port);
        this.db = db;
        this.activeGames = new HashMap<>();
    }
    
    /**
     * Handle messages received from clients
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof LoginMessage) {
            handleLoginMessage((LoginMessage) msg, client);
        } 
        else if (msg instanceof RegistrationMessage) {
            handleRegistrationMessage((RegistrationMessage) msg, client);
        }
        else if (msg instanceof GameStateMessage) {
            // Handle complete game state updates
            handleGameState((GameStateMessage) msg, client);
        }
        else if (msg instanceof GameMove) {
            // DEPRECATED: For backward compatibility only
            handleGameMove((GameMove) msg, client);
        }
        else if (msg instanceof InfoMessage) {
            // Handle info messages like new game requests
            handleInfoMessage((InfoMessage) msg, client);
        }
    }
    
    /**
     * Handle login authentication
     */
    private void handleLoginMessage(LoginMessage login, ConnectionToClient client) {
        boolean authenticated = db.authenticateUser(login.getUsername(), login.getPassword());
        
        try {
            client.sendToClient(new LoginResponse(authenticated));
            
            if (authenticated) {
                client.setInfo("username", login.getUsername());
                System.out.println("User " + login.getUsername() + " logged in.");
                
                // For now, automatically match players for testing
                matchPlayer(client);
            } else {
                System.out.println("Login failed for username: " + login.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle user registration
     */
    private void handleRegistrationMessage(RegistrationMessage reg, ConnectionToClient client) {
        System.out.println("Received registration request for username: " + reg.getUsername());
        
        boolean registered = db.registerUser(reg.getUsername(), reg.getPassword());
        
        try {
            client.sendToClient(new LoginResponse(registered));
            
            if (registered) {
                client.setInfo("username", reg.getUsername());
                System.out.println("User " + reg.getUsername() + " registered and logged in.");
                
                // For now, automatically match players for testing
                matchPlayer(client);
            } else {
                System.out.println("Registration failed for username: " + reg.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle info messages from clients
     */
    private void handleInfoMessage(InfoMessage msg, ConnectionToClient client) {
        String message = msg.getMessage();
        String username = (String) client.getInfo("username");
        
        System.out.println("Received info message: " + message + " from " + username);
        
        if (message.equals("NEW_GAME_REQUEST")) {
            // Handle request for a new game
            handleNewGameRequest(client);
        }
    }
    
    /**
     * Handle a request for a new game
     */
    private void handleNewGameRequest(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        
        // Remove player from any active games
        removePlayerFromGames(client);
        
        // Add player to waiting list
        if (!waitingPlayers.contains(client)) {
            waitingPlayers.add(client);
            System.out.println("Added " + username + " to waiting list for a new game");
            
            try {
                // Notify the client they're waiting
                client.sendToClient(new InfoMessage("WAITING_FOR_OPPONENT"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Check if we can match players now
        if (waitingPlayers.size() >= 2) {
            ConnectionToClient player1 = waitingPlayers.remove(0);
            ConnectionToClient player2 = waitingPlayers.remove(0);
            
            createGame(player1, player2);
        }
    }
    
    /**
     * Remove a player from any active game rooms
     */
    private void removePlayerFromGames(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        
        // This is a simple implementation - in a real game you might want to handle
        // this more gracefully, such as notifying the opponent about forfeit
        
        List<String> gamesToRemove = new ArrayList<>();
        
        for (String gameId : activeGames.keySet()) {
            GameRoom room = activeGames.get(gameId);
            
            if (username.equals(room.getWhitePlayer()) || username.equals(room.getBlackPlayer())) {
                gamesToRemove.add(gameId);
            }
        }
        
        for (String gameId : gamesToRemove) {
            System.out.println("Removing game " + gameId + " due to new game request from " + username);
            activeGames.remove(gameId);
        }
    }
    
    /**
     * Create a new game between two players
     */
    private void createGame(ConnectionToClient player1, ConnectionToClient player2) {
        String player1Username = (String) player1.getInfo("username");
        String player2Username = (String) player2.getInfo("username");
        String gameId = "game" + nextGameId++;
        
        System.out.println("Creating game between " + player1Username + " (White) and " + 
                         player2Username + " (Black) with ID: " + gameId);
        
        // Create game room with player1 as white, player2 as black
        GameRoom room = new GameRoom(gameId, player1Username, player2Username);
        activeGames.put(gameId, room);
        
        // Add both players to the room
        room.addPlayer(player1);
        room.addPlayer(player2);
        System.out.println("Added both players to game room " + gameId);
        room.startGame(); // Start the game
    }
    
    /**
     * Handle a game state update from a client
     */
    private void handleGameState(GameStateMessage stateMsg, ConnectionToClient client) {
        GameRoom room = activeGames.get(stateMsg.getGameId());
        
        if (room != null) {
            room.processGameState(stateMsg, client);
        } else {
            System.out.println("Error: Game room not found for ID " + stateMsg.getGameId());
        }
    }
    
    /**
     * Handle a game move from a client (DEPRECATED - kept for backward compatibility)
     */
    private void handleGameMove(GameMove move, ConnectionToClient client) {
        System.out.println("WARNING: Received deprecated GameMove message instead of GameStateMessage");
        
        GameRoom room = activeGames.get(move.getGameId());
        
        if (room != null) {
            room.processMove(move, client);
        } else {
            System.out.println("Error: Game room not found for ID " + move.getGameId());
        }
    }
    
    /**
     * Match a player with an opponent or create a waiting game
     */
    private void matchPlayer(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        
        // Add to waiting list
        if (!waitingPlayers.contains(client)) {
            waitingPlayers.add(client);
            System.out.println("Player " + username + " is waiting for an opponent.");
            
            try {
                // Notify the client they're waiting
                client.sendToClient(new InfoMessage("WAITING_FOR_OPPONENT"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Check if we can match players now
        if (waitingPlayers.size() >= 2) {
            ConnectionToClient player1 = waitingPlayers.remove(0);
            ConnectionToClient player2 = waitingPlayers.remove(0);
            
            createGame(player1, player2);
        }
    }
    
    /**
     * Handle a client disconnection
     */
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        
        if (username != null) {
            System.out.println("User " + username + " disconnected.");
            
            // Remove from waiting list if present
            waitingPlayers.remove(client);
            
            // Clean up any games the user was in
            removePlayerFromGames(client);
        }
    }
    
    /**
     * Log when a client connects
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected from " + client.getInetAddress());
    }
    
    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        int port = 12345;
        
        // Allow command line argument to override default port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 12345.");
            }
        }
        
        try {
            System.out.println("Attempting to connect to database...");
            Database db = new Database("jdbc:mysql://localhost:3306/chess_db", "student", "hello");
            ChessServer server = new ChessServer(port, db);
            
            System.out.println("Attempting to listen on port " + port);
            server.listen();
            System.out.println("Server successfully listening on port " + port);
            System.out.println("Waiting for client connections...");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}