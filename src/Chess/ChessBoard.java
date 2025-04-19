package Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class ChessBoard extends JPanel {
    Rectangle[][] squareBorders;
    JPanel Squares[][];
    private Color lightSquareColor = new Color(240, 217, 181);
    private Color darkSquareColor = new Color(181, 136, 99);
    private Color highlightColor = new Color(255, 255, 0, 100);
    
    private Map<Point, Boolean> highlightedSquares = new HashMap<>();
    private ChessPiece[][] boardState;
    private Map<String, Image> pieceImages;
    
    public ChessBoard() {
        this.setLayout(new GridLayout(8,8));
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        Squares = generateBoard();
    }
    
    /**
     * Sets the board state reference (from ChessGame)
     */
    public void setBoardState(ChessPiece[][] boardState) {
        this.boardState = boardState;
    }
    
    /**
     * Sets the piece images reference (from ChessGame)
     */
    public void setPieceImages(Map<String, Image> pieceImages) {
        this.pieceImages = pieceImages;
    }
    
    /**
     * Highlights a square at the given coordinates
     */
    public void highlightSquare(int x, int y) {
        highlightedSquares.clear(); // Clear previous highlights
        highlightedSquares.put(new Point(x, y), true);
        repaint();
    }
    
    /**
     * Clears all highlighted squares
     */
    public void clearHighlights() {
        highlightedSquares.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // If we have a board state and images, draw the pieces
        if (boardState != null && pieceImages != null) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPiece piece = boardState[row][col];
                    if (piece != null) {
                        // Get the image for this piece
                        String key = piece.getColor() + "_" + piece.getRank();
                        Image img = pieceImages.get(key);
                        
                        if (img != null) {
                            // Calculate position to draw the image
                            Rectangle bounds = squareBorders[row][col];
                            
                            // Draw the image centered in the square
                            int imgWidth = img.getWidth(null);
                            int imgHeight = img.getHeight(null);
                            int x = bounds.x + (bounds.width - imgWidth) / 2;
                            int y = bounds.y + (bounds.height - imgHeight) / 2;
                            
                            g.drawImage(img, x, y, null);
                        }
                    }
                    
                    // Draw highlight if this square is highlighted
                    if (highlightedSquares.containsKey(new Point(col, row))) {
                        Rectangle bounds = squareBorders[row][col];
                        g.setColor(highlightColor);
                        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    }
                }
            }
        }
    }
    
    public void setSquareBorders() {
        squareBorders = generateSquareBounds();
    }
    
    /*
     * Method name: generateBoard: Creates the grid of panels, black and white
     * Input: None
     * Output: Double array of premade panels
     */
    private JPanel[][] generateBoard() {
        JPanel temporary[][] = new JPanel[8][8];
        
        // Generate JPanels for black and white 
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                int colorVal = 0;
                for (int u = 0; u < 8; u++) {
                    temporary[i][u] = new JPanel();
                    temporary[i][u].setSize(new Dimension(50,50));
                    if (colorVal == 0) {
                        temporary[i][u].setBackground(lightSquareColor);
                        colorVal = 1;
                    } else {
                        temporary[i][u].setBackground(darkSquareColor);
                        colorVal = 0;
                    }
                    temporary[i][u].setVisible(true);
                    this.add(temporary[i][u]);
                    this.repaint();
                }
            } else {
                int colorVal = 1;
                for (int u = 0; u < 8; u++) {
                    temporary[i][u] = new JPanel();
                    temporary[i][u].setSize(new Dimension(50,50));
                    if (colorVal == 0) {
                        temporary[i][u].setBackground(lightSquareColor);
                        colorVal = 1;
                    } else {
                        temporary[i][u].setBackground(darkSquareColor);
                        colorVal = 0;
                    }
                    temporary[i][u].setLocation(u*50, i*50);
                    temporary[i][u].setVisible(true);
                    this.add(temporary[i][u]);
                    this.repaint();
                }
            }
        }
        
        return temporary;
    }
    
    public Rectangle[][] generateSquareBounds() {
        Rectangle[][] temp = new Rectangle[8][8];
        for (int i = 0; i < 8; i++) {
            for (int u = 0; u < 8; u++) {
                Point location = Squares[i][u].getLocationOnScreen();
                temp[7-i][u] = new Rectangle(location.x, location.y, Squares[i][u].getWidth(), Squares[i][u].getHeight());
            }
        }
        return temp;
    }
    
    public Rectangle[][] getSquareBounds() {
        return squareBorders;
    }
}