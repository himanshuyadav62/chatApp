package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class ChatController {
    
    private MessageRepository messageRepository;
    private SimpMessagingTemplate messagingTemplate;
    private UserRepository userRepository;

    public ChatController(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat")
    public void send(Message message) {
        messageRepository.save(message); // Save message to the database
        if (message.getReceiver() == null || message.getReceiver().isEmpty()) {
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/messages", message);
        } else {
            // Send to specific user
            messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<String>> getUsers(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        List<String> users = userRepository.findAll().stream()
                .map(User::getEmail)
                .filter(email -> !email.equals(currentUserEmail))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(@RequestParam String user, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        List<Message> messages = messageRepository.findBySenderAndReceiverOrderByTimeStampDesc(currentUserEmail, user);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
}
