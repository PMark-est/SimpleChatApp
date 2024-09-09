package com.example.messagingapp.Services;

import com.example.messagingapp.Models.User;
import com.example.messagingapp.Models.UserDTO;
import com.example.messagingapp.Repos.UserRepository;
import com.example.messagingapp.WebSocket.Messages.Connection;
import com.example.messagingapp.WebSocket.Messages.WSMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;

    public void save(User user){
        userRepository.save(user);
    }

    public Optional<User> getUserByName(String name){
        return userRepository.findByName(name);
    }

    public Optional<User> getUserByID(Integer id){
        return userRepository.findByID(id);
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            users.add(UserDTO.builder()
                    .id(user.getID())
                    .name(user.getName())
                    .isOnline(user.getIsOnline())
                    .build());
        });
        return users;
    }

    public List<User> getUsersInGroup(Integer id){
        return userRepository.findByGroups_ID(id);
    }

    public void userWentOffline(String name, Collection<WebSocketSession> sessions){
        User user = userRepository.findByName(name).get();
        user.setIsOnline(false);
        user.setLastOnline(LocalDateTime.now());
        userRepository.save(user);
        sendNotifications(user, sessions);
    }

    public void userWentOnline(String name, Collection<WebSocketSession> sessions) throws Exception {
        User user = userRepository.findByName(name).get();
        user.setIsOnline(true);
        userRepository.save(user);
        sendNotifications(user, sessions);
    }

    private void sendNotifications(User user, Collection<WebSocketSession> sessions){
        Connection message = Connection.builder()
                .senderUsername(user.getUsername())
                .isOnline(user.getIsOnline())
                .build();
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage("4/"+message.send(user.getID())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Integer getUserID(String name){
        return userRepository.findByName(name).get().getID();
    }
}
