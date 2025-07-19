package com.thinkle_backend.repositories;

import com.thinkle_backend.models.HintType;
import com.thinkle_backend.models.WordHint;
import com.thinkle_backend.models.WordOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordHintRepository extends JpaRepository<WordHint, Long> {
    Optional<WordHint> findByWordOfTheDayAndHintType(WordOfTheDay wordOfTheDay, HintType hintType);
}
