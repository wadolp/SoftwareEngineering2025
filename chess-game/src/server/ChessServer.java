package server;

import ocsf.server.*;
import shared.messages.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * Server implementation for the networked chess game
 */
public class ChessServer extends AbstractServer {
    private Database db;
    private HashMap<String, GameRoom> activeGames;
    private int nextGameId = 1;
    
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
        else if (msg instanceof GameMove) {
            handleGameMove((GameMove) msg, client);
        }
        // Handle other message types...
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
     * Handle a game move from a client
     */
    private void handleGameMove(GameMove move, ConnectionToClient client) {
        GameRoom room = activeGames.get(move.getGameId());
        
        if (room != null) {
            room.processMove(move, client);
        } else {
            System.out.println("Error: Game room not found for ID " + move.getGameId());
        }
    }
    private ConnectionToClient waitingPlayer = null;
    /**
     * Match a player with an opponent or create a waiting game
     */
    private void matchPlayer(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        
        if (waitingPlayer == null) {
            // No players waiting - add this player to waiting queue
            waitingPlayer = client;
            System.out.println("Player " + username + " is waiting for an opponent.");
        } else {
            // We have a waiting player - create a game
            String waitingUsername = (String) waitingPlayer.getInfo("username");
            String gameId = "game" + nextGameId++;
            
            System.out.println("Creating game between " + waitingUsername + " (White) and " + 
                             username + " (Black) with ID: " + gameId);
            
            // Create game room with the waiting player as white, new player as black
            GameRoom room = new GameRoom(gameId, waitingUsername, username);
            activeGames.put(gameId, room);
            
            // Add both players to the room
            room.addPlayer(waitingPlayer);
            room.addPlayer(client);
            System.out.println("Added both players to game room " + gameId);
            room.startGame(); // Start the game
            
            // Send game start message to both players
            GameStartMessage startMsg = new GameStartMessage(gameId, waitingUsername, username);
            try {
                waitingPlayer.sendToClient(startMsg);
                client.sendToClient(startMsg);
                System.out.println("Sent game start messages to both players");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Reset waiting player
            waitingPlayer = null;
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
            
            // Clean up any games the user was in
            // In a real implementation, you would notify opponents and handle forfeits
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