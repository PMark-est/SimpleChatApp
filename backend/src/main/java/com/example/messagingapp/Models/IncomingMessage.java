package com.example.messagingapp.Models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class IncomingMessage {
    private String sender;
    private String content;

}
