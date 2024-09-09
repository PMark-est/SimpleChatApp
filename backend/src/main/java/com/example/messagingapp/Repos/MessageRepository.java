package com.example.messagingapp.Repos;


import com.example.messagingapp.Models.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findFirst10ByGroup_IDOrderByIDDesc(Integer groupID);
    List<Message> findMessagesByGroup_IDAndIDBetween(Integer groupID, Long fromID, Long toID);
}
