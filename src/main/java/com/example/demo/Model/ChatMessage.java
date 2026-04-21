package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
//    private String chatId;
//    private String senderId;
//    private String recipientId;
//    @Lob
    @Column (nullable = false , length = 50000)
    private String content;
    private String sender;
    private String receiver;


    private MessageType type;
    private LocalDateTime timestamp;


}


