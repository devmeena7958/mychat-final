package com.example.demo.repo;

import com.example.demo.Model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

    // We can leave this here just in case you need it later, but we won't use it for the chat history.
    List<ChatMessage> findTop50ByOrderByTimestampDesc();

    // 1. ADD THIS: Fetch ONLY public messages and sort them chronologically (ASC)
    @Query("SELECT m FROM ChatMessage m WHERE m.receiver IS NULL ORDER BY m.timestamp ASC")
    List<ChatMessage> findPublicMessages();

    // 2. FIX THIS: Change DESC to ASC so the private messages aren't upside down
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender = :userA AND m.receiver = :userB) OR " +
            "(m.sender = :userB AND m.receiver = :userA) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findPrivateMessages(@Param("userA")String userA, @Param("userB") String userB);

    // Add this to your existing queries in ChatMessageRepo.java
    @Query("SELECT m FROM ChatMessage m WHERE m.sender = :username OR m.receiver = :username ORDER BY m.timestamp ASC")
    List<ChatMessage> findAllMessagesByUser(@Param("username") String username);
}