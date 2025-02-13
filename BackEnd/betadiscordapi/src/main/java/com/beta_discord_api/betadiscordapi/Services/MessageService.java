package com.beta_discord_api.betadiscordapi.Services;

import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Entities.*;
import com.beta_discord_api.betadiscordapi.Repos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // Send a private message
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        AppUser sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));
        AppUser receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        return new MessageDTO(
                savedMessage.getId(),
                savedMessage.getSender().getId(),
                savedMessage.getSender().getUsername(), // ✅ Use senderUsername
                savedMessage.getReceiver() != null ? savedMessage.getReceiver().getId() : null,
                savedMessage.getContent(),
                savedMessage.getTimestamp()
        );
    }

    // Retrieve private messages between two users
    public List<MessageDTO> getPrivateMessagesBetweenUsers(Long senderId, Long receiverId) {
        List<Message> messages = messageRepository.findBySenderOrReceiver(senderId, receiverId);

        return messages.stream()
                .map(message -> new MessageDTO(
                        message.getId(),
                        message.getSender().getId(),
                        message.getSender().getUsername(), // ✅ Fix: Include senderUsername
                        message.getReceiver() != null ? message.getReceiver().getId() : null,
                        message.getContent(),
                        message.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
