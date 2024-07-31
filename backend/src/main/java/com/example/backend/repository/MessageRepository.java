package com.example.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiver(String sender, String receiver);
}
