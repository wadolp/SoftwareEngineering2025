package Chess;

public class ChessPieceLocation {
    private final int HLocation;
    private final int VLocation;

    ChessPieceLocation(int HLocation, int VLocation) {
        if (HLocation >= 0 && HLocation < 8) {
            this.HLocation = HLocation;
        }
        else {
            this.HLocation = -1;
        }
        if (VLocation >= 0 && VLocation < 8) {
            this.VLocation = VLocation;
        }
        else {
            this.VLocation = -1;
        }
    }

    ChessPieceLocation(char HLocation, int VLocation) {
        switch (HLocation) {
            case 'a':
            case 'A':
                this.HLocation = 0;
                break;
            case 'b':
            case 'B':
                this.HLocation = 1;
                break;
            case 'c':
            case 'C':
                this.HLocation = 2;
                break;
            case 'd':
            case 'D':
                this.HLocation = 3;
                break;
            case 'e':
            case 'E':
                this.HLocation = 4;
                break;
            case 'f':
            case 'F':
                this.HLocation = 5;
                break;
            case 'g':
            case 'G':
                this.HLocation = 6;
                break;
            case 'h':
            case 'H':
                this.HLocation = 7;
                break;
            default:
                this.HLocation = -1;
        }
        if (VLocation >= 0 && VLocation <= 7)
            this.VLocation = VLocation;
        else
            this.VLocation = -1;
    }
    public boolean isValid() {
        return this.HLocation != -1 && this.VLocation != -1;
    }
    public int getHLocation() {
        return HLocation;
    }
    public int getVLocation() {
        return VLocation;
    }
}
