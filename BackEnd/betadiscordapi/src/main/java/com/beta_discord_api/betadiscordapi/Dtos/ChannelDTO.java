package com.beta_discord_api.betadiscordapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {
    private Long id;
    private String name;
    private Long ownerId;
    private Set<Long> userIds;
}
