package com.example.messagingapp.WebSocket.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WSMessage {
    private String message;
    private String senderUsername;
    private String senderID;
    private Integer senderGroupID;
    private Boolean isOnline;

    public String send(Integer senderID, Long postID){
        return "{"
                + "\"senderID\": \"" + senderID + "\", "
                + "\"senderUsername\": \"" + getSenderUsername() + "\", "
                + "\"message\": \"" + getMessage() + "\", "
                + "\"messageID\": \"" + postID + "\", "
                + "\"isOnline\": \"" + getIsOnline() + "\", "
                + "\"senderGroupID\": \"" + getSenderGroupID() + "\" "
                + "}";
    }
}
