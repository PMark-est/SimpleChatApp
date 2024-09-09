package com.example.messagingapp.WebSocket;

import com.example.messagingapp.Models.Group;
import com.example.messagingapp.Models.GroupDTO;
import com.example.messagingapp.Models.User;
import com.example.messagingapp.Services.GroupService;
import com.example.messagingapp.Services.MessageService;
import com.example.messagingapp.Services.UsersService;
import com.example.messagingapp.WebSocket.Messages.DM;
import com.example.messagingapp.WebSocket.Messages.Invites;
import com.example.messagingapp.WebSocket.Messages.WSMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyHandler extends TextWebSocketHandler {
    //Igale sessionile tuleb saata sõnum eraldi
    private final UsersService usersService;
    private final MessageService messageService;
    private final GroupService groupService;

    private final Map<String, WebSocketSession> sessions = new Hashtable<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Object lock = new Object();
    private final Set<String> disconnects = new HashSet<>();
    private Thread connectionThread;

    MyHandler(UsersService usersService, MessageService messageService, GroupService groupService){
        this.usersService = usersService;
        this.messageService = messageService;
        this.groupService = groupService;
        connectionThread = new Thread((() -> {
            synchronized (lock){
                String username;
                while(true){
                    try {
                        if (disconnects.isEmpty()) lock.wait();
                        username = disconnects.stream().findFirst().get();
                        Thread.sleep(1000);
                        disconnects.remove(username);
                        usersService.userWentOffline(username, sessions.values());
                        // System.out.println("User actually disconnected");
                    } catch (InterruptedException e) {
                        // do nothing
                        // System.out.println("User refreshed");
                    }
                }
            }
        }));
        connectionThread.start();
    }

    private void notifyGroupMembers(WSMessage chatMessage, Long postID){
        List<User> usersInGroup = usersService.getUsersInGroup(chatMessage.getSenderGroupID());
        System.out.println(chatMessage.getSenderGroupID());
        usersInGroup.forEach(user -> {
            try {
                // kui kasutaja on lahti ühendatud, siis ära saada talle sõnumi aga pane andmebaasi mingi teate vms
                WebSocketSession session = sessions.get(user.getName());
                if (session == null) return;
                session.sendMessage(new TextMessage("1/"+chatMessage.send(user.getID(), postID)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void notifyDMRecipient(DM directMessage){
        User sender = usersService.getUserByName(directMessage.getSenderUsername()).get();
        User receiver = usersService.getUserByName(directMessage.getUserToDM()).get();

        Group group = groupService.getDirectMessageGroup(sender, receiver);
        if (group == null){
            Integer newGroupID = groupService.saveDM(sender, receiver);
            directMessage.setSenderGroupID(newGroupID);
        } else directMessage.setSenderGroupID(group.getID());
        Long postID = messageService.save(directMessage);
        List.of(sender, receiver).forEach(user -> {
            try {
                WebSocketSession session = sessions.get(user.getName());
                if (session == null) return;
                if(!user.getName().equals(sender.getUsername())) directMessage.setSenderUsername(sender.getUsername());
                else directMessage.setSenderUsername(receiver.getUsername());
                session.sendMessage(new TextMessage("2/"+directMessage.send(user.getID(), postID)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(directMessage.getSenderGroupID());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] parts = message.getPayload().split("/");
        int messageType = Integer.parseInt(parts[0]);
        // 1 - standard message to chat
        // 2 - dm
        // 3 - invites
        // 4 - connection
        // 5 - connections of users that that owner of a group added
        if (messageType == 1){
            WSMessage sentMessage = objectMapper.readValue(parts[1], WSMessage.class);
            notifyGroupMembers(sentMessage, messageService.save(sentMessage));
        } else if (messageType == 2) {
            DM sentMessage = objectMapper.readValue(parts[1], DM.class);
            notifyDMRecipient(sentMessage);
        } else if (messageType == 3){
            Invites invites = objectMapper.readValue(parts[1], Invites.class);
            List<User> users = new ArrayList<>();
            for (String invitedUser : invites.getInvitedUsers()) {
                users.add(usersService.getUserByName(invitedUser).get());
                sessions.get(invitedUser).sendMessage(new TextMessage("3/"+invites.send()));
                groupService.add(invitedUser, invites.getGroupID());
            }
            sessions.get(invites.getOwner()).sendMessage(new TextMessage("5/"+invites.notifyOwner(users)));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String name = getName(session.getHandshakeHeaders().get("cookie").getFirst());
        sessions.put(name, session);
        if (disconnects.contains(name)){
            disconnects.remove(name);
            connectionThread.interrupt();
            return;
        }
        usersService.userWentOnline(name, sessions.values());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String name = getName(session.getHandshakeHeaders().get("cookie").getFirst());
        synchronized (lock){
            if (disconnects.contains(name)) return;
            sessions.remove(name);
            disconnects.add(name);
            lock.notify();
        }
    }



    private String getName(String cookie){
        Pattern pattern = Pattern.compile("(?<=name=).*?(?=;|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(cookie);
        boolean found = matcher.find();
        if(found) {
            return matcher.group();
        }
        return null;
    }
}