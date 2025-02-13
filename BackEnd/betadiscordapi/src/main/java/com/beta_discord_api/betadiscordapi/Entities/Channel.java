package com.beta_discord_api.betadiscordapi.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
//import javax.persistence;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToMany
    @JoinTable(
            name = "channel_users",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<AppUser> users = new HashSet<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private Set<ChannelMessage> messages = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_roles", joinColumns = @JoinColumn(name = "channel_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "role")
    @Builder.Default
    private Map<AppUser, Role> roles = new HashMap<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}
