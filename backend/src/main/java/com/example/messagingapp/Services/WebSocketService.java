package com.example.messagingapp.Services;

import com.example.messagingapp.Models.IncomingMessage;
import com.example.messagingapp.Models.OutgoingMessage;
import com.example.messagingapp.Models.UserDTO;
import com.example.messagingapp.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public OutgoingMessage sendMessage(IncomingMessage message) {
    Long id = messageService.save(message);
        return new OutgoingMessage(
            id,
            HtmlUtils.htmlEscape(message.getContent()),
            HtmlUtils.htmlEscape(message.getSender())
            );
    }

    public UserDTO userDisconnect(String username){
        var user = userRepository.findByName(username).get();
        user.setIsOnline(false);
        userRepository.save(user);
        return UserDTO.builder()
                .id(user.getID())
                .name(user.getName())
                .isOnline(false)
                .build();
    }

    public UserDTO userHasConnected(String username){
        var user = userRepository.findByName(username).get();
        user.setIsOnline(true);
        userRepository.save(user);
        return UserDTO.builder()
                .id(user.getID())
                .name(user.getName())
                .isOnline(true)
                .build();
    }
}
