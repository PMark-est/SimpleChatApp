package com.example.messagingapp.Repos;

import com.example.messagingapp.Models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByName(String name);
    Optional<User> findByID(Integer id);
    List<User> findByGroups_ID(Integer id);
}
