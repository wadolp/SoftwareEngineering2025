package shared.messages;

import java.io.Serializable;

/**
 * Message for transmitting the entire game state between clients
 */
public class GameStateMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String gameId;
    private int[][] board;          // Stores piece types
    private int[][] pieceColors;    // Stores piece colors
    private boolean isWhiteTurn;    // Whose turn it is
    private String lastMovePlayer;  // Username of player who made the last move
    private int lastMoveFromRow;    // Last move start row
    private int lastMoveFromCol;    // Last move start column
    private int lastMoveToRow;      // Last move destination row
    private int lastMoveToCol;      // Last move destination column
    
    /**
     * Create a new game state message
     * 
     * @param gameId The game ID
     * @param board The board array (piece types)
     * @param pieceColors The piece colors array
     * @param isWhiteTurn Whether it's white's turn
     * @param lastMovePlayer Username of player who made the last move
     * @param lastMoveFromRow Last move start row
     * @param lastMoveFromCol Last move start column
     * @param lastMoveToRow Last move destination row
     * @param lastMoveToCol Last move destination column
     */
    public GameStateMessage(String gameId, int[][] board, int[][] pieceColors, boolean isWhiteTurn,
                           String lastMovePlayer, int lastMoveFromRow, int lastMoveFromCol, 
                           int lastMoveToRow, int lastMoveToCol) {
        this.gameId = gameId;
        
        // Deep copy the arrays to avoid reference issues
        this.board = new int[board.length][board[0].length];
        this.pieceColors = new int[pieceColors.length][pieceColors[0].length];
        
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = board[i][j];
                this.pieceColors[i][j] = pieceColors[i][j];
            }
        }
        
        this.isWhiteTurn = isWhiteTurn;
        this.lastMovePlayer = lastMovePlayer;
        this.lastMoveFromRow = lastMoveFromRow;
        this.lastMoveFromCol = lastMoveFromCol;
        this.lastMoveToRow = lastMoveToRow;
        this.lastMoveToCol = lastMoveToCol;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public int[][] getPieceColors() {
        return pieceColors;
    }
    
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    public String getLastMovePlayer() {
        return lastMovePlayer;
    }
    
    public int getLastMoveFromRow() {
        return lastMoveFromRow;
    }
    
    public int getLastMoveFromCol() {
        return lastMoveFromCol;
    }
    
    public int getLastMoveToRow() {
        return lastMoveToRow;
    }
    
    public int getLastMoveToCol() {
        return lastMoveToCol;
    }
}