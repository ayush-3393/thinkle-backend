package com.thinkle_backend.services.implementations;

import com.thinkle_backend.ai.features.wordGenerator.WordOfTheDayGenerator;
import com.thinkle_backend.exceptions.InvalidWordException;
import com.thinkle_backend.exceptions.WordAlreadyExistsException;
import com.thinkle_backend.models.WordOfTheDay;
import com.thinkle_backend.repositories.WordOfTheDayRepository;
import com.thinkle_backend.services.WordHintService;
import com.thinkle_backend.services.WordOfTheDayService;
import com.thinkle_backend.utils.WordUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WordOfTheDayServiceImpl implements WordOfTheDayService {

    @Value("${thinkle.game.default.word-length:5}")
    private Integer MAX_WORD_LENGTH;

    private final WordOfTheDayRepository wordOfTheDayRepository;
    private final WordOfTheDayGenerator wordOfTheDayGenerator;
    private final WordHintService wordHintService;

    public WordOfTheDayServiceImpl(WordOfTheDayRepository wordOfTheDayRepository,
                                   WordOfTheDayGenerator wordOfTheDayGenerator,
                                   WordHintService wordHintService) {
        this.wordOfTheDayRepository = wordOfTheDayRepository;
        this.wordOfTheDayGenerator = wordOfTheDayGenerator;
        this.wordHintService = wordHintService;
    }

    @Override
    @Transactional
    public WordOfTheDay generateWordOfTheDay() {
        Optional<WordOfTheDay> existing = wordOfTheDayRepository.findByGeneratedAt(LocalDate.now());
        if (existing.isPresent()) return existing.get();

        String word = wordOfTheDayGenerator.generateWordOfTheDay();
        if (!WordUtils.isValidWord(word, MAX_WORD_LENGTH)) {
            throw new InvalidWordException("Invalid word: " + word);
        }

        WordOfTheDay entity = new WordOfTheDay();
        entity.setSolutionWord(word.trim().toUpperCase());
        entity.setGeneratedAt(LocalDate.now());

        try {
            WordOfTheDay saved = wordOfTheDayRepository.save(entity);
            wordHintService.createHintsForWordOfTheDay(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            // Another thread inserted it first, fetch it instead
            return wordOfTheDayRepository.findByGeneratedAt(LocalDate.now())
                    .orElseThrow(() -> new RuntimeException("Race condition occurred, and fallback failed."));
        }
    }

}
