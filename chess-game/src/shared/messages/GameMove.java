package shared.messages;
import java.io.Serializable;

public class GameMove implements Serializable {
    private String gameId;
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private String sender;
    
    public GameMove(String gameId, int fromRow, int fromCol, int toRow, int toCol) {
        this.gameId = gameId;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public int getFromRow() {
        return fromRow;
    }
    
    public int getFromCol() {
        return fromCol;
    }
    
    public int getToRow() {
        return toRow;
    }
    
    public int getToCol() {
        return toCol;
    }
    public String getSender() {
        return sender;
    }
}