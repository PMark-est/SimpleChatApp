package com.example.messagingapp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @ManyToOne
    @JoinColumn(name = "user_ID")
    private User user;
    private String message;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "group_ID", referencedColumnName = "ID")
    private Group group;
}
