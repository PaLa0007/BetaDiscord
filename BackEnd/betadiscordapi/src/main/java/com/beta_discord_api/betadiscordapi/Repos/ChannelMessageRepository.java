package com.beta_discord_api.betadiscordapi.Repos;

import com.beta_discord_api.betadiscordapi.Entities.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
    List<ChannelMessage> findByChannelId(Long channelId);
}
