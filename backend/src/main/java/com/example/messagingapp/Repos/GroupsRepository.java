package com.example.messagingapp.Repos;

import com.example.messagingapp.Models.Group;
import com.example.messagingapp.Models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface GroupsRepository extends CrudRepository<Group, Integer> {
    List<Group> findGroupsByUsers_Name(String username);
    List<Group> findGroupByDirectMessageIsTrueAndUsersIn(List<User> users);
}
