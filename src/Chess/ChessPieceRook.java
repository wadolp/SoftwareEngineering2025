package Chess;

public class ChessPieceRook implements ChessPiece {
    private ChessPieceLocation location;
    private final ChessPieceColor color;

    ChessPieceRook(ChessPieceLocation location, ChessPieceColor color) {
        this.location = location;
        this.color = color;
    }
    @Override
    public ChessPieceColor getColor() {
        return this.color;
    }

    @Override
    public ChessPieceRank getRank() {
        return ChessPieceRank.Rook;
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

        return this.location.getHLocation() == newLocation.getHLocation()
                || this.location.getVLocation() == newLocation.getVLocation();
    }

    @Override
    public boolean isEligibleForPromotion() {
        return false;
    }
}
