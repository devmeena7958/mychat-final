package com.example.demo.config;

import com.example.demo.Model.ChatMessage;
import com.example.demo.Model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations msgTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // Track users
    private static final Set<String> activeUsers = new HashSet<>();
    private static final Map<String, String> sessionUserMap = new HashMap<>();


    @EventListener
    public void handleConnectListener(SessionConnectedEvent event){
        log.info("New WebSocket connection");
    }


    // 🔴 DISCONNECT
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String username = sessionUserMap.get(sessionId);

        if (username != null) {

            log.info("User disconnected: {}", username);

            activeUsers.remove(username);
            sessionUserMap.remove(sessionId);

            // Send OFFLINE message
            var chatMsg = ChatMessage.builder()
                    .type(MessageType.OFFLINE)
                    .sender(username)
                    .build();

            msgTemplate.convertAndSend("/topic/public", chatMsg);

            // 🔥 Update active users list
            messagingTemplate.convertAndSend("/topic/users", activeUsers);
        }
    }

    // 🧩 ADD USER (called from controller)
    public void addUser(String sessionId, String username) {
        activeUsers.add(username);
        sessionUserMap.put(sessionId, username);

        // broadcast updated list
        messagingTemplate.convertAndSend("/topic/users", activeUsers);
    }

}
