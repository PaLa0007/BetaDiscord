package com.beta_discord_api.betadiscordapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;            // Message ID
    private Long senderId;      // ID of the sender
    private String senderUsername; // Add sender name
    private Long receiverId;    // ID of the receiver (for private messages)
    private String content;     // Content of the message
    private LocalDateTime timestamp; // Time the message was sent
}
