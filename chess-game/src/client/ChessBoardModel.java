package client;

import shared.messages.GameMove;
import shared.messages.GameStateMessage;

/**
 * Model class for chess board data and game logic
 */
public class ChessBoardModel {
    // Chess piece constants
    public static final int EMPTY = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    
    // Color constants
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    
    // Board dimensions
    public static final int BOARD_SIZE = 8;
    
    // Game state
    private int[][] board;          // Stores piece types
    private int[][] pieceColors;    // Stores piece colors
    private boolean isWhiteTurn;    // Whose turn it is
    
    // Last move information
    private int lastMoveFromRow = -1;
    private int lastMoveFromCol = -1;
    private int lastMoveToRow = -1;
    private int lastMoveToCol = -1;
    
    /**
     * Initialize the chess board model
     */
    public ChessBoardModel() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        pieceColors = new int[BOARD_SIZE][BOARD_SIZE];
        isWhiteTurn = true;
        
        initializeBoard();
    }
    
    /**
     * Set up the initial board position
     */
    private void initializeBoard() {
        // Set up pawns
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[1][col] = PAWN;
            pieceColors[1][col] = BLACK;
            
            board[6][col] = PAWN;
            pieceColors[6][col] = WHITE;
        }
        
        // Set up other black pieces
        board[0][0] = ROOK;
        board[0][1] = KNIGHT;
        board[0][2] = BISHOP;
        board[0][3] = QUEEN;
        board[0][4] = KING;
        board[0][5] = BISHOP;
        board[0][6] = KNIGHT;
        board[0][7] = ROOK;
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            pieceColors[0][col] = BLACK;
        }
        
        // Set up other white pieces
        board[7][0] = ROOK;
        board[7][1] = KNIGHT;
        board[7][2] = BISHOP;
        board[7][3] = QUEEN;
        board[7][4] = KING;
        board[7][5] = BISHOP;
        board[7][6] = KNIGHT;
        board[7][7] = ROOK;
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            pieceColors[7][col] = WHITE;
        }
    }
    
    /**
     * Check if a move is valid according to chess rules
     * @param move The move to validate
     * @return True if the move is valid, false otherwise
     */
    public boolean isValidMove(GameMove move) {   
        return isValidMove(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
    }
    
    /**
     * Apply a move to the board
     * @param move The move to apply
     */
    public void applyMove(GameMove move) {        
        makeMove(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
    }
    
    /**
     * Check if a move is valid
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     * @return True if the move is valid, false otherwise
     */
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Basic validation
        if (fromRow < 0 || fromRow >= BOARD_SIZE || fromCol < 0 || fromCol >= BOARD_SIZE ||
            toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }
        
        // Can't move an empty square
        if (board[fromRow][fromCol] == EMPTY) {
            return false;
        }
        
        // Can't capture your own piece
        if (board[toRow][toCol] != EMPTY && pieceColors[toRow][toCol] == pieceColors[fromRow][fromCol]) {
            return false;
        }
        
        // Can't move opponent's pieces
        if ((isWhiteTurn && pieceColors[fromRow][fromCol] != WHITE) ||
            (!isWhiteTurn && pieceColors[fromRow][fromCol] != BLACK)) {
            return false;
        }
        
        // Piece-specific movement validation
        int pieceType = board[fromRow][fromCol];
        
        switch (pieceType) {
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
    
    /**
     * Check if a pawn move is valid
     */
    private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol) {
        int direction = (pieceColors[fromRow][fromCol] == WHITE) ? -1 : 1;
        
        // Moving forward one square
        if (fromCol == toCol && toRow == fromRow + direction && board[toRow][toCol] == EMPTY) {
            return true;
        }
        
        // Moving forward two squares from starting position
        int startRow = (pieceColors[fromRow][fromCol] == WHITE) ? 6 : 1;
        if (fromRow == startRow && fromCol == toCol && toRow == fromRow + 2 * direction &&
            board[fromRow + direction][fromCol] == EMPTY && board[toRow][toCol] == EMPTY) {
            return true;
        }
        
        // Capturing diagonally
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && 
            board[toRow][toCol] != EMPTY && pieceColors[toRow][toCol] != pieceColors[fromRow][fromCol]) {
            return true;
        }
        
        // TODO: Add en passant and promotion logic for a complete implementation
        
        return false;
    }
    
    /**
     * Check if a knight move is valid
     */
    private boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Knight moves in L-shape: 2 squares in one direction and 1 square perpendicular
        return (Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 1) ||
               (Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 2);
    }
    
    /**
     * Check if a bishop move is valid
     */
    private boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Bishop moves diagonally
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) {
            return false;
        }
        
        // Check for pieces in the way
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        
        for (int i = 1; i < Math.abs(fromRow - toRow); i++) {
            if (board[fromRow + i * rowStep][fromCol + i * colStep] != EMPTY) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if a rook move is valid
     */
    private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Rook moves horizontally or vertically
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }
        
        // Check for pieces in the way
        if (fromRow == toRow) {
            int start = Math.min(fromCol, toCol);
            int end = Math.max(fromCol, toCol);
            
            for (int col = start + 1; col < end; col++) {
                if (board[fromRow][col] != EMPTY) {
                    return false;
                }
            }
        } else {
            int start = Math.min(fromRow, toRow);
            int end = Math.max(fromRow, toRow);
            
            for (int row = start + 1; row < end; row++) {
                if (board[row][fromCol] != EMPTY) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Check if a queen move is valid
     */
    private boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Queen moves like a rook or bishop
        return isValidRookMove(fromRow, fromCol, toRow, toCol) || 
               isValidBishopMove(fromRow, fromCol, toRow, toCol);
    }
    
    /**
     * Check if a king move is valid
     */
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        // King moves one square in any direction
        return Math.abs(fromRow - toRow) <= 1 && Math.abs(fromCol - toCol) <= 1;
        
        // TODO: Add castling logic for a complete implementation
    }
    
    /**
     * Execute a move on the board
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Store last move information
        this.lastMoveFromRow = fromRow;
        this.lastMoveFromCol = fromCol;
        this.lastMoveToRow = toRow;
        this.lastMoveToCol = toCol;
        
        // Move the piece
        board[toRow][toCol] = board[fromRow][fromCol];
        pieceColors[toRow][toCol] = pieceColors[fromRow][fromCol];
        
        // Clear the original square
        board[fromRow][fromCol] = EMPTY;
        pieceColors[fromRow][fromCol] = 0;
        
        // Check for pawn promotion (simplified)
        if (board[toRow][toCol] == PAWN) {
            if ((pieceColors[toRow][toCol] == WHITE && toRow == 0) ||
                (pieceColors[toRow][toCol] == BLACK && toRow == 7)) {
                // Promote to queen automatically
                board[toRow][toCol] = QUEEN;
            }
        }
        
        // Switch turns
        isWhiteTurn = !isWhiteTurn;
    }
    
    /**
     * Completely replace the board state with new state from server
     * 
     * @param newBoard New board array (piece types)
     * @param newPieceColors New piece colors array
     * @param newIsWhiteTurn New turn state
     */
    public void setGameState(int[][] newBoard, int[][] newPieceColors, boolean newIsWhiteTurn) {
        // Deep copy the arrays
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = newBoard[i][j];
                this.pieceColors[i][j] = newPieceColors[i][j];
            }
        }
        
        this.isWhiteTurn = newIsWhiteTurn;
        
        System.out.println("Game state updated. Is white turn: " + isWhiteTurn);
    }
    
    /**
     * Create a game state message with the current board state
     * 
     * @param gameId The game ID
     * @param lastMovePlayer Username of player who made the last move
     * @return A GameStateMessage containing the full board state
     */
    public GameStateMessage createGameStateMessage(String gameId, String lastMovePlayer) {
        return new GameStateMessage(
            gameId,
            board,
            pieceColors, 
            isWhiteTurn,
            lastMovePlayer,
            lastMoveFromRow,
            lastMoveFromCol,
            lastMoveToRow,
            lastMoveToCol
        );
    }
    
    /**
     * Check if a player is in check
     * @param color The color of the player to check (WHITE or BLACK)
     * @return True if the player is in check, false otherwise
     */
    public boolean isInCheck(int color) {
        // Find the king's position
        int[] kingPos = findKing(color);
        if (kingPos == null) {
            return false; // Shouldn't happen in a valid game
        }
        
        int kingRow = kingPos[0];
        int kingCol = kingPos[1];
        
        // Check if any opponent piece can capture the king
        int opponentColor = (color == WHITE) ? BLACK : WHITE;
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                // If it's an opponent's piece
                if (board[row][col] != EMPTY && pieceColors[row][col] == opponentColor) {
                    // Check if it can move to the king's position
                    if (isValidPieceMove(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if a piece can move from one position to another without considering turns
     * This is used for check detection
     */
    private boolean isValidPieceMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Basic validation
        if (fromRow < 0 || fromRow >= BOARD_SIZE || fromCol < 0 || fromCol >= BOARD_SIZE ||
            toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }
        
        // Can't move an empty square
        if (board[fromRow][fromCol] == EMPTY) {
            return false;
        }
        
        // Can't capture your own piece
        if (board[toRow][toCol] != EMPTY && pieceColors[toRow][toCol] == pieceColors[fromRow][fromCol]) {
            return false;
        }
        
        // Piece-specific movement validation
        int pieceType = board[fromRow][fromCol];
        
        switch (pieceType) {
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
    
    /**
     * Check if a move would leave the player in check
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     * @return True if the move leaves the player in check, false otherwise
     */
    public boolean moveWouldLeaveInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        // Save the current board state
        int[][] tempBoard = new int[BOARD_SIZE][BOARD_SIZE];
        int[][] tempColors = new int[BOARD_SIZE][BOARD_SIZE];
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                tempBoard[r][c] = board[r][c];
                tempColors[r][c] = pieceColors[r][c];
            }
        }
        
        // Get the player's color
        int playerColor = pieceColors[fromRow][fromCol];
        
        // Temporarily make the move
        board[toRow][toCol] = board[fromRow][fromCol];
        pieceColors[toRow][toCol] = pieceColors[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        pieceColors[fromRow][fromCol] = 0;
        
        // Check if the player is in check after the move
        boolean inCheck = isInCheck(playerColor);
        
        // Restore the original board
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = tempBoard[r][c];
                pieceColors[r][c] = tempColors[r][c];
            }
        }
        
        return inCheck;
    }
    
    /**
     * Check if a player is in checkmate
     * @param color The color of the player to check (WHITE or BLACK)
     * @return True if the player is in checkmate, false otherwise
     */
    public boolean isInCheckmate(int color) {
        // First, make sure the player is in check
        if (!isInCheck(color)) {
            return false;
        }
        
        // Try every possible move for every piece of this color
        for (int fromRow = 0; fromRow < BOARD_SIZE; fromRow++) {
            for (int fromCol = 0; fromCol < BOARD_SIZE; fromCol++) {
                // If this is a piece of the right color
                if (board[fromRow][fromCol] != EMPTY && pieceColors[fromRow][fromCol] == color) {
                    // Try moving it to every possible square
                    for (int toRow = 0; toRow < BOARD_SIZE; toRow++) {
                        for (int toCol = 0; toCol < BOARD_SIZE; toCol++) {
                            // Check if the move is valid according to chess rules
                            if (isValidPieceMove(fromRow, fromCol, toRow, toCol)) {
                                // Check if the move would get the player out of check
                                if (!moveWouldLeaveInCheck(fromRow, fromCol, toRow, toCol)) {
                                    // Found a valid move that avoids check
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // If we've tried all moves and none work, it's checkmate
        return true;
    }
    
    /**
     * Check game status for checkmate or stalemate
     * @return Game status string ("CHECKMATE", "STALEMATE", "CHECK", or "CONTINUE")
     */
    public String checkGameStatus() {
        int currentColor = isWhiteTurn ? WHITE : BLACK;
        
        if (isInCheckmate(currentColor)) {
            return "CHECKMATE";
        }
        
        if (isInCheck(currentColor)) {
            return "CHECK";
        }
        
        // Basic stalemate check - no legal moves but not in check
        boolean hasLegalMove = false;
        
        for (int fromRow = 0; fromRow < BOARD_SIZE; fromRow++) {
            for (int fromCol = 0; fromCol < BOARD_SIZE; fromCol++) {
                if (board[fromRow][fromCol] != EMPTY && pieceColors[fromRow][fromCol] == currentColor) {
                    for (int toRow = 0; toRow < BOARD_SIZE; toRow++) {
                        for (int toCol = 0; toCol < BOARD_SIZE; toCol++) {
                            if (isValidPieceMove(fromRow, fromCol, toRow, toCol) && 
                                !moveWouldLeaveInCheck(fromRow, fromCol, toRow, toCol)) {
                                hasLegalMove = true;
                                break;
                            }
                        }
                        if (hasLegalMove) break;
                    }
                }
                if (hasLegalMove) break;
            }
            if (hasLegalMove) break;
        }
        
        if (!hasLegalMove) {
            return "STALEMATE";
        }
        
        return "CONTINUE";
    }
    
    /**
     * Get the piece type at a specific position
     * @param row The row
     * @param col The column
     * @return The piece type (or EMPTY if no piece)
     */
    public int getPiece(int row, int col) {
        return board[row][col];
    }
    
    /**
     * Get the piece color at a specific position
     * @param row The row
     * @param col The column
     * @return The piece color
     */
    public int getPieceColor(int row, int col) {
        return pieceColors[row][col];
    }
    
    /**
     * Check if it's white's turn
     * @return True if it's white's turn, false if it's black's turn
     */
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    /**
     * Set whose turn it is
     * @param isWhiteTurn True for white's turn, false for black's turn
     */
    public void setWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
    }
    
    /**
     * Get the board array
     * @return The board array
     */
    public int[][] getBoard() {
        return board;
    }
    
    /**
     * Get the piece colors array
     * @return The piece colors array
     */
    public int[][] getPieceColors() {
        return pieceColors;
    }
    
    /**
     * Find the position of a player's king
     * @param color The color of the king to find
     * @return An array with [row, col] of the king's position, or null if not found
     */
    public int[] findKing(int color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == KING && pieceColors[row][col] == color) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }
}