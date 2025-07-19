package com.thinkle_backend.repositories;

import com.thinkle_backend.models.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findByUserIdAndGameDate(Long userId, LocalDate gameDate);
}
