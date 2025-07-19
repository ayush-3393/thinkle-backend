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
import java.util.Optional;

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

        Optional<Boolean> wordExistsCheckOptional =
                this.wordOfTheDayRepository.existsBySolutionWordIgnoreCase(wordOfTheDay.getSolutionWord());

        if(wordExistsCheckOptional.isEmpty() || !wordExistsCheckOptional.get()){
            throw new WordDoesNotExistsException("Word of the day does not exist");
        }

        List<HintType> hintTypes = this.hintTypeRepository.findAll();

        if(hintTypes.isEmpty()){
            throw new HintTypeDoesNotExistsException("No hint types were found");
        }

        for(HintType hintType : hintTypes){

            Optional<WordHint> byWordOfTheDayAndHintType =
                    this.wordHintRepository.findByWordOfTheDayAndHintType(wordOfTheDay, hintType);

            if(byWordOfTheDayAndHintType.isPresent()){
                throw new HintAlreadyExists(
                        "Hint for the type " + hintType.getHintType() + " already exists for the word of the day"
                );
            }

            String generatedHintText =
                    this.hintGenerator.generateHint(wordOfTheDay.getSolutionWord(), hintType.getHintType());

            WordHint wordHint = new WordHint();
            wordHint.setHintType(hintType);
            wordHint.setWordOfTheDay(wordOfTheDay);
            wordHint.setText(generatedHintText);

            WordHint savedWordHint = this.wordHintRepository.save(wordHint);
        }
    }

    @Override
    @Transactional
    public String getHintForHintType(String hintType, Long userId) {

        // 1. Fetch game session
        Optional<GameSession> gameSessionOptional =
                this.gameSessionRepository.findByUserIdAndGameDate(userId, LocalDate.now());

        if(gameSessionOptional.isEmpty()){
            throw new GameSessionNotFoundException("Game session was not found");
        }

        GameSession gameSession = gameSessionOptional.get();

        // 2. Validate remaining lives
        if(gameSession.getRemainingLives() < MIN_REMAINING_LIVES_TO_USE_HINT){
            throw new CanNotUseHintException("Not enough lives remaining!");
        }

        // 3. Validate daily hint usage limit
        Long countOfHintsUsedByTheUserToday =
                this.hintRepository.countByGameSession_User_IdAndGameSession_GameDate(userId, LocalDate.now());

        if(countOfHintsUsedByTheUserToday >= MAX_LIMIT_OF_HINTS_USAGE_PER_USER_PER_DAY){
            throw new CanNotUseHintException("Maximum number of hints (2) used for today!");
        }


        // 4. Fetch word of the day
        Optional<WordOfTheDay> wordOfTheDayOptional =
                this.wordOfTheDayRepository.findByGeneratedAt(LocalDate.now());

        if(wordOfTheDayOptional.isEmpty()){
            throw new WordDoesNotExistsException("Word of the day does not exist");
        }

        WordOfTheDay wordOfTheDay = wordOfTheDayOptional.get();

        // 5. Fetch hint type
        Optional<HintType> hintTypeOptional = this.hintTypeRepository.findByHintTypeIgnoreCase(hintType);

        if(hintTypeOptional.isEmpty()){
            throw new HintTypeDoesNotExistsException("Requested Hint Type does not exist");
        }

        HintType type = hintTypeOptional.get();

        // 6. Check if this hint type is already used in this session
        if (this.hintRepository.existsByGameSession_IdAndWordHint_HintType_Id(gameSession.getId(), type.getId())) {
            throw new CanNotUseHintException("Hint of this type already used in this session!");
        }

        // 7. Fetch WordHint
        Optional<WordHint> optionalWordHint =
                this.wordHintRepository.findByWordOfTheDayAndHintType(wordOfTheDay, type);

        if(optionalWordHint.isEmpty()){
            throw new HintDoesNotExistsException("Hint for this type and today's word does not exist");
        }

        WordHint wordHint = optionalWordHint.get();

        // 8. Update game session lives
        Integer remainingLives = gameSession.getRemainingLives() - LIFE_COST_PER_HINT;

        gameSession.setRemainingLives(remainingLives);
        gameSession.setUpdatedAt(LocalDateTime.now());

        this.gameSessionRepository.save(gameSession);

        // 9. Save hint usage
        Hint hint = new Hint();
        hint.setWordHint(wordHint);
        hint.setUsedAt(LocalDateTime.now());
        hint.setGameSession(gameSession);
        this.hintRepository.save(hint);

        // 10. Return hint text
        return wordHint.getText();
    }
}
