package client;

import ocsf.client.*;
import shared.messages.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Client implementation for the networked chess game
 */
public class ChessClient extends AbstractClient {
    private String username;
    private ChessBoard chessBoard;
    private LoginPanel loginPanel;
    private JFrame mainFrame;
    private boolean isRegistering = false; // Track if last attempt was registration
    
    /**
     * Create a new chess client
     * 
     * @param host Server hostname
     * @param port Server port
     */
    public ChessClient(String host, int port) {
        super(host, port);
    }
    
    /**
     * Handle messages received from the server
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof LoginResponse) {
            LoginResponse response = (LoginResponse) msg;
            if (response.isSuccess()) {
                SwingUtilities.invokeLater(() -> {
                    if (isRegistering) {
                        JOptionPane.showMessageDialog(
                            loginPanel,
                            "Registration successful! You are now logged in.",
                            "Registration Success",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        isRegistering = false;
                    }
                    loginPanel.loginSuccess();
                    showChessBoard();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    if (isRegistering) {
                        JOptionPane.showMessageDialog(
                            loginPanel,
                            "Registration failed. Username may already exist.",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE
                        );
                        isRegistering = false;
                    } else {
                        loginPanel.loginFailed();
                    }
                });
            }
        } else if (msg instanceof GameStartMessage) {
            GameStartMessage start = (GameStartMessage) msg;
            SwingUtilities.invokeLater(() -> {
                // Create a game object and pass it to the chess board
                Game game = new Game(
                    start.getGameId(), 
                    start.getWhitePlayer(), 
                    start.getBlackPlayer()
                );
                
                // Make sure we have a chess board
                if (chessBoard == null) {
                    showChessBoard();
                }
                
                chessBoard.setGame(game);
                
                // Update the window title
                String color = username.equals(start.getWhitePlayer()) ? "White" : "Black";
                mainFrame.setTitle("Chess - " + username + " (" + color + ")");
                
                System.out.println("Game started: " + start.getWhitePlayer() + " vs " + start.getBlackPlayer());
            });
        } else if (msg instanceof GameStateMessage) {
            // Handle the complete game state message
            GameStateMessage stateMsg = (GameStateMessage) msg;
            System.out.println("Received game state from: " + stateMsg.getLastMovePlayer() + 
                              " - game ID: " + stateMsg.getGameId());
            
            SwingUtilities.invokeLater(() -> {
                if (chessBoard != null) {
                    System.out.println("Processing game state in client, username: " + getUsername());
                    chessBoard.processGameState(stateMsg);
                } else {
                    System.out.println("ERROR: Received game state but chessBoard is null!");
                }
            });
        } else if (msg instanceof GameMove) {
            // DEPRECATED: For backward compatibility only
            GameMove move = (GameMove) msg;
            System.out.println("DEPRECATED: Received move instead of game state: " + 
                              move.getFromRow() + "," + move.getFromCol() + 
                              " to " + move.getToRow() + "," + move.getToCol() + 
                              " - game ID: " + move.getGameId());
            
            SwingUtilities.invokeLater(() -> {
                if (chessBoard != null) {
                    System.out.println("WARNING: Processing move in client instead of game state. " + 
                                     "This is deprecated behavior.");
                    chessBoard.processOpponentMove(
                        move.getFromRow(), 
                        move.getFromCol(), 
                        move.getToRow(), 
                        move.getToCol()
                    );
                } else {
                    System.out.println("ERROR: Received move but chessBoard is null!");
                }
            });
        } else if (msg instanceof InfoMessage) {
            // Handle info messages
            InfoMessage infoMsg = (InfoMessage) msg;
            String message = infoMsg.getMessage();
            
            if (message.equals("WAITING_FOR_OPPONENT")) {
                // The server has registered our request and we're waiting for an opponent
                SwingUtilities.invokeLater(() -> {
                    if (chessBoard != null) {
                        JOptionPane.showMessageDialog(
                            chessBoard,
                            "Waiting for an opponent to join the game.",
                            "New Game",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });
            }
        }
    }
    
    /**
     * Set flag to indicate a registration attempt
     */
    public void setRegistering(boolean registering) {
        this.isRegistering = registering;
    }
    
    /**
     * Show the login panel
     */
    public void showLoginPanel() {
        mainFrame = new JFrame("Chess Login");
        loginPanel = new LoginPanel(this);
        mainFrame.add(loginPanel);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Show the chess board after successful login
     */
    public void showChessBoard() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
        
        mainFrame = new JFrame("Online Chess - " + username);
        chessBoard = new ChessBoard(this);
        mainFrame.add(chessBoard);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Set the username for this client
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get the username for this client
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Main method to start the client
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        
        // Allow command line arguments to override defaults
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 12345.");
            }
        }
        
        ChessClient client = new ChessClient(host, port);
        
        try {
            System.out.println("Attempting to connect to server at " + host + ":" + port);
            client.openConnection();
            System.out.println("Connected to server successfully!");
            client.showLoginPanel();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null, 
                "Could not connect to server at " + host + ":" + port + "\n" +
                "Error: " + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
}