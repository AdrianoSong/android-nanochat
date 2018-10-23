package adrianosong.com.br.nanochat.model;

/**
 * Created by song on 17/10/16.
 * Model for chat messages
 */

public class ChatMessage {

    private String name;
    private String message;

    /**
     * Constructor for automatic binding
     */
    public ChatMessage(){}

    /**
     * Constructor for my own purpose
     * @param name String
     * @param message String
     */
    public ChatMessage(String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
