package com.thinkle_backend.repositories;

import com.thinkle_backend.models.WordOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WordOfTheDayRepository extends JpaRepository<WordOfTheDay, Long> {
    Optional<WordOfTheDay> findByGeneratedAt(LocalDate date);
}
