package client;

import shared.messages.GameMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;

/**
 * ChessBoard class for online chess game
 * Represents a chess game board UI and integrates with the ChessBoardModel
 */
public class ChessBoard extends JPanel {
    // Board dimensions
    private static final int SQUARE_SIZE = 60;
    
    // Game state
    private ChessBoardModel model;
    private boolean isCheckmate;    // Is the game over
    
    // Selection state
    private int selectedRow = -1;
    private int selectedCol = -1;
    
    // Networking
    private ChessClient client;
    private String gameId;
    private Game game;              // Game information
    
    // UI components
    private JPanel boardPanel;
    private JLabel statusLabel;

    /**
     * Constructor for the chess board
     */
    public ChessBoard(ChessClient client) {
        this.client = client;
        
        // Initialize game state
        model = new ChessBoardModel();
        isCheckmate = false;
        
        // Create a placeholder game until we get real game info
        this.game = new Game("tempId", "waiting...", "waiting...");
        
        // Set up the UI
        setLayout(new BorderLayout());
        
        // Create the board panel
        boardPanel = new JPanel(new GridLayout(ChessBoardModel.BOARD_SIZE, ChessBoardModel.BOARD_SIZE));
        boardPanel.setPreferredSize(new Dimension(
            ChessBoardModel.BOARD_SIZE * SQUARE_SIZE, 
            ChessBoardModel.BOARD_SIZE * SQUARE_SIZE
        ));
        add(boardPanel, BorderLayout.CENTER);
        
        // Create status label
        statusLabel = new JLabel("Waiting for opponent...");
        add(statusLabel, BorderLayout.SOUTH);
        
        updateBoardDisplay();
        
        // Add mouse listener for square clicks
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;
                handleSquareClick(row, col);
            }
        });
    }
    
    /**
     * Set up the game with information from the server
     */
    public void setGame(Game game) {
        this.game = game;
        this.gameId = game.getGameId();
        System.out.println("Game ID set to: " + this.gameId); // Add debugging
        updateStatusLabel();
    }
    
    /**
     * Update the visual display of the board
     */
    private void updateBoardDisplay() {
        boardPanel.removeAll();
        
        for (int row = 0; row < ChessBoardModel.BOARD_SIZE; row++) {
            for (int col = 0; col < ChessBoardModel.BOARD_SIZE; col++) {
                JPanel square = new JPanel();
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                
                // Set the background color (checkered pattern)
                if ((row + col) % 2 == 0) {
                    square.setBackground(new Color(240, 217, 181)); // Light square
                } else {
                    square.setBackground(new Color(181, 136, 99));  // Dark square
                }
                
                // Highlight selected square
                if (row == selectedRow && col == selectedCol) {
                    square.setBackground(new Color(186, 202, 68)); // Highlight color
                }
                
                // Add chess piece if present
                if (model.getPiece(row, col) != ChessBoardModel.EMPTY) {
                    square.add(createPieceLabel(model.getPiece(row, col), model.getPieceColor(row, col)));
                }
                
                boardPanel.add(square);
            }
        }
        
        updateStatusLabel();
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    /**
     * Create a label with the appropriate chess piece icon
     */
    private JLabel createPieceLabel(int pieceType, int pieceColor) {
        String colorStr = (pieceColor == ChessBoardModel.WHITE) ? "white" : "black";
        String pieceStr;
        
        switch (pieceType) {
            case ChessBoardModel.PAWN: pieceStr = "pawn"; break;
            case ChessBoardModel.KNIGHT: pieceStr = "knight"; break;
            case ChessBoardModel.BISHOP: pieceStr = "bishop"; break;
            case ChessBoardModel.ROOK: pieceStr = "rook"; break;
            case ChessBoardModel.QUEEN: pieceStr = "queen"; break;
            case ChessBoardModel.KING: pieceStr = "king"; break;
            default: pieceStr = ""; break;
        }
        
        // Load the image from the images folder
        ImageIcon icon = new ImageIcon("images/" + colorStr + "_" + pieceStr + ".png");
        
        // Resize image if needed
        if (icon.getIconWidth() > SQUARE_SIZE - 10 || icon.getIconHeight() > SQUARE_SIZE - 10) {
            Image img = icon.getImage();
            Image resizedImg = img.getScaledInstance(SQUARE_SIZE - 10, SQUARE_SIZE - 10, Image.SCALE_SMOOTH);
            icon = new ImageIcon(resizedImg);
        }
        
        JLabel pieceLabel = new JLabel(icon);
        pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        return pieceLabel;
    }
    
    /**
     * Update the status label with current game state
     */
    private void updateStatusLabel() {
        String status;
        
        if (isCheckmate) {
            status = (model.isWhiteTurn() ? "Black" : "White") + " wins by checkmate!";
        } else {
            String turnColor = model.isWhiteTurn() ? "White" : "Black";
            String turnPlayer = model.isWhiteTurn() ? game.getWhitePlayer() : game.getBlackPlayer();
            
            if (isMyTurn()) {
                status = "Your turn (" + turnColor + ")";
            } else {
                status = "Waiting for " + turnPlayer + " (" + turnColor + ")";
            }
        }
        
        statusLabel.setText(status);
    }
    
    /**
     * Handle a square being clicked on the board
     */
    private void handleSquareClick(int row, int col) {
        // Only allow moves if it's the player's turn and the game is not over
        if (isCheckmate || !isMyTurn()) {
            return;
        }
        
        // First click - select a piece
        if (selectedRow == -1 || selectedCol == -1) {
            // Check if there's a piece at the clicked square
            if (model.getPiece(row, col) != ChessBoardModel.EMPTY) {
                // Check if the piece belongs to the current player
                if ((model.isWhiteTurn() && model.getPieceColor(row, col) == ChessBoardModel.WHITE) ||
                    (!model.isWhiteTurn() && model.getPieceColor(row, col) == ChessBoardModel.BLACK)) {
                    
                    selectedRow = row;
                    selectedCol = col;
                    updateBoardDisplay();
                }
            }
        } 
        // Second click - try to move the selected piece
        else {
            // Cannot select the same square
            if (row == selectedRow && col == selectedCol) {
                selectedRow = -1;
                selectedCol = -1;
                updateBoardDisplay();
                return;
            }
            
            // Check if the move is valid
            // In ChessBoard.java - handleSquareClick method
            if (model.isValidMove(selectedRow, selectedCol, row, col)) {
                try {
                    // IMPORTANT: Make sure gameId is correctly set here!
                    System.out.println("Sending move with gameId: " + gameId);
                    client.sendToServer(new GameMove(gameId, selectedRow, selectedCol, row, col));
        
                    // Apply move locally
                    model.makeMove(selectedRow, selectedCol, row, col);
        
                 // Reset selection
                    selectedRow = -1;
                    selectedCol = -1;
        
                 // Update display
                updateBoardDisplay();
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error sending move to server", "Network Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Determine if it's the current player's turn
     */
    private boolean isMyTurn() {
        return (model.isWhiteTurn() && client.getUsername().equals(game.getWhitePlayer())) ||
               (!model.isWhiteTurn() && client.getUsername().equals(game.getBlackPlayer()));
    }
    
    /**
     * Process a move received from the opponent via the server
     */
    /**
 * Process a move received from the opponent via the server
 */
/**
 * Process a move received from the opponent via the server
 */
public void processOpponentMove(int fromRow, int fromCol, int toRow, int toCol) {
    System.out.println("Processing opponent move in ChessBoard: " + fromRow + "," + fromCol + 
                      " to " + toRow + "," + toCol);
    System.out.println("Current player: " + client.getUsername() + 
                      ", White player: " + game.getWhitePlayer() + 
                      ", Black player: " + game.getBlackPlayer());
    System.out.println("Is white turn: " + model.isWhiteTurn());
    
    // DON'T check if it's the player's turn - we need to apply opponent moves regardless
    // This was the original bug - we were skipping opponent moves!
    
    // Execute the move
    model.makeMove(fromRow, fromCol, toRow, toCol);
    
    // Update the display
    updateBoardDisplay();
}
/**
 * Get the game information
 */
public Game getGame() {
    return this.game;
}

/**
 * Get whether it's white's turn
 */
public boolean getIsWhiteTurn() {
    return model.isWhiteTurn();
}
}

