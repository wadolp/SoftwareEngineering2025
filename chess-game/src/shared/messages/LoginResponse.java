package shared.messages;
import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean success;
    
    public LoginResponse(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
}