package com.example.messagingapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private Integer ID;
    private String name;
    private String owner;
    private String userToDM;
}
