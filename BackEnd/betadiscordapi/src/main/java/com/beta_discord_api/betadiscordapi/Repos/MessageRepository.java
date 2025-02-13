package com.beta_discord_api.betadiscordapi.Repos;

import com.beta_discord_api.betadiscordapi.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Fetch messages between a sender and a receiver (private messages)
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :friendId) OR (m.sender.id = :friendId AND m.receiver.id = :userId)")
    List<Message> findBySenderOrReceiver(@Param("userId") Long userId, @Param("friendId") Long friendId);
}