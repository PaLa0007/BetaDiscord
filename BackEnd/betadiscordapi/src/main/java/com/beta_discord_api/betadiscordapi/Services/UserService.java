package com.beta_discord_api.betadiscordapi.Services;

import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Entities.*;
import com.beta_discord_api.betadiscordapi.Repos.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final ChannelRepository channelRepository; // ✅ Ensure this line exists

    // Create a new user
    public AppUser createUser(AppUser user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>()); // Ensure the friends set is initialized
        }
        return userRepository.save(user);
    }

    // Retrieve a user by username and map to UserDTO
    public Optional<AppUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    // Retrieve all active users (not deleted)
    public List<AppUser> getAllActiveUsers() {
        return userRepository.findAllActive();
    }

    // Update user's friends list
    public AppUser updateUserFriends(Long userId, Set<Long> friendIds) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        Set<AppUser> updatedFriends = friendIds.stream()
                .map(friendId -> userRepository.findById(friendId)
                        .orElseThrow(() -> new IllegalArgumentException("Friend with ID " + friendId + " not found.")))
                .collect(Collectors.toSet());

        // Update mutual relationships
        for (AppUser friend : updatedFriends) {
            friend.getFriends().add(user);
            userRepository.save(friend); // Save each friend to update their side of the relationship
        }

        // Clear old friends and set new ones
        user.getFriends().clear();
        user.getFriends().addAll(updatedFriends);

        return userRepository.save(user); // Save the user with updated friends
    }


    // Mark a user as deleted
    public void deleteUser(Long id) {
        Optional<AppUser> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            user.setDeleted(true); // Mark as deleted
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User with ID " + id + " not found.");
        }
    }

    // Add a friend to a user
    public AppUser addFriend(Long userId, Long friendId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        AppUser friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found."));

        // Add the friend to both sides of the relationship
        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);
        }
        if (!friend.getFriends().contains(user)) {
            friend.getFriends().add(user);
        }

        // Save both users to update the relationship in the database
        userRepository.save(friend); // Save friend first to ensure integrity
        return userRepository.save(user);
    }


    // Remove a friend from a user
    public AppUser removeFriend(Long userId, Long friendId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        AppUser friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        if (!user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Friend not found in user's friend list");
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user); // Ensure it's removed from both users
        userRepository.save(user);
        userRepository.save(friend);

        return user; // ✅ Ensure this returns AppUser, not UserDTO
    }




    public List<ChannelDTO> getUserChannels(Long userId) {
        AppUser user = userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return user.getChannels().stream()
                .filter(channel -> !channel.isDeleted()) // Exclude deleted channels
                .map(channel -> new ChannelDTO(
                        channel.getId(),
                        channel.getName(),
                        channel.getOwner().getId(),
                        channel.getUsers().stream().map(AppUser::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }


    public List<UserDTO> getUserFriends(Long userId) {
        AppUser user = userRepository.findByIdWithNestedFriends(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Log fetched user details
        System.out.println("Fetched User: " + user.getUsername());

        // Ensure friends' relationships are initialized and log details
        user.getFriends().forEach(friend -> {
            Hibernate.initialize(friend.getFriends());
            System.out.println("Friend: " + friend.getUsername());

            friend.getFriends().forEach(nestedFriend -> {
                Hibernate.initialize(nestedFriend.getFriends());
                System.out.println("Nested Friend of " + friend.getUsername() + ": " + nestedFriend.getUsername());
            });
        });

        // Map to UserDTO
        return user.getFriends().stream()
                .map(friend -> new UserDTO(
                        friend.getId(),
                        friend.getUsername(),
                        friend.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }


    public List<UserDTO> searchUsers(String username) {
        List<AppUser> users = userRepository.searchByUsername(username);
        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }

    // Add this method to UserService
    public AppUser login(String username, String password) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        if (user.isDeleted()) {
            throw new IllegalArgumentException("This account is deactivated.");
        }

        return user;
    }

    //Fetches the username of a user based on their ID.
    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findByIdAndNotDeleted(id);
    }

    // Search for users allowing partial search
    public List<UserDTO> searchUsersForChannel(String query, Long channelId) {
        List<AppUser> allUsers = userRepository.findUsersByPartialUsername(query); // Custom query needed
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        return allUsers.stream()
                .filter(user -> !channel.getUsers().contains(user)) // ✅ Exclude users already in the channel
                .map(user -> new UserDTO(user.getId(), user.getUsername(), null))
                .collect(Collectors.toList());
    }


}
