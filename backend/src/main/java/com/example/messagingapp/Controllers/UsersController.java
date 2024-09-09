package com.example.messagingapp.Controllers;

import com.example.messagingapp.Models.UserDTO;
import com.example.messagingapp.Services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {


    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/id")
    public ResponseEntity<List<UserDTO>> getUsersInGroup(@RequestParam Integer groupID) {
        List<UserDTO> users = new ArrayList<>();

        usersService.getUsersInGroup(groupID).forEach(user -> users.add(UserDTO.builder()
                .id(user.getID())
                .name(user.getName())
                .isOnline(user.getIsOnline())
                .build()));
        return ResponseEntity.ok(users);
    }
}
