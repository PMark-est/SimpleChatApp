package com.example.messagingapp.WebSocket.Messages;

import lombok.Builder;

@Builder
public class Connection {
    private String senderUsername;
    private Boolean isOnline;

    public String send(Integer senderID){
        return "{"
                + "\"senderID\": \"" + senderID + "\", "
                + "\"senderUsername\": \"" + senderUsername + "\", "
                + "\"isOnline\": \"" + isOnline + "\""
                + "}";
    }
}
