package com.beta_discord_api.betadiscordapi.Controllers;

import com.beta_discord_api.betadiscordapi.Dtos.*;
import com.beta_discord_api.betadiscordapi.Services.*;
import com.beta_discord_api.betadiscordapi.Entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus; // For HTTP status codes
import org.springframework.http.ResponseEntity; // For HTTP responses
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        AppUser newUser = AppUser.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();

        AppUser savedUser = userService.createUser(newUser);

        UserDTO responseDTO = new UserDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
        );

        return ResponseEntity.ok(responseDTO);
    }

    // Get a user by username
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        Optional<AppUser> userOptional = userService.getUserByUsername(username);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
            );
            return ResponseEntity.ok(userDTO);
        }

        return ResponseEntity.status(404).body("User not found.");
    }


    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllActiveUsers().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Update a user's friends
    @PutMapping("/{userId}/friends")
    public ResponseEntity<?> updateUserFriends(@PathVariable Long userId, @RequestBody Set<Long> friendIds) {
        if (userId == null || friendIds == null) {
            return ResponseEntity.badRequest().body("User ID and Friend IDs are required.");
        }

        try {
            AppUser updatedUser = userService.updateUserFriends(userId, friendIds);
            UserDTO userDTO = new UserDTO(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
            );
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


    // Mark a user as deleted
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Add a friend to a user
    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        if (userId == null || friendId == null) {
            return ResponseEntity.badRequest().body("User ID and Friend ID are required.");
        }

        try {
            AppUser updatedUser = userService.addFriend(userId, friendId);
            UserDTO userDTO = new UserDTO(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
            );
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


    // Remove a friend from a user
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        if (userId == null || friendId == null) {
            return ResponseEntity.badRequest().body("User ID and Friend ID are required.");
        }

        try {
            // ✅ `removeFriend` now correctly returns AppUser
            AppUser updatedUser = userService.removeFriend(userId, friendId);

            // Convert to UserDTO to avoid infinite recursion issues
            UserDTO userDTO = new UserDTO(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
            );

            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Gives a list of the channels a user is in
    @GetMapping("/{userId}/channels")
    public ResponseEntity<?> getUserChannels(@PathVariable Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        try {
            List<ChannelDTO> userChannels = userService.getUserChannels(userId);
            return ResponseEntity.ok(userChannels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Gives a list of user's friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        try {
            List<UserDTO> userFriends = userService.getUserFriends(userId);
            return ResponseEntity.ok(userFriends);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        List<UserDTO> users = userService.searchUsers(username);
        return ResponseEntity.ok(users);
    }

    // Add this method to UserController
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required.");
        }

        try {
            AppUser loggedInUser = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return ResponseEntity.ok(new UserDTO(
                    loggedInUser.getId(),
                    loggedInUser.getUsername(),
                    loggedInUser.getFriends().stream().map(AppUser::getId).collect(Collectors.toSet())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Fetches the username of a user based on their ID.
    @GetMapping("/{id}/name")
    public ResponseEntity<Map<String, String>> getUserName(@PathVariable Long id) {
        Optional<AppUser> user = userService.getUserById(id);
        if (user.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("name", user.get().getUsername()); // Add the username to the response map
            return ResponseEntity.ok(response); // Return the response map with status 200
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if user not found
    }

    // Search for users allowing partial search
    @GetMapping("/searchUsersForChannel")
    public ResponseEntity<List<UserDTO>> searchUsersForChannel(@RequestParam String query, @RequestParam Long channelId) {
        List<UserDTO> users = userService.searchUsersForChannel(query, channelId);
        return ResponseEntity.ok(users);
    }



}
