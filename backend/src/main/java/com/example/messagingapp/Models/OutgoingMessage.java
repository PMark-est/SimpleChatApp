package com.example.messagingapp.Models;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OutgoingMessage {
    private Long ID;
    private String content;
    private String username;

}
