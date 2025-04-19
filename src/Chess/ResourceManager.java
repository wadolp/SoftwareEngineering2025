package Chess;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Manages resources for the Chess game, particularly images
 */
public class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Image> pieceImages = new HashMap<>();
    
    // Constants for file paths
    private static final String DEFAULT_IMAGE_PATH = "resources/pieces/";
    private static final int DEFAULT_PIECE_SIZE = 60;
    
    /**
     * Private constructor for singleton pattern
     */
    private ResourceManager() {
        loadPieceImages();
    }
    
    /**
     * Gets the single instance of ResourceManager
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    /**
     * Gets the piece images map
     */
    public Map<String, Image> getPieceImages() {
        return pieceImages;
    }
    
    /**
     * Gets a specific piece image
     */
    public Image getPieceImage(ChessPieceColor color, ChessPieceRank rank) {
        return pieceImages.get(color + "_" + rank);
    }
    
    /**
     * Loads all chess piece images from the resources folder
     */
    private void loadPieceImages() {
        ChessPieceColor[] colors = {ChessPieceColor.WHITE, ChessPieceColor.BLACK};
        ChessPieceRank[] ranks = {
            ChessPieceRank.King, 
            ChessPieceRank.Queen, 
            ChessPieceRank.Rook, 
            ChessPieceRank.Bishop, 
            ChessPieceRank.Knight, 
            ChessPieceRank.Pawn
        };
        
        // First check if the resources directory exists, create if it doesn't
        File resourceDir = new File(DEFAULT_IMAGE_PATH);
        if (!resourceDir.exists()) {
            resourceDir.mkdirs();
        }
        
        // Load images for each piece
        for (ChessPieceColor color : colors) {
            for (ChessPieceRank rank : ranks) {
                String key = color + "_" + rank;
                String fileName = color.toString().toLowerCase() + "_" + rank.toString().toLowerCase() + ".png";
                String path = DEFAULT_IMAGE_PATH + fileName;
                
                // Try to load the image from file
                try {
                    File imageFile = new File(path);
                    if (imageFile.exists()) {
                        pieceImages.put(key, ImageIO.read(imageFile));
                    } else {
                        // Create a placeholder and save it to the file
                        Image placeholder = createPlaceholderImage(color, rank);
                        pieceImages.put(key, placeholder);
                        
                        // Save the placeholder to file
                        BufferedImage bufferedPlaceholder = new BufferedImage(
                            DEFAULT_PIECE_SIZE, 
                            DEFAULT_PIECE_SIZE, 
                            BufferedImage.TYPE_INT_ARGB
                        );
                        Graphics2D g = bufferedPlaceholder.createGraphics();
                        g.drawImage(placeholder, 0, 0, null);
                        g.dispose();
                        
                        try {
                            ImageIO.write(bufferedPlaceholder, "png", imageFile);
                            System.out.println("Created placeholder image: " + path);
                        } catch (IOException e) {
                            System.out.println("Could not save placeholder image: " + path);
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error loading image: " + path);
                    pieceImages.put(key, createPlaceholderImage(color, rank));
                }
            }
        }
    }
    
    /**
     * Creates a placeholder image for a chess piece
     */
    private Image createPlaceholderImage(ChessPieceColor color, ChessPieceRank rank) {
        // Create a blank image
        BufferedImage img = new BufferedImage(DEFAULT_PIECE_SIZE, DEFAULT_PIECE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Set color based on piece color
        if (color == ChessPieceColor.WHITE) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
        }
        
        // Draw the basic shape
        g2d.fillOval(5, 5, 50, 50);
        g2d.setColor(color == ChessPieceColor.WHITE ? Color.BLACK : Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(5, 5, 50, 50);
        
        // Add text representing the piece
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String symbol = "";
        switch (rank) {
            case King:
                symbol = "K";
                break;
            case Queen:
                symbol = "Q";
                break;
            case Rook:
                symbol = "R";
                break;
            case Bishop:
                symbol = "B";
                break;
            case Knight:
                symbol = "N";
                break;
            case Pawn:
                symbol = "P";
                break;
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getHeight();
        g2d.drawString(symbol, (DEFAULT_PIECE_SIZE - textWidth) / 2, 
                      ((DEFAULT_PIECE_SIZE - textHeight) / 2) + fm.getAscent());
        
        g2d.dispose();
        return img;
    }
}