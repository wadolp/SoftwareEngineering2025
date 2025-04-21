package client;

import shared.messages.GameMove;
import shared.messages.GameStateMessage;
import shared.messages.InfoMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * ChessBoard class for online chess game
 * Represents a chess game board UI and integrates with the ChessBoardModel
 */
public class ChessBoard extends JPanel {
    // Board dimensions
    private static final int SQUARE_SIZE = 60;
    
    // Game state
    private ChessBoardModel model;
    private boolean isGameOver = false;  // Is the game over
    
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
    private JLabel lastMoveLabel;
    private JButton newGameButton;
    private JPanel controlPanel;

    /**
     * Constructor for the chess board
     */
    public ChessBoard(ChessClient client) {
        this.client = client;
        
        // Initialize game state
        model = new ChessBoardModel();
        
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
        
        // Create control panel with status info and new game button
        controlPanel = new JPanel(new BorderLayout());
        
        // Status panel (top of control panel)
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel("Waiting for opponent...");
        lastMoveLabel = new JLabel("No moves yet");
        statusPanel.add(statusLabel);
        statusPanel.add(lastMoveLabel);
        controlPanel.add(statusPanel, BorderLayout.CENTER);
        
        // New game button (bottom of control panel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> requestNewGame());
        newGameButton.setEnabled(false);  // Disabled until game is over
        buttonPanel.add(newGameButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.SOUTH);
        
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
        System.out.println("Game ID set to: " + this.gameId);
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
                
                // Highlight the king if in check
                int currentColor = model.isWhiteTurn() ? ChessBoardModel.WHITE : ChessBoardModel.BLACK;
                if (model.isInCheck(currentColor)) {
                    int[] kingPos = model.findKing(currentColor);
                    if (kingPos != null && row == kingPos[0] && col == kingPos[1]) {
                        square.setBackground(new Color(232, 88, 73)); // Red highlight for check
                    }
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
        String gameStatus = model.checkGameStatus();
        
        if (gameStatus.equals("CHECKMATE")) {
            isGameOver = true;
            newGameButton.setEnabled(true);
            
            String winner = model.isWhiteTurn() ? "Black" : "White";
            status = winner + " Wins by Checkmate!";
        } else if (gameStatus.equals("STALEMATE")) {
            isGameOver = true;
            newGameButton.setEnabled(true);
            status = "Game Drawn by Stalemate!";
        } else if (gameStatus.equals("CHECK")) {
            String turnColor = model.isWhiteTurn() ? "White" : "Black";
            String turnPlayer = model.isWhiteTurn() ? game.getWhitePlayer() : game.getBlackPlayer();
            
            if (isMyTurn()) {
                status = "Your King is in Check! (" + turnColor + ")";
            } else {
                status = turnPlayer + "'s King is in Check! (" + turnColor + ")";
            }
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
     * Update the last move label
     */
    private void updateLastMoveLabel(String player, int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow >= 0 && fromCol >= 0 && toRow >= 0 && toCol >= 0) {
            String moveText = player + " moved from (" + fromRow + "," + fromCol + 
                             ") to (" + toRow + "," + toCol + ")";
            lastMoveLabel.setText(moveText);
        }
    }
    
    /**
     * Handle a square being clicked on the board
     */
    private void handleSquareClick(int row, int col) {
        // Only allow moves if it's the player's turn and the game is not over
        if (isGameOver || !isMyTurn()) {
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
            if (model.isValidMove(selectedRow, selectedCol, row, col)) {
                // Check if the move would leave or put the player in check
                int playerColor = model.getPieceColor(selectedRow, selectedCol);
                if (model.moveWouldLeaveInCheck(selectedRow, selectedCol, row, col)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "That move would leave your king in check!",
                        "Invalid Move",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                
                try {
                    System.out.println("Making move with gameId: " + gameId);
                    
                    // First apply the move locally
                    model.makeMove(selectedRow, selectedCol, row, col);
                    
                    // Create and send game state message
                    GameStateMessage stateMsg = model.createGameStateMessage(
                        gameId, 
                        client.getUsername()
                    );
                    
                    // Send the game state to the server
                    client.sendToServer(stateMsg);
        
                    // Reset selection
                    selectedRow = -1;
                    selectedCol = -1;
        
                    // Update display
                    updateBoardDisplay();
                    updateLastMoveLabel(client.getUsername(), selectedRow, selectedCol, row, col);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error sending move to server", "Network Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Request a new game from the server
     */
    private void requestNewGame() {
        try {
            // Send a message to the server requesting a new game
            client.sendToServer(new InfoMessage("NEW_GAME_REQUEST"));
            
            // Disable the button to prevent multiple requests
            newGameButton.setEnabled(false);
            statusLabel.setText("Waiting for a new game...");
            
            // Reset the game state
            isGameOver = false;
            selectedRow = -1;
            selectedCol = -1;
            model = new ChessBoardModel(); // Create a fresh board
            
            // Update the display
            updateBoardDisplay();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error requesting new game: " + e.getMessage(),
                "Network Error",
                JOptionPane.ERROR_MESSAGE
            );
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
     * Process a game state received from the server
     * @param stateMsg The complete game state from the server
     */
    public void processGameState(GameStateMessage stateMsg) {
        System.out.println("Processing game state in ChessBoard from player: " + stateMsg.getLastMovePlayer());
        System.out.println("Is white turn: " + stateMsg.isWhiteTurn());
        
        // Update the model with the new game state
        model.setGameState(stateMsg.getBoard(), stateMsg.getPieceColors(), stateMsg.isWhiteTurn());
        
        // Check for checkmate after updating
        String gameStatus = model.checkGameStatus();
        if (gameStatus.equals("CHECKMATE") || gameStatus.equals("STALEMATE")) {
            isGameOver = true;
            newGameButton.setEnabled(true);
        }
        
        // Update the display
        updateBoardDisplay();
        
        // Update the last move label
        updateLastMoveLabel(
            stateMsg.getLastMovePlayer(),
            stateMsg.getLastMoveFromRow(),
            stateMsg.getLastMoveFromCol(),
            stateMsg.getLastMoveToRow(),
            stateMsg.getLastMoveToCol()
        );
    }
    
    /**
     * This method is kept for backward compatibility but redirects to processGameState
     */
    public void processOpponentMove(int fromRow, int fromCol, int toRow, int toCol) {
        System.out.println("DEPRECATED: processOpponentMove called. This method should not be used anymore.");
        // Do nothing - we should be receiving complete game states now
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