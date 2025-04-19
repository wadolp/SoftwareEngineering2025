package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class Test extends JFrame {
    private static final int SQUARE_SIZE = 80;
    private final JPanel chessboardPanel;
    private final JLabel statusLabel;
    
    // Game controller
    private ChessGameController gameController;
    
    // UI state
    private ChessPiece selectedPiece = null;
    private JPanel selectedPanel = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    
    public Test() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize game controller
        gameController = new ChessGameController();
        
        // Create status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("White's turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
        
        // Create chessboard panel
        chessboardPanel = new JPanel(new GridLayout(8, 8));
        initializeBoard();
        add(chessboardPanel, BorderLayout.CENTER);
        
        // Size the window
        setSize(SQUARE_SIZE * 8 + 20, SQUARE_SIZE * 8 + 70);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    private void initializeBoard() {
        // Create the visual board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel square = new JPanel(new BorderLayout());
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                
                // Set square color
                Color squareColor = (row + col) % 2 == 0 ? Color.WHITE : new Color(139, 69, 19); // Brown for black squares
                square.setBackground(squareColor);
                square.setBorder(new LineBorder(Color.BLACK));
                
                // Store row and col as client properties for later retrieval
                square.putClientProperty("row", 7 - row); // Flip board for display
                square.putClientProperty("col", col);
                
                // Add piece if exists
                updateSquare(square, 7 - row, col);
                
                // Add mouse listener
                square.addMouseListener(new SquareClickListener());
                
                chessboardPanel.add(square);
            }
        }
    }
    
    private void updateSquare(JPanel square, int row, int col) {
        // Clear the square
        square.removeAll();
        
        // Add piece if exists
        ChessPiece piece = gameController.getPieceAt(row, col);
        if (piece != null) {
            JLabel pieceLabel = new JLabel();
            pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Create a simple representation of the piece
            String pieceName = getPieceName(piece);
            pieceLabel.setText(pieceName);
            pieceLabel.setForeground(piece.getColor() == ChessPieceColor.WHITE ? Color.BLUE : Color.RED);
            pieceLabel.setFont(new Font("Arial", Font.BOLD, 24));
            
            square.add(pieceLabel, BorderLayout.CENTER);
        }
        
        square.revalidate();
        square.repaint();
    }
    
    private String getPieceName(ChessPiece piece) {
        String symbol = "";
        switch (piece.getRank()) {
            case King -> symbol = "K";
            case Queen -> symbol = "Q";
            case Rook -> symbol = "R";
            case Bishop -> symbol = "B";
            case Knight -> symbol = "N";
            case Pawn -> symbol = "P";
        }
        return symbol;
    }
    
    private class SquareClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JPanel clickedPanel = (JPanel) e.getComponent();
            int row = (Integer) clickedPanel.getClientProperty("row");
            int col = (Integer) clickedPanel.getClientProperty("col");
            
            // If no piece is selected, try to select one
            if (selectedPiece == null) {
                ChessPiece piece = gameController.getPieceAt(row, col);
                if (piece != null && piece.getColor() == gameController.getCurrentPlayer()) {
                    selectedPiece = piece;
                    selectedPanel = clickedPanel;
                    selectedRow = row;
                    selectedCol = col;
                    
                    // Highlight the selected square
                    clickedPanel.setBorder(new LineBorder(Color.YELLOW, 3));
                }
            } 
            // If a piece is already selected, try to move it
            else {
                // If clicking on own piece, switch selection
                ChessPiece targetPiece = gameController.getPieceAt(row, col);
                if (targetPiece != null && targetPiece.getColor() == gameController.getCurrentPlayer()) {
                    selectedPanel.setBorder(new LineBorder(Color.BLACK));
                    selectedPiece = targetPiece;
                    selectedPanel = clickedPanel;
                    selectedRow = row;
                    selectedCol = col;
                    clickedPanel.setBorder(new LineBorder(Color.YELLOW, 3));
                }
                // Try to move the piece
                else if (gameController.movePiece(selectedRow, selectedCol, row, col)) {
                    // Check for pawn promotion
                    ChessPiece movedPiece = gameController.getPieceAt(row, col);
                    if (movedPiece != null && movedPiece.getRank() == ChessPieceRank.Pawn && movedPiece.isEligibleForPromotion()) {
                        promotePawn(row, col);
                    }
                    
                    // Update the visual board
                    updateSquare(selectedPanel, selectedRow, selectedCol);
                    updateSquare(clickedPanel, row, col);
                    
                    // Reset selection
                    selectedPanel.setBorder(new LineBorder(Color.BLACK));
                    selectedPiece = null;
                    selectedPanel = null;
                    selectedRow = -1;
                    selectedCol = -1;
                    
                    // Update status label
                    String playerName = (gameController.getCurrentPlayer() == ChessPieceColor.WHITE) ? "White" : "Black";
                    statusLabel.setText(playerName + "'s turn");
                    
                    // Check for checkmate or stalemate
                    if (gameController.isCheckmate()) {
                        String winner = (gameController.getCurrentPlayer() == ChessPieceColor.WHITE) ? "Black" : "White";
                        JOptionPane.showMessageDialog(Test.this, winner + " wins by checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        resetGame();
                    } else if (gameController.isStalemate()) {
                        JOptionPane.showMessageDialog(Test.this, "The game ends in a draw by stalemate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        resetGame();
                    }
                }
                // If invalid move, deselect
                else {
                    selectedPanel.setBorder(new LineBorder(Color.BLACK));
                    selectedPiece = null;
                    selectedPanel = null;
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
    }
    
    private void promotePawn(int row, int col) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
            this, 
            "Choose a piece for pawn promotion", 
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        ChessPieceRank newRank;
        switch (choice) {
            case 0: // Queen
                newRank = ChessPieceRank.Queen;
                break;
            case 1: // Rook
                newRank = ChessPieceRank.Rook;
                break;
            case 2: // Bishop
                newRank = ChessPieceRank.Bishop;
                break;
            case 3: // Knight
                newRank = ChessPieceRank.Knight;
                break;
            default: // Default to Queen if dialog is closed without selection
                newRank = ChessPieceRank.Queen;
        }
        
        gameController.promotePawn(row, col, newRank);
        
        // Update the square visually
        updateSquare(getSquarePanelAt(row, col), row, col);
    }
    
    private JPanel getSquarePanelAt(int row, int col) {
        // Find the panel at the given board position
        for (Component component : chessboardPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                int panelRow = (Integer) panel.getClientProperty("row");
                int panelCol = (Integer) panel.getClientProperty("col");
                
                if (panelRow == row && panelCol == col) {
                    return panel;
                }
            }
        }
        return null;
    }
    
    private void resetGame() {
        gameController.resetGame();
        
        // Update the visual board
        for (Component component : chessboardPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                int row = (Integer) panel.getClientProperty("row");
                int col = (Integer) panel.getClientProperty("col");
                updateSquare(panel, row, col);
                panel.setBorder(new LineBorder(Color.BLACK));
            }
        }
        
        // Reset selection
        selectedPiece = null;
        selectedPanel = null;
        selectedRow = -1;
        selectedCol = -1;
        
        // Update status label
        statusLabel.setText("White's turn");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test::new);
    }
}