package com.example.messagingapp.Services;
import com.example.messagingapp.Models.IncomingMessage;
import com.example.messagingapp.Models.OutgoingMessage;
import com.example.messagingapp.Repos.GroupsRepository;
import com.example.messagingapp.Repos.MessageRepository;
import com.example.messagingapp.Models.Message;
import com.example.messagingapp.Repos.UserRepository;
import com.example.messagingapp.WebSocket.Messages.WSMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupsRepository groupsRepository;

    public Long save(IncomingMessage message) {
        var msg = Message.builder()
                .user(userRepository.findByName(message.getSender()).get())
                .message(HtmlUtils.htmlEscape(message.getContent()))
                .time(LocalDateTime.now())
                .build();
        messageRepository.save(msg);
        return msg.getID();
    }

    public Long save(WSMessage message){
        var msg = Message.builder()
                .user(userRepository.findByName(message.getSenderUsername()).get())
                .group(groupsRepository.findById(message.getSenderGroupID()).get())
                .message(HtmlUtils.htmlEscape(message.getMessage()))
                .time(LocalDateTime.now())
                .build();
        return messageRepository.save(msg).getID();
    }

    public List<OutgoingMessage> getInitialMessages(Integer groupID) {
        List<OutgoingMessage> messages = new ArrayList<>();
        messageRepository.findFirst10ByGroup_IDOrderByIDDesc(groupID).forEach(message -> {
            messages.add(
                    OutgoingMessage.builder()
                            .ID(message.getID())
                            .username(message.getUser().getName())
                            .content(message.getMessage())
                            .build()
            );
        });
        return messages;
    }

    public List<OutgoingMessage> getMoreMessages(Long start, Long end, Integer groupID) {
        List<OutgoingMessage> messages = new ArrayList<>();
        messageRepository.findMessagesByGroup_IDAndIDBetween(groupID, start, end).forEach(message -> {
            messages.add(
                    OutgoingMessage.builder()
                            .ID(message.getID())
                            .username(message.getUser().getName())
                            .content(message.getMessage())
                            .build()
            );
        });
        return messages;
    }
}
