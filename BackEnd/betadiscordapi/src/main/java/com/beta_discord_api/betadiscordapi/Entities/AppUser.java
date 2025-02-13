package com.beta_discord_api.betadiscordapi.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder // Generates the builder pattern
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @ToString.Exclude // Prevents recursion in toString
    @EqualsAndHashCode.Exclude // Prevents recursion in equals and hashCode
    @JsonIgnore // Prevents infinite recursion in serialization
    private Set<AppUser> friends = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude // Prevents recursion in toString
    @EqualsAndHashCode.Exclude // Prevents recursion in equals and hashCode
    @JsonIgnore // Prevents infinite recursion in serialization
    private Set<Channel> channels = new HashSet<>();

    @Column(nullable = false)
    private boolean isDeleted = false;
}
