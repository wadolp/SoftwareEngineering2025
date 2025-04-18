package Chess;

public interface ChessPiece {
    public ChessPieceColor getColor();
    public ChessPieceRank getRank();
    public ChessPieceLocation getLocation();
    public void makeMove(ChessPieceLocation newLocation);
    public boolean testMove(ChessPieceLocation newLocation);
    public boolean isEligibleForPromotion();
}
