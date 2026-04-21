package com.example.demo.controller;

import com.example.demo.Model.ChatMessage;
import com.example.demo.config.WebSocketEventListener;
import com.example.demo.repo.ChatMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepo chatMessageRepo;
    private final WebSocketEventListener webSocketEventListener;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage msg, Principal principal){

        msg.setSender(principal.getName());
        msg.setTimestamp(LocalDateTime.now());

        return chatMessageRepo.save(msg);
    }

    @MessageMapping("/chat.privateMessage")

    public void privateMessage(@Payload ChatMessage msg, Principal principal){

        msg.setSender(principal.getName());
        msg.setTimestamp(LocalDateTime.now());

        // Send message to specific user using SimpMessagingTemplate(same as @SendToUser but more specific)
        messagingTemplate.convertAndSendToUser(
                msg.getReceiver(),
                "/queue/messages",
                msg
        );

        // Save the message to the database
         chatMessageRepo.save(msg);

         // If you want to return the message to the sender as well, you can use SimpMessagingTemplate to send it back to the sender
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/messages",
                msg
        );
//        return chatMessageRepo.save(msg);

    }

    @GetMapping("/messages/public")
    public List<ChatMessage> getPublicMessages(){
        // ONLY fetch public messages, preventing private data leaks
        return chatMessageRepo.findPublicMessages();

//        List<ChatMessage> messages = chatMessageRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
//        return messages;
//        return chatMessageRepo.findAll(Sort.sort(ChatMessage.class).by(ChatMessage::getTimestamp).descending());

    }

    // 1. Update the Contacts endpoint
    @GetMapping("/messages/contacts")
    public java.util.Set<String> getActiveContacts(@RequestParam String username) {
        List<ChatMessage> userMessages = chatMessageRepo.findAllMessagesByUser(username);

        java.util.Set<String> contacts = new java.util.HashSet<>();

        for (ChatMessage msg : userMessages) {
            if (msg.getSender().equals(username) && msg.getReceiver() != null &&
                    !msg.getReceiver().equals("public") && !msg.getReceiver().trim().isEmpty()) {
                contacts.add(msg.getReceiver());
            }
            else if (msg.getReceiver() != null && msg.getReceiver().equals(username)) {
                contacts.add(msg.getSender());
            }
        }
        return contacts;
    }

    // 2. Update the Private Messages endpoint
    @GetMapping("/messages/private/{targetUser}")
    public List<ChatMessage> getPrivateMessages(@PathVariable String targetUser, @RequestParam String username){
        return chatMessageRepo.findPrivateMessages(username, targetUser);
    }

    @MessageMapping("/addUser")
    public void addUser(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();
        String username = message.getSender();

        // store in session
        headerAccessor.getSessionAttributes().put("username", username);

        webSocketEventListener.addUser(sessionId, username);
    }











//    @GetMapping("/chat")
//    @ResponseBody
//    public String uploadChat(){
//        chatMessageRepo.save(new com.example.demo.Model.ChatMessage("1","1chat","1sender","1recipient","Hello", LocalDateTime.now()));
//        return "DONE";
//    }
//
//    @GetMapping("/delete")
//    @ResponseBody
//    public String delChat(){
//        ChatMessage chat = chatMessageRepo.findById("1").orElse(null);
//        chatMessageRepo.delete(chat);
//        return "DELETED";
//    }


}
