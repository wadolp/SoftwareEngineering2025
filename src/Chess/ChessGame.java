package Chess;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;


public class ChessGame extends JFrame {
    private int windowSize = 1000;
    private double sidePanelRatio = .15; // How much space of the window does the side panel take up
    JPanel east = new JPanel();
    JPanel west = new JPanel();
    JPanel south = new JPanel();
    JPanel north = new JPanel();
    ChessBoard chessBoard;
    Rectangle[][] squareChessBounds;
    
    // Game state variables
    private ChessPiece[][] boardState = new ChessPiece[8][8];
    private ChessPiece selectedPiece = null;
    private int selectedX = -1;
    private int selectedY = -1;
    private boolean isWhiteTurn = true;
    
    // Resource manager for images
    private ResourceManager resourceManager;
    private Map<String, Image> pieceImages;
    
    // Game history tracking
    private StringBuilder moveHistory = new StringBuilder();
    private int moveNumber = 1;
    
    // Variables for mouse listener
    int[] centerPanelCoordinates = new int[4]; // stores the corners starting from top left clockwise
    
    public ChessGame() {
        // Set up the window
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(windowSize, windowSize));
        setLayout(new BorderLayout());
        
        // Initialize resource manager
        resourceManager = ResourceManager.getInstance();
        pieceImages = resourceManager.getPieceImages();
        
        // Initialize the chess board
        chessBoard = new ChessBoard();
        initializeChessBoard(); // Initialize pieces first
        chessBoard.setBoardState(boardState); // Then tell the board about the pieces
        chessBoard.setPieceImages(pieceImages); 
        
        // Add the chess board to the frame
        add(chessBoard, BorderLayout.CENTER);
        squareChessBounds = chessBoard.getSquareBounds();
        
        // Set up mouse listener for chess moves
        mouseListenerAdd();
        
        // Add side panels
        addSidePanels();
        addWindowResizeSensor();
        chessBoard.setSquareBorders();
        
        // Initialize the chess board with pieces
        initializeChessBoard();
        
        // Draw the initial board state
        repaint();
    }
    
    /*
     * Method name: initializeChessBoard
     * Description: Sets up the initial state of the chess board with all pieces
     * Input: None
     * Output: None
     */
    private void initializeChessBoard() {
        // Initialize empty board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardState[i][j] = null;
            }
        }
        
        // Set up pawns
        for (int i = 0; i < 8; i++) {
            boardState[1][i] = new ChessPiecePawn(new ChessPieceLocation(i, 1), ChessPieceColor.BLACK);
            boardState[6][i] = new ChessPiecePawn(new ChessPieceLocation(i, 6), ChessPieceColor.WHITE);
        }
        
        // Set up rooks
        boardState[0][0] = new ChessPieceRook(new ChessPieceLocation(0, 0), ChessPieceColor.BLACK);
        boardState[0][7] = new ChessPieceRook(new ChessPieceLocation(7, 0), ChessPieceColor.BLACK);
        boardState[7][0] = new ChessPieceRook(new ChessPieceLocation(0, 7), ChessPieceColor.WHITE);
        boardState[7][7] = new ChessPieceRook(new ChessPieceLocation(7, 7), ChessPieceColor.WHITE);
        
        // Set up knights
        boardState[0][1] = new ChessPieceKnight(new ChessPieceLocation(1, 0), ChessPieceColor.BLACK);
        boardState[0][6] = new ChessPieceKnight(new ChessPieceLocation(6, 0), ChessPieceColor.BLACK);
        boardState[7][1] = new ChessPieceKnight(new ChessPieceLocation(1, 7), ChessPieceColor.WHITE);
        boardState[7][6] = new ChessPieceKnight(new ChessPieceLocation(6, 7), ChessPieceColor.WHITE);
        
        // Set up bishops
        boardState[0][2] = new ChessPieceBishop(new ChessPieceLocation(2, 0), ChessPieceColor.BLACK);
        boardState[0][5] = new ChessPieceBishop(new ChessPieceLocation(5, 0), ChessPieceColor.BLACK);
        boardState[7][2] = new ChessPieceBishop(new ChessPieceLocation(2, 7), ChessPieceColor.WHITE);
        boardState[7][5] = new ChessPieceBishop(new ChessPieceLocation(5, 7), ChessPieceColor.WHITE);
        
        // Set up queens
        boardState[0][3] = new ChessPieceQueen(new ChessPieceLocation(3, 0), ChessPieceColor.BLACK);
        boardState[7][3] = new ChessPieceQueen(new ChessPieceLocation(3, 7), ChessPieceColor.WHITE);
        
        // Set up kings
        boardState[0][4] = new ChessPieceKing(new ChessPieceLocation(4, 0), ChessPieceColor.BLACK);
        boardState[7][4] = new ChessPieceKing(new ChessPieceLocation(4, 7), ChessPieceColor.WHITE);
    }
    
    /*
     * Method name: whenWindowResized
     * Description: Automatically adjusts anything that needs to be readjusted when the window is resized
     * Input: None
     * Output: None
     */
    private void whenWindowResized() {
        if (getWidth() > getHeight()) {
            setSize(new Dimension(getWidth(), getWidth()));
        } else {
            setSize(new Dimension(getHeight(), getHeight()));
        }
        
        // Resize borders
        north.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*getWidth())));
        west.setPreferredSize(new Dimension((int)(sidePanelRatio*getWidth()), 0));
        south.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*getWidth())));
        east.setPreferredSize(new Dimension((int)(sidePanelRatio*getWidth()), 0));
        
        chessBoard.generateSquareBounds();
        squareChessBounds = chessBoard.generateSquareBounds();
        
        // Repaint to update the pieces
        repaint();
    }
    
    /*
     * Method name: northPanelDesign
     * Description: Holds the design properties for the north panel (intermediary method)
     * Input: None
     * Output: None
     */
    private void northPanelDesign() {
        north.setVisible(true);
        north.setBackground(Color.GRAY);
        north.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*windowSize)));
        
        JLabel turnIndicator = new JLabel("White's Turn");
        turnIndicator.setFont(new Font("Arial", Font.BOLD, 16));
        turnIndicator.setVisible(true);
        north.add(turnIndicator);
    }
    
    /*
     * Method name: westPanelDesign
     * Description: Holds the design properties for the west panel (intermediary method)
     * Input: None
     * Output: None
     */
    private void westPanelDesign() {
        west.setVisible(true);
        west.setBackground(Color.GRAY);
        west.setPreferredSize(new Dimension((int)(sidePanelRatio*windowSize), 0));
        JLabel tempText = new JLabel("Captured White Pieces");
        tempText.setVisible(true);
        west.add(tempText);
    }
    
    /*
     * Method name: southPanelDesign
     * Description: Holds the design properties for the south panel (intermediary method)
     * Input: None
     * Output: None
     */
    private void southPanelDesign() {
        south.setVisible(true);
        south.setBackground(Color.GRAY);
        south.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*windowSize)));
        
        // Create move history components
        JLabel historyLabel = new JLabel("Move History");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create a text area for move history
        JTextArea moveHistoryArea = new JTextArea(3, 20);
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setBackground(new Color(240, 240, 240));
        
        // Create a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Add components to panel
        south.setLayout(new BorderLayout());
        south.add(historyLabel, BorderLayout.NORTH);
        south.add(scrollPane, BorderLayout.CENTER);
    }
    
    /*
     * Method name: eastPanelDesign
     * Description: Holds the design properties for the east panel (intermediary method)
     * Input: None
     * Output: None
     */
    private void eastPanelDesign() {
        east.setVisible(true);
        east.setBackground(Color.GRAY);
        east.setPreferredSize(new Dimension((int)(sidePanelRatio*windowSize), 100));
        JLabel tempText = new JLabel("Captured Black Pieces");
        tempText.setVisible(true);
        east.add(tempText);
    }
    
    /*
     * Method name: addSidePanels
     * Description: Function to handle calling intermediary build methods, and adding them to the frame.
     * Input: None
     * Output: None
     */
    private void addSidePanels() {
        northPanelDesign();
        eastPanelDesign();
        southPanelDesign();
        westPanelDesign();
        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);
        add(west, BorderLayout.WEST);
        revalidate();
    }
    
    /*
     * Method name: addWindowResizeSensor
     * Description: Only needs to be called once. Adds the resize listener
     * Input: None
     * Output: None
     */
    private void addWindowResizeSensor() {
        // Using javax.swing.Timer instead of java.util.Timer
        addComponentListener(new ComponentAdapter() {
            private javax.swing.Timer resizeTimer;

            @Override
            public void componentResized(ComponentEvent e) {
                if (resizeTimer != null && resizeTimer.isRunning()) {
                    resizeTimer.stop();
                }

                // Create a new timer that will call whenWindowResized after a delay
                resizeTimer = new javax.swing.Timer(300, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        whenWindowResized();
                        resizeTimer.stop();
                    }
                });
                
                // Set timer to run only once
                resizeTimer.setRepeats(false);
                
                // Start the timer
                resizeTimer.start();
            }
        });
    }
    
    private void mouseListenerAdd() {
        // Initialize the corner coordinates
        centerPanelCoordinates[0] = chessBoard.getLocation().x; // Stores left wall
        centerPanelCoordinates[1] = chessBoard.getLocation().y; // Stores top wall
        centerPanelCoordinates[2] = chessBoard.getLocation().x + chessBoard.getWidth(); // Stores right wall
        centerPanelCoordinates[3] = chessBoard.getLocation().y + chessBoard.getHeight(); // Stores bottom wall
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Get the position of the mouse
                Point p = e.getPoint();
                
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (squareChessBounds[i][j].contains(p)) {
                            handleBoardClick(j, i);
                            break;
                        }
                    }
                }
            }
        });
    }
    
    /*
     * Method name: handleBoardClick
     * Description: Handles the logic when a square on the board is clicked
     * Input: int x, int y (chess board coordinates)
     * Output: None
     */
    private void handleBoardClick(int x, int y) {
        String clickedSquare = convertCoordToNotation(x, y);
        System.out.println(clickedSquare);
        
        // If no piece is selected yet
        if (selectedPiece == null) {
            // Check if there's a piece at the clicked location
            if (boardState[y][x] != null) {
                // Check if it's the correct player's turn
                if ((isWhiteTurn && boardState[y][x].getColor() == ChessPieceColor.WHITE) ||
                    (!isWhiteTurn && boardState[y][x].getColor() == ChessPieceColor.BLACK)) {
                    // Select the piece
                    selectedPiece = boardState[y][x];
                    selectedX = x;
                    selectedY = y;
                    
                    // Highlight the selected piece (redraw the board)
                    chessBoard.highlightSquare(x, y);
                    System.out.println("Selected " + selectedPiece.getRank() + " at " + clickedSquare);
                }
            }
        } else {
            // A piece is already selected, try to move it
            ChessPieceLocation newLocation = new ChessPieceLocation(x, y);
            String startSquare = convertCoordToNotation(selectedX, selectedY);
            
            // Check if the move is valid
            if (selectedPiece.testMove(newLocation)) {
                // Check for additional game rules (collision, etc.)
                if (isValidMove(selectedX, selectedY, x, y)) {
                    // Execute the move
                    ChessPiece capturedPiece = boardState[y][x]; // May be null if no capture
                    boardState[y][x] = selectedPiece;
                    boardState[selectedY][selectedX] = null;
                    
                    // Update the piece's internal location
                    selectedPiece.makeMove(newLocation);
                    
                    // Check for pawn promotion
                    boolean wasPromoted = false;
                    if (selectedPiece.getRank() == ChessPieceRank.Pawn && selectedPiece.isEligibleForPromotion()) {
                        promotePawn(x, y);
                        wasPromoted = true;
                    }
                    
                    // Record the move in chess notation
                    recordMove(selectedPiece, startSquare, clickedSquare, capturedPiece, wasPromoted);
                    
                    // Switch turns
                    isWhiteTurn = !isWhiteTurn;
                    updateTurnIndicator();
                    
                    // Handle captured piece (if any)
                    if (capturedPiece != null) {
                        System.out.println("Captured " + capturedPiece.getRank());
                        // Add to captured pieces display
                        addToCapturedPieces(capturedPiece);
                    }
                    
                    System.out.println("Moved to " + clickedSquare);
                }
            }
            
            // Deselect the piece regardless of whether the move was successful
            selectedPiece = null;
            selectedX = -1;
            selectedY = -1;
            
            // Redraw the board
            chessBoard.clearHighlights();
            repaint();
        }
    }
    
    /*
     * Method name: recordMove
     * Description: Records a move in standard chess notation
     * Input: ChessPiece piece, String from, String to, ChessPiece captured, boolean promoted
     * Output: None
     */
    private void recordMove(ChessPiece piece, String from, String to, ChessPiece captured, boolean promoted) {
        StringBuilder moveNotation = new StringBuilder();
        
        // If white's move, add move number
        if (isWhiteTurn) {
            moveNotation.append(moveNumber).append(". ");
        }
        
        // Add piece symbol (except for pawns)
        if (piece.getRank() != ChessPieceRank.Pawn) {
            moveNotation.append(getPieceSymbol(piece.getRank()));
        }
        
        // Add capture symbol if there was a capture
        if (captured != null) {
            if (piece.getRank() == ChessPieceRank.Pawn) {
                // For pawns, include the file they moved from when capturing
                moveNotation.append(from.charAt(0));
            }
            moveNotation.append("x");
        }
        
        // Add destination square
        moveNotation.append(to);
        
        // Add promotion symbol if promoted
        if (promoted) {
            moveNotation.append("=Q"); // Currently we only promote to Queen
        }
        
        // Check and checkmate symbols would be added here
        
        // Add the move to history
        moveHistory.append(moveNotation).append(" ");
        
        // If black's move, end the line and increment move number
        if (!isWhiteTurn) {
            moveHistory.append("\n");
            moveNumber++;
        }
        
        // Update the move history display
        updateMoveHistoryDisplay();
    }
    
    /*
     * Method name: getPieceSymbol
     * Description: Returns the standard symbol for a chess piece
     * Input: ChessPieceRank rank
     * Output: String
     */
    private String getPieceSymbol(ChessPieceRank rank) {
        switch (rank) {
            case King: return "K";
            case Queen: return "Q";
            case Rook: return "R";
            case Bishop: return "B";
            case Knight: return "N";
            default: return "";
        }
    }
    
    /*
     * Method name: updateMoveHistoryDisplay
     * Description: Updates the move history display in the UI
     * Input: None
     * Output: None
     */
    private void updateMoveHistoryDisplay() {
        // Find the JTextArea in the south panel
        for (Component comp : south.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) view;
                    textArea.setText(moveHistory.toString());
                    
                    // Scroll to the bottom
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    break;
                }
            }
        }
    }
    
    /*
     * Method name: addToCapturedPieces
     * Description: Adds a captured piece to the appropriate display panel
     * Input: ChessPiece piece
     * Output: None
     */
    private void addToCapturedPieces(ChessPiece piece) {
        JPanel targetPanel = piece.getColor() == ChessPieceColor.WHITE ? west : east;
        
        // Create a small label with the piece image
        JLabel pieceLabel = new JLabel();
        String key = piece.getColor() + "_" + piece.getRank();
        Image pieceImage = pieceImages.get(key);
        
        // Scale down the image for the captured pieces panel
        if (pieceImage != null) {
            Image scaledImage = pieceImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            pieceLabel.setIcon(new ImageIcon(scaledImage));
            targetPanel.add(pieceLabel);
            targetPanel.revalidate();
            targetPanel.repaint();
        }
    }
    
    /*
     * Method name: isValidMove
     * Description: Checks for additional game rules (collision, etc.)
     * Input: int fromX, int fromY, int toX, int toY
     * Output: boolean - true if the move is valid
     */
    private boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        ChessPiece movingPiece = boardState[fromY][fromX];
        ChessPiece destinationPiece = boardState[toY][toX];
        
        // Can't capture your own pieces
        if (destinationPiece != null && destinationPiece.getColor() == movingPiece.getColor()) {
            return false;
        }
        
        // Check for path obstructions (except for knights which can jump)
        if (movingPiece.getRank() != ChessPieceRank.Knight) {
            // Determine the direction of movement
            int deltaX = Integer.compare(toX - fromX, 0);
            int deltaY = Integer.compare(toY - fromY, 0);
            
            // Check each square along the path (excluding start and end points)
            int checkX = fromX + deltaX;
            int checkY = fromY + deltaY;
            
            while (checkX != toX || checkY != toY) {
                if (boardState[checkY][checkX] != null) {
                    return false; // Path is obstructed
                }
                checkX += deltaX;
                checkY += deltaY;
            }
        }
        
        // Special handling for pawn captures (can only capture diagonally)
        if (movingPiece.getRank() == ChessPieceRank.Pawn) {
            int deltaX = Math.abs(toX - fromX);
            
            if (deltaX > 0) {
                // This is a diagonal move - must be capturing
                return destinationPiece != null;
            } else {
                // This is a forward move - must not be capturing
                return destinationPiece == null;
            }
        }
        
        return true;
    }
    
    /*
     * Method name: promotePawn
     * Description: Handles pawn promotion when a pawn reaches the opposite end
     * Input: int x, int y - position of the pawn
     * Output: None
     */
    private void promotePawn(int x, int y) {
        ChessPiece pawn = boardState[y][x];
        ChessPieceColor color = pawn.getColor();
        
        // For now, automatically promote to Queen
        // You could add a dialog to let the player choose
        boardState[y][x] = new ChessPieceQueen(new ChessPieceLocation(x, y), color);
        System.out.println("Pawn promoted to Queen at " + convertCoordToNotation(x, y));
    }
    
    /*
     * Method name: updateTurnIndicator
     * Description: Updates the turn indicator in the UI
     * Input: None
     * Output: None
     */
    private void updateTurnIndicator() {
        // Find the JLabel in the north panel
        for (Component comp : north.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setText(isWhiteTurn ? "White's Turn" : "Black's Turn");
                break;
            }
        }
    }
    
    // Takes in coords and converts to things like E4, H5
    private String convertCoordToNotation(int x, int y) {
        String[] columnLetter = {"a","b","c","d","e","f","g","h"};
        String[] rowNumber = {"8","7","6","5","4","3","2","1"};
        return columnLetter[x] + rowNumber[y];
    }
    
    public static void main(String args[]) {
        new ChessGame();
    }
}