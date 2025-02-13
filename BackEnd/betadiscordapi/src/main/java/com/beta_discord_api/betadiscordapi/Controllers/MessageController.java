package com.beta_discord_api.betadiscordapi.Controllers;

import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // Send a private message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO) {
        if (messageDTO.getSenderId() == null || messageDTO.getReceiverId() == null || messageDTO.getContent() == null) {
            return ResponseEntity.badRequest().body("Sender ID, Receiver ID, and Content are required.");
        }

        try {
            MessageDTO savedMessage = messageService.sendMessage(
                    messageDTO.getSenderId(),
                    messageDTO.getReceiverId(),
                    messageDTO.getContent()
            );
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Retrieve private messages between two users
    @GetMapping("/private")
    public ResponseEntity<List<MessageDTO>> getPrivateMessages(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        List<MessageDTO> messages = messageService.getPrivateMessagesBetweenUsers(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }
}
