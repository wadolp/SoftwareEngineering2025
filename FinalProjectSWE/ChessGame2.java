import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ChessGame2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessBoard board = new ChessBoard();
            board.setVisible(true);
        });
    }
}

class ChessBoard extends JFrame {
    private final int BOARD_SIZE = 8;
    private final int TILE_SIZE = 70;
    private final Color LIGHT_COLOR = new Color(240, 217, 181);
    private final Color DARK_COLOR = new Color(181, 136, 99);
    private final Color SELECTED_COLOR = new Color(170, 162, 58);
    private final Color CHECK_COLOR = new Color(255, 100, 100);
    
    private JPanel boardPanel;
    private JButton[][] squares;
    private ChessPiece[][] pieces;
    private ChessPiece selectedPiece;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isWhiteTurn = true;
    private boolean isCheck = false;
    private boolean isCheckmate = false;
    private JLabel statusLabel;
    
    // HashMap to store the piece images
    private HashMap<String, ImageIcon> pieceImages;
    
    public ChessBoard() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Load chess piece images
        loadPieceImages();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create control panel with New Game button
        JPanel controlPanel = new JPanel();
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> setupNewGame());
        controlPanel.add(newGameBtn);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Create the chess board
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squares = new JButton[BOARD_SIZE][BOARD_SIZE];
        pieces = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                
                // Set alternating colors for chess board
                Color squareColor = (row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;
                squares[row][col].setBackground(squareColor);
                
                final int finalRow = row;
                final int finalCol = col;
                squares[row][col].addActionListener(e -> handleSquareClick(finalRow, finalCol));
                
                squares[row][col].setBorderPainted(false);
                squares[row][col].setFocusPainted(false);
                
                boardPanel.add(squares[row][col]);
            }
        }
        
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Status panel for displaying turn information
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("White's turn");
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setupNewGame();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupNewGame() {
        // Clear the board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                pieces[row][col] = null;
                updateSquareDisplay(row, col);
            }
        }
        
        // Setup pawns
        for (int col = 0; col < BOARD_SIZE; col++) {
            pieces[1][col] = new ChessPiece(ChessPieceType.PAWN, false);
            pieces[6][col] = new ChessPiece(ChessPieceType.PAWN, true);
        }
        
        // Setup black pieces (top of board)
        pieces[0][0] = new ChessPiece(ChessPieceType.ROOK, false);
        pieces[0][1] = new ChessPiece(ChessPieceType.KNIGHT, false);
        pieces[0][2] = new ChessPiece(ChessPieceType.BISHOP, false);
        pieces[0][3] = new ChessPiece(ChessPieceType.QUEEN, false);
        pieces[0][4] = new ChessPiece(ChessPieceType.KING, false);
        pieces[0][5] = new ChessPiece(ChessPieceType.BISHOP, false);
        pieces[0][6] = new ChessPiece(ChessPieceType.KNIGHT, false);
        pieces[0][7] = new ChessPiece(ChessPieceType.ROOK, false);
        
        // Setup white pieces (bottom of board)
        pieces[7][0] = new ChessPiece(ChessPieceType.ROOK, true);
        pieces[7][1] = new ChessPiece(ChessPieceType.KNIGHT, true);
        pieces[7][2] = new ChessPiece(ChessPieceType.BISHOP, true);
        pieces[7][3] = new ChessPiece(ChessPieceType.QUEEN, true);
        pieces[7][4] = new ChessPiece(ChessPieceType.KING, true);
        pieces[7][5] = new ChessPiece(ChessPieceType.BISHOP, true);
        pieces[7][6] = new ChessPiece(ChessPieceType.KNIGHT, true);
        pieces[7][7] = new ChessPiece(ChessPieceType.ROOK, true);
        
        // Update the display
        updateBoardDisplay();
        
        // Reset game state
        isWhiteTurn = true;
        isCheck = false;
        isCheckmate = false;
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
        updateStatus();
    }
    
    private void handleSquareClick(int row, int col) {
        if (isCheckmate) {
            return; // Game is over, no more moves allowed
        }
        
        // First click - select a piece
        if (selectedPiece == null) {
            if (pieces[row][col] != null && pieces[row][col].isWhite == isWhiteTurn) {
                selectedPiece = pieces[row][col];
                selectedRow = row;
                selectedCol = col;
                squares[row][col].setBackground(SELECTED_COLOR);
            }
        } 
        // Second click - move the piece
        else {
            // If clicking the same square, deselect
            if (row == selectedRow && col == selectedCol) {
                deselectPiece();
                return;
            }
            
            // If clicking another piece of the same color, select that piece
            if (pieces[row][col] != null && pieces[row][col].isWhite == isWhiteTurn) {
                deselectPiece();
                selectedPiece = pieces[row][col];
                selectedRow = row;
                selectedCol = col;
                squares[row][col].setBackground(SELECTED_COLOR);
                return;
            }
            
            // Attempt to move the piece
            if (isValidMove(selectedRow, selectedCol, row, col)) {
                // Make a temporary move to check if it leaves king in check
                ChessPiece temp = pieces[row][col];
                pieces[row][col] = selectedPiece;
                pieces[selectedRow][selectedCol] = null;
                
                boolean stillInCheck = isKingInCheck(isWhiteTurn);
                
                if (stillInCheck) {
                    // Undo the temporary move - this move doesn't resolve check
                    pieces[selectedRow][selectedCol] = selectedPiece;
                    pieces[row][col] = temp;
                    deselectPiece();
                    JOptionPane.showMessageDialog(this, "You must move out of check!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Check for pawn promotion
                if (selectedPiece.type == ChessPieceType.PAWN) {
                    if ((selectedPiece.isWhite && row == 0) || (!selectedPiece.isWhite && row == 7)) {
                        pieces[row][col] = new ChessPiece(ChessPieceType.QUEEN, selectedPiece.isWhite);
                    }
                }
                
                // Update the display
                updateBoardDisplay();
                
                // Switch turns
                isWhiteTurn = !isWhiteTurn;
                
                // Check if opponent is now in check
                isCheck = isKingInCheck(isWhiteTurn);
                
                // Check for checkmate
                if (isCheck && isCheckmate(isWhiteTurn)) {
                    isCheckmate = true;
                    JOptionPane.showMessageDialog(this, 
                        "Checkmate! " + (isWhiteTurn ? "Black" : "White") + " wins!", 
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Update status
                updateStatus();
                
                // Reset selection
                selectedPiece = null;
                selectedRow = -1;
                selectedCol = -1;
            } else {
                deselectPiece();
            }
        }
    }
    
    private void updateStatus() {
        String status;
        if (isCheckmate) {
            status = "Checkmate! " + (isWhiteTurn ? "Black" : "White") + " wins!";
        } else if (isCheck) {
            status = (isWhiteTurn ? "Black" : "White") + "'s turn - " + (isWhiteTurn ? "White" : "Black") + " is in check!";
        } else {
            status = (isWhiteTurn ? "White" : "Black") + "'s turn";
        }
        statusLabel.setText(status);
    }
    
    private boolean isKingInCheck(boolean isWhiteKing) {
        int kingRow = -1, kingCol = -1;
        
        // Find the king's position
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.type == ChessPieceType.KING && piece.isWhite == isWhiteKing) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
            if (kingRow != -1) break;
        }
        
        // Check if any opponent piece can attack the king
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece piece = pieces[row][col];
                if (piece != null && piece.isWhite != isWhiteKing) {
                    if (isValidMove(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean isCheckmate(boolean isWhiteKing) {
        // Check if any move can get the king out of check
        for (int fromRow = 0; fromRow < BOARD_SIZE; fromRow++) {
            for (int fromCol = 0; fromCol < BOARD_SIZE; fromCol++) {
                ChessPiece piece = pieces[fromRow][fromCol];
                if (piece != null && piece.isWhite == isWhiteKing) {
                    for (int toRow = 0; toRow < BOARD_SIZE; toRow++) {
                        for (int toCol = 0; toCol < BOARD_SIZE; toCol++) {
                            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                                // Try the move
                                ChessPiece temp = pieces[toRow][toCol];
                                pieces[toRow][toCol] = piece;
                                pieces[fromRow][fromCol] = null;
                                
                                boolean stillInCheck = isKingInCheck(isWhiteKing);
                                
                                // Undo the move
                                pieces[fromRow][fromCol] = piece;
                                pieces[toRow][toCol] = temp;
                                
                                if (!stillInCheck) {
                                    return false; // Found a move that gets out of check
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return true; // No moves get out of check - checkmate
    }
    
    private void deselectPiece() {
        if (selectedRow >= 0 && selectedCol >= 0) {
            Color originalColor = (selectedRow + selectedCol) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;
            squares[selectedRow][selectedCol].setBackground(originalColor);
        }
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
    }
    
    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Basic move validation - can be expanded with chess rules
        ChessPiece piece = pieces[fromRow][fromCol];
        if (piece == null) return false;
        
        // Can't capture own pieces
        if (pieces[toRow][toCol] != null && pieces[toRow][toCol].isWhite == piece.isWhite) {
            return false;
        }
        
        // Implement piece-specific movement rules
        switch (piece.type) {
            case PAWN:
                return isValidPawnMove(fromRow, fromCol, toRow, toCol);
            case KNIGHT:
                return isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case BISHOP:
                return isValidBishopMove(fromRow, fromCol, toRow, toCol);
            case ROOK:
                return isValidRookMove(fromRow, fromCol, toRow, toCol);
            case QUEEN:
                return isValidQueenMove(fromRow, fromCol, toRow, toCol);
            case KING:
                return isValidKingMove(fromRow, fromCol, toRow, toCol);
            default:
                return false;
        }
    }
    
    private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol) {
        boolean isWhite = pieces[fromRow][fromCol].isWhite;
        int direction = isWhite ? -1 : 1; // White moves up (-1), Black moves down (+1)
        
        // Basic forward movement
        if (fromCol == toCol && pieces[toRow][toCol] == null) {
            // Single square forward
            if (toRow == fromRow + direction) {
                return true;
            }
            
            // Double square forward from starting position
            if ((isWhite && fromRow == 6 && toRow == 4) || (!isWhite && fromRow == 1 && toRow == 3)) {
                // Check if the path is clear
                int middleRow = fromRow + direction;
                if (pieces[middleRow][fromCol] == null) {
                    return true;
                }
            }
        }
        
        // Diagonal capture
        if ((toCol == fromCol - 1 || toCol == fromCol + 1) && toRow == fromRow + direction) {
            return pieces[toRow][toCol] != null && pieces[toRow][toCol].isWhite != isWhite;
        }
        
        return false;
    }
    
    private boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // Knights move in an L-shape: 2 squares in one direction and 1 square in the perpendicular direction
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    private boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // Bishops move diagonally
        if (rowDiff != colDiff) {
            return false;
        }
        
        // Check if the path is clear
        int rowDirection = toRow > fromRow ? 1 : -1;
        int colDirection = toCol > fromCol ? 1 : -1;
        
        int currentRow = fromRow + rowDirection;
        int currentCol = fromCol + colDirection;
        
        while (currentRow != toRow && currentCol != toCol) {
            if (pieces[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }
        
        return true;
    }
    
    private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Rooks move in straight lines (horizontally or vertically)
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }
        
        // Check if the path is clear
        if (fromRow == toRow) {
            // Horizontal movement
            int start = Math.min(fromCol, toCol) + 1;
            int end = Math.max(fromCol, toCol);
            
            for (int col = start; col < end; col++) {
                if (pieces[fromRow][col] != null) {
                    return false;
                }
            }
        } else {
            // Vertical movement
            int start = Math.min(fromRow, toRow) + 1;
            int end = Math.max(fromRow, toRow);
            
            for (int row = start; row < end; row++) {
                if (pieces[row][fromCol] != null) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Queens can move like rooks or bishops
        return isValidRookMove(fromRow, fromCol, toRow, toCol) || 
               isValidBishopMove(fromRow, fromCol, toRow, toCol);
    }
    
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // Kings move one square in any direction
        return rowDiff <= 1 && colDiff <= 1;
    }
    
    private void updateBoardDisplay() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                updateSquareDisplay(row, col);
            }
        }
    }
    
    private void updateSquareDisplay(int row, int col) {
        // Reset background color
        Color squareColor = (row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;
        squares[row][col].setBackground(squareColor);
        
        // Highlight king if in check
        ChessPiece piece = pieces[row][col];
        if (piece != null && piece.type == ChessPieceType.KING && isKingInCheck(piece.isWhite)) {
            squares[row][col].setBackground(CHECK_COLOR);
        }
        
        // Update piece display
        if (piece != null) {
            // Use image instead of text
            String pieceKey = piece.getImageKey();
            squares[row][col].setIcon(pieceImages.get(pieceKey));
            squares[row][col].setText(""); // Clear any text
        } else {
            squares[row][col].setIcon(null);
            squares[row][col].setText("");
        }
    }
    
    private void loadPieceImages() {
        pieceImages = new HashMap<>();
        try {
            // Define the piece types and colors
            String[] types = {"pawn", "knight", "bishop", "rook", "queen", "king"};
            String[] colors = {"white", "black"};
            
            // Load each image
            for (String color : colors) {
                for (String type : types) {
                    String key = color + "_" + type;
                    String imagePath = "images/" + key + ".png";
                    
                    // Load and scale the image
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage();
                    Image scaledImg = img.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
                    pieceImages.put(key, new ImageIcon(scaledImg));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading chess piece images: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Could not load piece images. Please ensure the 'images' folder exists with the required PNG files.",
                "Image Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

enum ChessPieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

class ChessPiece {
    public final ChessPieceType type;
    public final boolean isWhite;
    
    public ChessPiece(ChessPieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }
    
    public String getImageKey() {
        String color = isWhite ? "white" : "black";
        String pieceType = "";
        
        switch (type) {
            case PAWN: pieceType = "pawn"; break;
            case KNIGHT: pieceType = "knight"; break;
            case BISHOP: pieceType = "bishop"; break;
            case ROOK: pieceType = "rook"; break;
            case QUEEN: pieceType = "queen"; break;
            case KING: pieceType = "king"; break;
        }
        
        return color + "_" + pieceType;
    }
    
    // Keep this method for backward compatibility or if images fail to load
    public String getSymbol() {
        switch (type) {
            case PAWN: return "♟";
            case KNIGHT: return "♞";
            case BISHOP: return "♝";
            case ROOK: return "♜";
            case QUEEN: return "♛";
            case KING: return "♚";
            default: return "";
        }
    }
}