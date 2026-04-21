package com.example.demo.config;

import com.example.demo.service.JwtService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setHandshakeHandler(new DefaultHandshakeHandler(){
                 @Override
                 protected @Nullable Principal determineUser(
                         ServerHttpRequest request,
                         WebSocketHandler wsHandler,
                         Map<String, Object> attributes) {

//                     String username = ((ServletServerHttpRequest) request)
//                             .getServletRequest()
//                             .getParameter("username");
//                     return () -> username;

                        String token = ((ServletServerHttpRequest) request)
                                .getServletRequest()
                                .getParameter("token");

                        String username = jwtService.extractUsername(token);

                        if(username == null){
                        throw new IllegalArgumentException("Invalid token");
                        }
                        return () -> username;
                 }
            })
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        // topic for public chat, queue for private chat
        registry.setApplicationDestinationPrefixes("/app");
        // for messages sent from client to server
        registry.setUserDestinationPrefix("/user");
        //for one to one chat
    }
}
