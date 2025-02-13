package com.beta_discord_api.betadiscordapi.Services;
import com.beta_discord_api.betadiscordapi.Entities.*;
import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Repos.*;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMessageRepository channelMessageRepository;

    // Helper method to check permissions
// ✅ Overloaded Method (For actions that involve a target user)
    private void checkPermission(Channel channel, AppUser user, Role requiredRole, String action, AppUser targetUser) {
        Role userRole = channel.getRoles().get(user);
        Role targetUserRole = channel.getRoles().get(targetUser);

        if (userRole == null || targetUserRole == null) {
            throw new IllegalArgumentException("User is not a member of the channel.");
        }

        boolean isOwner = userRole == Role.OWNER;
        boolean isAdmin = userRole == Role.ADMIN;

        if (requiredRole == Role.OWNER && !isOwner) {
            throw new IllegalArgumentException("Only the owner can " + action + ".");
        }

        if (requiredRole == Role.ADMIN) {
            if (isAdmin && targetUserRole == Role.GUEST) {
                return; // ✅ Admin removing a guest - Allowed
            }
            if (!isOwner) {
                throw new IllegalArgumentException("Only the owner can " + action + " non-guest users.");
            }
        }
    }

    // ✅ Overloaded Method (For actions that involve only the acting user)
    private void checkPermission(Channel channel, AppUser user, Role requiredRole, String action) {
        Role userRole = channel.getRoles().get(user);

        if (userRole == null) {
            throw new IllegalArgumentException("User is not a member of the channel.");
        }

        boolean isOwner = userRole == Role.OWNER;
        boolean isAdmin = userRole == Role.ADMIN;

        if (requiredRole == Role.OWNER && !isOwner) {
            throw new IllegalArgumentException("Only the owner can " + action + ".");
        }

        if (requiredRole == Role.ADMIN && !(isAdmin || isOwner)) {
            throw new IllegalArgumentException("Only admins or the owner can " + action + ".");
        }
    }


    // Create a new channel
    public ChannelDTO createChannel(Long ownerId, String name) {
        AppUser owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found."));

        Channel channel = Channel.builder()
                .name(name)
                .owner(owner)
                .build();

        channel.getUsers().add(owner);
        channel.getRoles().put(owner, Role.OWNER);

        Channel savedChannel = channelRepository.save(channel);

        return new ChannelDTO(
                savedChannel.getId(),
                savedChannel.getName(),
                savedChannel.getOwner().getId(),
                savedChannel.getUsers().stream().map(AppUser::getId).collect(Collectors.toSet())
        );
    }

    // Delete a channel
    public void deleteChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findByIdAndNotDeleted(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found or already deleted."));
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        checkPermission(channel, user, Role.OWNER, "delete the channel");

        channel.setDeleted(true);
        channelRepository.save(channel);
    }

    // Change channel name
    public void changeChannelName(Long channelId, Long userId, String newName) {
        Channel channel = channelRepository.findByIdAndNotDeleted(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found or already deleted."));
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        checkPermission(channel, user, Role.ADMIN, "change the channel name");

        // ✅ Save only the raw name, preventing JSON-like storage
        channel.setName(newName.trim()); // Trim to remove accidental spaces
        channelRepository.save(channel);
    }

    // Get channel name
    public String getChannelName(Long channelId) {
        return channelRepository.findChannelNameById(channelId)
                .orElse("Unknown Channel"); // ✅ Return default name if not found
    }

    // Add user to channel
    public void addUserToChannel(Long channelId, Long userId, Long addedUserId, Role role) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found."));
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        AppUser addedUser = userRepository.findById(addedUserId)
                .orElseThrow(() -> new IllegalArgumentException("User to be added not found."));

        checkPermission(channel, user, Role.ADMIN, "add users to the channel");

        if (!channel.getRoles().containsKey(addedUser)) {
            channel.getRoles().put(addedUser, role);
            channel.getUsers().add(addedUser);
            channelRepository.save(channel);
        } else {
            throw new IllegalArgumentException("User is already a member of the channel.");
        }
    }

    // Remove user from channel
    public void removeUserFromChannel(Long channelId, Long userId, Long removedUserId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        AppUser removedUser = userRepository.findById(removedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        // ✅ Use the updated checkPermission method to allow Admins to remove Guests
        checkPermission(channel, user, Role.ADMIN, "remove users", removedUser);

        Role removedUserRole = channel.getRoles().get(removedUser);

        if (removedUserRole == Role.OWNER) {
            throw new IllegalArgumentException("Cannot remove the owner of the channel.");
        }

        // ✅ Remove user from the channel
        channel.getRoles().remove(removedUser);
        channel.getUsers().remove(removedUser);
        channelRepository.save(channel);
    }

    // Change user role in channel
    public void changeUserRole(Long channelId, Long userId, Long targetUserId, Role newRole) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found."));
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found."));

        checkPermission(channel, user, Role.OWNER, "change roles");

        if (channel.getRoles().containsKey(targetUser)) {
            channel.getRoles().put(targetUser, newRole);
            channelRepository.save(channel);
        } else {
            throw new IllegalArgumentException("Target user is not a member of the channel.");
        }
    }

    // Retrieve messages from a channel
    public List<MessageDTO> getMessagesFromChannel(Long channelId) {
        return channelMessageRepository.findByChannelId(channelId).stream()
                .map(message -> new MessageDTO(
                        message.getId(),
                        message.getSender().getId(),
                        message.getSender().getUsername(),
                        null, // Channels do not have a receiver
                        message.getContent(),
                        message.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public void addMessageToChannel(Long channelId, Long senderId, String content) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found."));
        AppUser sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        // Ensure the sender is a member of the channel
        if (!channel.getUsers().contains(sender)) {
            throw new IllegalArgumentException("Sender is not a member of the channel.");
        }

        // Create a new message
        ChannelMessage message = ChannelMessage.builder()
                .channel(channel)
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        channelMessageRepository.save(message);
    }

    // Fetch users for a specific channel
    public ChannelUsersDTO getUsersInChannel(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found."));

        // Retrieve users grouped by role
        Map<AppUser, Role> userRoles = channel.getRoles();

        List<UserDTO> owners = userRoles.entrySet().stream()
                .filter(entry -> entry.getValue() == Role.OWNER)
                .map(entry -> new UserDTO(entry.getKey().getId(), entry.getKey().getUsername(), null))
                .collect(Collectors.toList());

        List<UserDTO> admins = userRoles.entrySet().stream()
                .filter(entry -> entry.getValue() == Role.ADMIN)
                .map(entry -> new UserDTO(entry.getKey().getId(), entry.getKey().getUsername(), null))
                .collect(Collectors.toList());

        List<UserDTO> guests = userRoles.entrySet().stream()
                .filter(entry -> entry.getValue() == Role.GUEST)
                .map(entry -> new UserDTO(entry.getKey().getId(), entry.getKey().getUsername(), null))
                .collect(Collectors.toList());

        return new ChannelUsersDTO(owners, admins, guests);
    }

}