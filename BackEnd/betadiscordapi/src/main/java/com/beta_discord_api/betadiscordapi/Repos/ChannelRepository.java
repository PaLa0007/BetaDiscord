package com.beta_discord_api.betadiscordapi.Repos;

import com.beta_discord_api.betadiscordapi.Entities.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("SELECT c FROM Channel c WHERE c.isDeleted = false")
    List<Channel> findAllActive();

    @Query("SELECT c FROM Channel c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Channel> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT c.name FROM Channel c WHERE c.id = :id AND c.isDeleted = false")
    Optional<String> findChannelNameById(@Param("id") Long id);
}
