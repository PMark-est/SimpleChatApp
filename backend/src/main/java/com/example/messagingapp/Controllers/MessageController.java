package com.example.messagingapp.Controllers;

import com.example.messagingapp.Models.OutgoingMessage;
import com.example.messagingapp.Services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<OutgoingMessage>> initialMessages(@RequestParam Integer groupID ) {
        return ResponseEntity.ok(messageService.getInitialMessages(groupID));
    }

    @GetMapping("/get")
    public ResponseEntity<List<OutgoingMessage>> moreMessages(@RequestParam Long postID, @RequestParam Integer groupID) {
        return ResponseEntity.ok(messageService.getMoreMessages(postID-10, postID, groupID));
    }
}
