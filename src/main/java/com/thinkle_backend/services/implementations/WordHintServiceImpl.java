package com.thinkle_backend.services.implementations;

import com.thinkle_backend.ai.features.hintGenerator.HintGenerator;
import com.thinkle_backend.exceptions.*;
import com.thinkle_backend.models.*;
import com.thinkle_backend.repositories.*;
import com.thinkle_backend.services.WordHintService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WordHintServiceImpl implements WordHintService {

    @Value("${thinkle.game.default.min-life-to-use-hint}")
    private Integer MIN_REMAINING_LIVES_TO_USE_HINT;

    @Value("${thinkle.game.default.max-hints}")
    private Integer MAX_LIMIT_OF_HINTS_USAGE_PER_USER_PER_DAY;

    @Value("${thinkle.game.default.life-cost-per-hint}")
    private Integer LIFE_COST_PER_HINT;

    private final HintTypeRepository hintTypeRepository;
    private final WordOfTheDayRepository wordOfTheDayRepository;
    private final HintGenerator hintGenerator;
    private final WordHintRepository wordHintRepository;
    private final GameSessionRepository gameSessionRepository;
    private final HintRepository hintRepository;

    public WordHintServiceImpl(HintTypeRepository hintTypeRepository,
                               WordOfTheDayRepository wordOfTheDayRepository,
                               HintGenerator hintGenerator,
                               WordHintRepository wordHintRepository,
                               GameSessionRepository gameSessionRepository,
                               HintRepository hintRepository) {
        this.hintTypeRepository = hintTypeRepository;
        this.wordOfTheDayRepository = wordOfTheDayRepository;
        this.hintGenerator = hintGenerator;
        this.wordHintRepository = wordHintRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.hintRepository = hintRepository;
    }

    @Override
    public void createHintsForWordOfTheDay(WordOfTheDay wordOfTheDay) {
        validateWordOfTheDayExists(wordOfTheDay);

        List<HintType> hintTypes = fetchAllHintTypes();

        for (HintType hintType : hintTypes) {
            ensureHintNotExists(wordOfTheDay, hintType);

            String generatedHintText = hintGenerator.generateHint(
                    wordOfTheDay.getSolutionWord(), hintType.getHintType()
            );

            WordHint wordHint = new WordHint();
            wordHint.setHintType(hintType);
            wordHint.setWordOfTheDay(wordOfTheDay);
            wordHint.setText(generatedHintText);
            wordHintRepository.save(wordHint);
        }
    }

    @Override
    @Transactional
    public String getHintForHintType(String hintTypeStr, Long userId) {
        GameSession session = getValidGameSession(userId);

        validateLives(session);
        validateHintUsageLimit(userId);
        WordOfTheDay word = getTodayWordOfTheDay();
        HintType hintType = getHintType(hintTypeStr);
        ensureHintNotUsed(session, hintType);

        WordHint wordHint = getWordHint(word, hintType);
        deductLife(session);
        saveHintUsage(session, wordHint);

        return wordHint.getText();
    }

    // ---------------------- Private Helpers ---------------------------- //

    private void validateWordOfTheDayExists(WordOfTheDay wordOfTheDay) {
        if (!wordOfTheDayRepository
                .existsBySolutionWordIgnoreCase(wordOfTheDay.getSolutionWord())
                .orElse(false)) {
            throw new WordDoesNotExistsException("Word of the day does not exist");
        }
    }

    private List<HintType> fetchAllHintTypes() {
        List<HintType> hintTypes = hintTypeRepository.findAll();
        if (hintTypes.isEmpty()) {
            throw new HintTypeDoesNotExistsException("No hint types were found");
        }
        return hintTypes;
    }

    private void ensureHintNotExists(WordOfTheDay word, HintType type) {
        if (wordHintRepository.findByWordOfTheDayAndHintType(word, type).isPresent()) {
            throw new HintAlreadyExists(
                    "Hint for type " + type.getHintType() + " already exists for this word"
            );
        }
    }

    private GameSession getValidGameSession(Long userId) {
        return gameSessionRepository
                .findByUserIdAndGameDate(userId, LocalDate.now())
                .orElseThrow(() -> new GameSessionNotFoundException("Game session was not found"));
    }

    private void validateLives(GameSession session) {
        if (session.getRemainingLives() < MIN_REMAINING_LIVES_TO_USE_HINT) {
            throw new CanNotUseHintException("Not enough lives remaining!");
        }
    }

    private void validateHintUsageLimit(Long userId) {
        long hintsUsed = hintRepository
                .countByGameSession_User_IdAndGameSession_GameDate(userId, LocalDate.now());
        if (hintsUsed >= MAX_LIMIT_OF_HINTS_USAGE_PER_USER_PER_DAY) {
            throw new CanNotUseHintException("Maximum number of hints used for today!");
        }
    }

    private WordOfTheDay getTodayWordOfTheDay() {
        return wordOfTheDayRepository.findByGeneratedAt(LocalDate.now())
                .orElseThrow(() -> new WordDoesNotExistsException("Today's word not found"));
    }

    private HintType getHintType(String hintTypeStr) {
        return hintTypeRepository.findByHintTypeIgnoreCase(hintTypeStr)
                .orElseThrow(() -> new HintTypeDoesNotExistsException("Hint type does not exist"));
    }

    private void ensureHintNotUsed(GameSession session, HintType type) {
        if (hintRepository.existsByGameSession_IdAndWordHint_HintType_Id(session.getId(), type.getId())) {
            throw new CanNotUseHintException("Hint of this type already used in this session!");
        }
    }

    private WordHint getWordHint(WordOfTheDay word, HintType type) {
        return wordHintRepository.findByWordOfTheDayAndHintType(word, type)
                .orElseThrow(() -> new HintDoesNotExistsException("Hint for this type and word not found"));
    }

    private void deductLife(GameSession session) {
        session.setRemainingLives(session.getRemainingLives() - LIFE_COST_PER_HINT);
        session.setUpdatedAt(LocalDateTime.now());
        gameSessionRepository.save(session);
    }

    private void saveHintUsage(GameSession session, WordHint wordHint) {
        Hint hint = new Hint();
        hint.setGameSession(session);
        hint.setWordHint(wordHint);
        hint.setUsedAt(LocalDateTime.now());
        hintRepository.save(hint);
    }
}

