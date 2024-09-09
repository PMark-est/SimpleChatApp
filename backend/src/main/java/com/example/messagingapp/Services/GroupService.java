package com.example.messagingapp.Services;

import com.example.messagingapp.Models.Group;
import com.example.messagingapp.Models.GroupDTO;
import com.example.messagingapp.Models.User;
import com.example.messagingapp.Repos.GroupsRepository;
import com.example.messagingapp.Repos.UserRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupsRepository groupsRepository;
    private final UserRepository userRepository;

    public List<Group> getAllGroups(String username) {
        return groupsRepository.findGroupsByUsers_Name(username);
    }

    public Group getDirectMessageGroup(User user1, User user2) {
        List<User> users = List.of(user1, user2);
        for (Group group : groupsRepository.findGroupByDirectMessageIsTrueAndUsersIn(users)) {
            if (new HashSet<>(group.getUsers()).containsAll(users)) return group;
        }
        return null;
    }

    public Integer saveDM(User user1, User user2) {
        Group newGroup =  groupsRepository.save(Group.builder()
                        .name("dm-"+user1.getUsername()+"-"+user2.getUsername())
                        .directMessage(true)
                        .users(List.of(user1, user2))
                .build());
        user1.getGroups().add(newGroup);
        user2.getGroups().add(newGroup);
        userRepository.save(user1);
        userRepository.save(user2);
        return newGroup.getID();
    }

    public GroupDTO createNewGroup(String username, String groupName) {
        User user = userRepository.findByName(username).get();
        Group newGroup = groupsRepository.save(Group.builder()
                        .owner(user)
                        .name(groupName)
                        .directMessage(false)
                        .users(List.of(user))
                .build());
        user.getGroups().add(newGroup);
        userRepository.save(user);
        return GroupDTO.builder()
                .owner(user.getUsername())
                .ID(newGroup.getID())
                .name(newGroup.getName())
                .build();
    }

    public void add(String username, Integer groupID){
        User user = userRepository.findByName(username).get();
        Group group = groupsRepository.findById(groupID).get();
        group.getUsers().add(user);
        groupsRepository.save(group);
        user.getGroups().add(group);
        userRepository.save(user);
    }

    public Group checkIfDMExists(String username1, String username2) {
        User user1 = userRepository.findByName(username1).get();
        User user2 = userRepository.findByName(username2).get();
        List<User> users = List.of(user1, user2);
        for (Group group : groupsRepository.findGroupByDirectMessageIsTrueAndUsersIn(users)) {
            if (new HashSet<>(group.getUsers()).containsAll(users)) return group;
        }
        return null;
    }
}
