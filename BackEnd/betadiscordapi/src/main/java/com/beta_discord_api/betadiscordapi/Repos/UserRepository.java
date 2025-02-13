package com.beta_discord_api.betadiscordapi.Repos;

import com.beta_discord_api.betadiscordapi.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    // Find all active users (not deleted)
    @Query("SELECT u FROM AppUser u WHERE u.isDeleted = false")
    List<AppUser> findAllActive();

    // Find a user by ID, ensuring the user is not deleted
    @Query("SELECT u FROM AppUser u WHERE u.id = :id AND u.isDeleted = false")
    Optional<AppUser> findByIdAndNotDeleted(@Param("id") Long id);

    // Find a user by username
    Optional<AppUser> findByUsername(String username);

    @Query("SELECT DISTINCT u FROM AppUser u " +
            "LEFT JOIN FETCH u.friends f " +
            "LEFT JOIN FETCH f.friends ff " +
            "WHERE u.id = :id AND u.isDeleted = false")
    Optional<AppUser> findByIdWithNestedFriends(@Param("id") Long id);

    // Search user by username
    @Query("SELECT u FROM AppUser u WHERE u.username LIKE %:username% AND u.isDeleted = false")
    List<AppUser> searchByUsername(@Param("username") String username);

    @Query("SELECT u FROM AppUser u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) AND u.isDeleted = false")
    List<AppUser> findUsersByPartialUsername(@Param("query") String query);


}
