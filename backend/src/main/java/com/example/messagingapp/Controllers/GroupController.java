package com.example.messagingapp.Controllers;

import com.example.messagingapp.Models.Group;
import com.example.messagingapp.Models.GroupDTO;
import com.example.messagingapp.Models.NewGroupDAO;
import com.example.messagingapp.Models.User;
import com.example.messagingapp.Services.GroupService;
import com.example.messagingapp.Services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/all")
    public ResponseEntity<List<GroupDTO>> getGroups(@RequestParam String username) {
        List<GroupDTO> groups = new ArrayList<>();
        groupService.getAllGroups(username).forEach(group -> {
            User groupOwner = group.getOwner();
            String groupOwnerName = null;
            String groupName = group.getName();
            if (group.getDirectMessage()) {
                List<User> users = group.getUsers();
                if (users.get(0).getUsername().equals(username)) groupName = users.get(1).getUsername();
                else if (users.get(1).getUsername().equals(username)) groupName = users.get(0).getUsername();
            }
            if (groupOwner != null) groupOwnerName = groupOwner.getName();
            groups.add(
                GroupDTO.builder()
                        .ID(group.getID())
                        .name(groupName)
                        .owner(groupOwnerName)
                        .build());
        }
        );
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@RequestBody NewGroupDAO dao) {
        GroupDTO group = groupService.createNewGroup(dao.getUsername(), dao.getGroupName());
        return ResponseEntity.ok(group);
    }

    @GetMapping("/dmExists")
    public ResponseEntity<GroupDTO> checkIfDMExists(@RequestParam String username1, @RequestParam String username2) {
        Group group = groupService.checkIfDMExists(username1, username2);
        if (group == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(GroupDTO.builder()
                        .ID(group.getID())
                        .name(group.getName())
                        .build());
    }

}
