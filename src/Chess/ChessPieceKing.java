package Chess;

public class ChessPieceKing implements ChessPiece {
    private ChessPieceLocation location;
    private final ChessPieceColor color;

    ChessPieceKing(ChessPieceLocation location, ChessPieceColor color) {
        this.location = location;
        this.color = color;
    }
    @Override
    public ChessPieceColor getColor() {
        return this.color;
    }

    @Override
    public ChessPieceRank getRank() {
        return ChessPieceRank.King;
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
        int deltaH = Math.abs(this.location.getHLocation() - newLocation.getHLocation());
        int deltaV = Math.abs(this.location.getVLocation() - newLocation.getVLocation());

        return deltaH <= 1 && deltaV <= 1;
    }

    @Override
    public boolean isEligibleForPromotion() {
        return false;
    }
}
