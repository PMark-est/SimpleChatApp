package com.example.messagingapp.Models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    private String name;
    private Boolean directMessage = false;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "ID")
    private User owner;
    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER)
    private List<User> users;
    @OneToMany(mappedBy = "group")
    private List<Message> messages;
}
