package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import shared.messages.NewGameRequest;

/**
 * Dialog shown when a game ends (checkmate, stalemate, etc.)
 */
public class GameEndDialog extends JDialog {
    private ChessClient client;
    private Game game;
    private int gameResult;
    private boolean isWhiteWinner;
    
    // Game result constants
    public static final int CHECKMATE = 1;
    public static final int STALEMATE = 2;
    public static final int RESIGNATION = 3;
    public static final int DISCONNECT = 4;
    
    /**
     * Create a new game end dialog
     * 
     * @param parent Parent frame
     * @param client Chess client
     * @param game Game information
     * @param gameResult Result type (CHECKMATE, STALEMATE, etc.)
     * @param isWhiteWinner True if white won, false if black won
     */
    public GameEndDialog(JFrame parent, ChessClient client, Game game, int gameResult, boolean isWhiteWinner) {
        super(parent, "Game Over", true);
        this.client = client;
        this.game = game;
        this.gameResult = gameResult;
        this.isWhiteWinner = isWhiteWinner;
        
        setupUI();
        
        // Center dialog on parent
        setLocationRelativeTo(parent);
    }
    
    /**
     * Set up the dialog UI
     */
    private void setupUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create result message
        JLabel resultLabel = new JLabel(getResultMessage(), JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(resultLabel, BorderLayout.NORTH);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Add player information
        JLabel whiteLabel = new JLabel("White: " + game.getWhitePlayer());
        JLabel blackLabel = new JLabel("Black: " + game.getBlackPlayer());
        detailsPanel.add(whiteLabel);
        detailsPanel.add(blackLabel);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> requestNewGame());
        
        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener(e -> {
            dispose();
            System.exit(0);
        });
        
        buttonsPanel.add(newGameBtn);
        buttonsPanel.add(quitBtn);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    /**
     * Get the appropriate result message based on the game result
     */
    private String getResultMessage() {
        String winnerName = isWhiteWinner ? game.getWhitePlayer() : game.getBlackPlayer();
        String loserName = isWhiteWinner ? game.getBlackPlayer() : game.getWhitePlayer();
        
        switch (gameResult) {
            case CHECKMATE:
                return winnerName + " wins by checkmate!";
            case STALEMATE:
                return "Draw by stalemate!";
            case RESIGNATION:
                return winnerName + " wins by resignation!";
            case DISCONNECT:
                return winnerName + " wins by disconnection!";
            default:
                return "Game ended";
        }
    }
    
    /**
     * Request a new game from the server
     */
    private void requestNewGame() {
        try {
            // Send a request for a new game
            client.sendToServer(new NewGameRequest(client.getUsername()));
            dispose(); // Close the dialog
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
}