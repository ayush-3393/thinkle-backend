package com.thinkle_backend.repositories;

import com.thinkle_backend.models.ThinkleUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThinkleUsersRepository extends JpaRepository<ThinkleUsers, Long> {
    Optional<ThinkleUsers> findByEmail(String email);
    Optional<ThinkleUsers> findByUsername(String username); // Add this for better validation
    boolean existsByEmail(String email); // Optional: for efficiency
    boolean existsByUsername(String username); // Optional: for efficiency
}
