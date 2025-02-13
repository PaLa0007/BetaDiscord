package com.beta_discord_api.betadiscordapi.Controllers;

import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Entities.*;
import com.beta_discord_api.betadiscordapi.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // Create a new channel
    @PostMapping("/create")
    public ResponseEntity<ChannelDTO> createChannel(@RequestParam Long ownerId, @RequestBody String name) {
        ChannelDTO channel = channelService.createChannel(ownerId, name);
        return ResponseEntity.ok(channel);
    }

    // Delete a channel
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Map<String, String>> deleteChannel(
            @PathVariable Long channelId,
            @RequestParam Long userId) {

        channelService.deleteChannel(channelId, userId);

        // ✅ Return JSON response instead of plain text
        Map<String, String> response = new HashMap<>();
        response.put("message", "Channel deleted successfully.");
        return ResponseEntity.ok(response);
    }


    // Change channel name
    @PostMapping("/{channelId}/changeName")
    public ResponseEntity<String> changeChannelName(@PathVariable Long channelId,
                                                    @RequestParam Long userId,
                                                    @RequestBody String newName) {
        channelService.changeChannelName(channelId, userId, newName);
        return ResponseEntity.ok("Channel name updated successfully.");
    }

    // Get channel name
    @GetMapping("/{channelId}/name")
    public ResponseEntity<?> getChannelName(@PathVariable Long channelId) {
        String channelName = channelService.getChannelName(channelId);
        if ("Unknown Channel".equals(channelName)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Channel not found");
        }
        return ResponseEntity.ok(Collections.singletonMap("name", channelName));
    }

    // Add user to a channel
    @PostMapping("/{channelId}/addUser")
    public ResponseEntity<Map<String, String>> addUserToChannel(@PathVariable Long channelId,
                                                                @RequestParam Long userId,
                                                                @RequestParam Long addedUserId,
                                                                @RequestParam Role role) {
        channelService.addUserToChannel(channelId, userId, addedUserId, role);

        // ✅ Return a minimal JSON response instead of plain text
        Map<String, String> response = new HashMap<>();
        response.put("message", "User added successfully");

        return ResponseEntity.ok(response);
    }


    // Remove a user from a channel
    @PostMapping("/{channelId}/removeUser")
    public ResponseEntity<Map<String, String>> removeUserFromChannel(
            @PathVariable Long channelId,
            @RequestParam Long userId,
            @RequestParam Long removedUserId) {
        channelService.removeUserFromChannel(channelId, userId, removedUserId);

        // ✅ Return a proper JSON response instead of plain text
        Map<String, String> response = new HashMap<>();
        response.put("message", "User removed from channel.");

        return ResponseEntity.ok(response);
    }


    // Change a user's role
    @PostMapping("/{channelId}/changeRole")
    public ResponseEntity<Map<String, String>> changeUserRole(
            @PathVariable Long channelId,
            @RequestParam Long userId,
            @RequestParam Long targetUserId,
            @RequestParam Role newRole) {

        channelService.changeUserRole(channelId, userId, targetUserId, newRole);

        // ✅ Return a JSON object instead of a plain text string
        Map<String, String> response = new HashMap<>();
        response.put("message", "User role updated to: " + newRole);

        return ResponseEntity.ok(response);
    }


    // Retrieve messages from a channel
    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<MessageDTO>> getChannelMessages(@PathVariable Long channelId) {
        List<MessageDTO> messages = channelService.getMessagesFromChannel(channelId);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/{channelId}/messages")
    public ResponseEntity<?> addMessageToChannel(
            @PathVariable Long channelId,
            @RequestParam Long senderId,
            @RequestBody String content) {
        channelService.addMessageToChannel(channelId, senderId, content);
        return ResponseEntity.ok(Collections.singletonMap("message", "Message added to channel."));
    }

    // Get users in a channel grouped by roles
    @GetMapping("/{channelId}/users")
    public ResponseEntity<ChannelUsersDTO> getChannelUsers(@PathVariable Long channelId) {
        return ResponseEntity.ok(channelService.getUsersInChannel(channelId));
    }
}
