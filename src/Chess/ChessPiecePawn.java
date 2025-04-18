package Chess;

public class ChessPiecePawn implements ChessPiece {
    private ChessPieceLocation location;
    private final ChessPieceColor color;

    ChessPiecePawn(ChessPieceLocation location, ChessPieceColor color) {
        this.location = location;
        this.color = color;
    }
    @Override
    public ChessPieceColor getColor() {
        return this.color;
    }

    @Override
    public ChessPieceRank getRank() {
        return ChessPieceRank.Pawn;
    }

    @Override
    public ChessPieceLocation getLocation() {
        return this.location;
    }

    @Override
    public void makeMove(ChessPieceLocation newLocation) {
        if (testMove(newLocation)) {
            this.location = newLocation;
        }
    }

    @Override
    public boolean testMove(ChessPieceLocation newLocation) {
        if (this.location.equals(newLocation)) return false;
        if (!this.location.isValid() || !newLocation.isValid()) return false;
        int deltaH = this.location.getHLocation() - newLocation.getHLocation();
        int deltaV = this.location.getVLocation() - newLocation.getVLocation();
        return this.color == ChessPieceColor.WHITE ?
                (deltaV == -1 && deltaH == 0)
                || (isAtStart() && (deltaV == -2 && deltaH == 0))
                || (deltaV == 1 && Math.abs(deltaH) == 1)
                : (deltaV == 1 && deltaH == 0)
                || (isAtStart() && (deltaV == 2 && deltaH == 0))
                || (deltaV == 1 && Math.abs(deltaH) == 1);
    }

    @Override
    public boolean isEligibleForPromotion() {
        return this.color == ChessPieceColor.BLACK
                ? this.location.getVLocation() == 7 : this.location.getVLocation() == 0;
    }

    public boolean isAtStart() {
        return this.color == ChessPieceColor.WHITE
                ? this.location.getVLocation() == 6 : this.location.getVLocation() == 2;
    }
}
