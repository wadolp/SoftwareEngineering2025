// Create this as shared.messages.InfoMessage.java
package shared.messages;

import java.io.Serializable;

public class InfoMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String message;
    
    public InfoMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}