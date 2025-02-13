package com.beta_discord_api.betadiscordapi.Dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;                // User ID
    private String username;        // Username
    private Set<Long> friendIds;    // List of Friend IDs

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Allows password only in requests
    private String password;

    // Constructor without password (for read-only operations)
    public UserDTO(Long id, String username, Set<Long> friendIds) {
        this.id = id;
        this.username = username;
        this.friendIds = friendIds;
    }
}


