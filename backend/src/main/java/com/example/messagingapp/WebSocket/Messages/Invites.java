package com.example.messagingapp.WebSocket.Messages;

import com.example.messagingapp.Models.User;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Invites {
    private Integer groupID;
    private String groupName;
    private String owner;
    private String[] invitedUsers;

    public String send(){
        return "{"
                + "\"owner\": \"" + owner + "\", "
                + "\"groupID\": \"" + groupID + "\", "
                + "\"groupName\": \"" + groupName + "\""
                + "}";
    }

    public String notifyOwner(List<User> users){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"invitedUsers\" : {");
        int i = 0;
        for (User invitedUser : users) {
            builder.append(
                    "\"user" + i + "\" : {\"id\" : \"" + invitedUser.getID() + "\" , "
                    + "\"name\" : \"" + invitedUser.getUsername() + "\" , "
                    + "\"isOnline\" : \"" + invitedUser.getIsOnline() + "\"},"
            );
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("}}");
        return builder.toString();
    }
}
