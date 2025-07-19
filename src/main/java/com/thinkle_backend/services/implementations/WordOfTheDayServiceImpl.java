package com.thinkle_backend.services.implementations;

import com.thinkle_backend.ai.features.wordGenerator.WordOfTheDayGenerator;
import com.thinkle_backend.exceptions.InvalidWordException;
import com.thinkle_backend.exceptions.WordAlreadyExistsException;
import com.thinkle_backend.models.WordOfTheDay;
import com.thinkle_backend.repositories.WordOfTheDayRepository;
import com.thinkle_backend.services.WordHintService;
import com.thinkle_backend.services.WordOfTheDayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
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

        Optional<WordOfTheDay> wordOfTheDayOptional =
                this.wordOfTheDayRepository.findByGeneratedAt(LocalDate.now());

        if(wordOfTheDayOptional.isPresent()){
            throw new WordAlreadyExistsException("Word Already Generated for today!");
        }

        String generateWordOfTheDay = this.wordOfTheDayGenerator.generateWordOfTheDay();

        if(!isValidWordOfTheDay(generateWordOfTheDay)){
            throw new InvalidWordException("Invalid Word Generated!: " + generateWordOfTheDay);
        }

        WordOfTheDay wordOfTheDay = new WordOfTheDay();
        wordOfTheDay.setSolutionWord(generateWordOfTheDay.trim().toUpperCase());
        wordOfTheDay.setGeneratedAt(LocalDate.now());

        WordOfTheDay savedWordOfTheDay = this.wordOfTheDayRepository.save(wordOfTheDay);

        // generate the hints for the word
        this.wordHintService.createHintsForWordOfTheDay(savedWordOfTheDay);

        return savedWordOfTheDay;
    }

    private boolean isValidWordOfTheDay(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        if(word.length() > MAX_WORD_LENGTH){
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

}
