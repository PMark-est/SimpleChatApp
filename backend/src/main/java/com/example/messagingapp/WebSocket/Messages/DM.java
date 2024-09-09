package com.example.messagingapp.WebSocket.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DM extends WSMessage {
    private String userToDM;

    public String send(Integer senderID, Long postID){
        return "{"
                + "\"senderID\": \"" + senderID + "\", " //
                + "\"senderUsername\": \"" + getSenderUsername() + "\", "
                + "\"message\": \"" + getMessage() + "\", "
                + "\"messageID\": \"" + postID + "\", "
                + "\"isOnline\": \"" + getIsOnline() + "\", "
                + "\"senderGroupID\": \"" + getSenderGroupID() + "\", "
                + "\"userToDM\": \"" + userToDM + "\""
                + "}";
    }
}
