package com.beta_discord_api.betadiscordapi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ChannelUsersDTO {
    private List<UserDTO> owners;
    private List<UserDTO> admins;
    private List<UserDTO> guests;
}
