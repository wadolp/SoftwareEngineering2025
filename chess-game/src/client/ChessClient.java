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
            if (chessBoard != null) {
                SwingUtilities.invokeLater(() -> {
                    // Create a game object and pass it to the chess board
                    Game game = new Game(
                        start.getGameId(), 
                        start.getWhitePlayer(), 
                        start.getBlackPlayer()
                    );
                    chessBoard.setGame(game);
                    
                    // Update the window title
                    String color = username.equals(start.getWhitePlayer()) ? "White" : "Black";
                    mainFrame.setTitle("Chess - " + username + " (" + color + ")");
                });
            }
        } else if (msg instanceof GameMove) {
            GameMove move = (GameMove) msg;
            SwingUtilities.invokeLater(() -> {
                if (chessBoard != null) {
                    chessBoard.processOpponentMove(
                        move.getFromRow(), 
                        move.getFromCol(), 
                        move.getToRow(), 
                        move.getToCol()
                    );
                }
            });
        }
        // Handle other message types as needed
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