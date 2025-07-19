package com.thinkle_backend.services.implementations;

import com.thinkle_backend.ai.features.replyToGuesses.ReplyToGuessesGenerator;
import com.thinkle_backend.dtos.requests.GuessRequestDto;
import com.thinkle_backend.dtos.responses.GuessResponseDto;
import com.thinkle_backend.exceptions.CanNotSubmitGuessException;
import com.thinkle_backend.exceptions.GameSessionNotFoundException;
import com.thinkle_backend.exceptions.InvalidWordException;
import com.thinkle_backend.exceptions.WordDoesNotExistsException;
import com.thinkle_backend.models.GameSession;
import com.thinkle_backend.models.Guess;
import com.thinkle_backend.models.WordOfTheDay;
import com.thinkle_backend.models.enums.GameStatus;
import com.thinkle_backend.repositories.GameSessionRepository;
import com.thinkle_backend.repositories.GuessRepository;
import com.thinkle_backend.repositories.HintRepository;
import com.thinkle_backend.repositories.WordOfTheDayRepository;
import com.thinkle_backend.services.GuessService;
import com.thinkle_backend.utils.GuessUtils;
import com.thinkle_backend.utils.WordUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GuessServiceImpl implements GuessService {

    @Value("${thinkle.game.default.word-length:5}")
    private Integer MAX_WORD_LENGTH;

    @Value("${thinkle.game.default.life-cost-per-wrong-guess}")
    private Integer LIFE_COST_PER_WRONG_GUESS;

    @Value("${thinkle.game.default.max-guess-count}")
    private Integer MAX_GUESS_COUNT;

    private final GuessRepository guessRepository;
    private final WordOfTheDayRepository wordOfTheDayRepository;
    private final GameSessionRepository gameSessionRepository;
    private final ReplyToGuessesGenerator replyToGuessesGenerator;
    private final HintRepository hintRepository;

    public GuessServiceImpl(GuessRepository guessRepository,
                            WordOfTheDayRepository wordOfTheDayRepository,
                            GameSessionRepository gameSessionRepository,
                            ReplyToGuessesGenerator replyToGuessesGenerator,
                            HintRepository hintRepository) {
        this.guessRepository = guessRepository;
        this.wordOfTheDayRepository = wordOfTheDayRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.replyToGuessesGenerator = replyToGuessesGenerator;
        this.hintRepository = hintRepository;
    }

    @Override
    @Transactional
    public GuessResponseDto processGuess(GuessRequestDto guessRequestDto) {
        validateGuessWord(guessRequestDto.getGuessedWord());

        GameSession gameSession = getActiveGameSession(guessRequestDto.getUserId());

        WordOfTheDay wordOfTheDay = getTodayWordOfTheDay();

        GuessUtils guessUtils = new GuessUtils(wordOfTheDay.getSolutionWord(), guessRequestDto.getGuessedWord());

        Guess savedGuess = saveGuess(guessRequestDto, gameSession, guessUtils);

        updateGameStatus(gameSession, guessRequestDto.getGuessedWord(), guessUtils);

        Long hintCount = getHintCount(guessRequestDto.getUserId());

        return buildResponseDto(savedGuess, guessUtils, gameSession, wordOfTheDay.getSolutionWord(), hintCount);
    }

    private void validateGuessWord(String guessedWord) {
        if (!WordUtils.isValidWord(guessedWord, MAX_WORD_LENGTH)) {
            throw new InvalidWordException("Invalid Guess Word: " + guessedWord + " with max length " + MAX_WORD_LENGTH);
        }
    }

    private GameSession getActiveGameSession(Long userId) {
        GameSession gameSession = gameSessionRepository
                .findByUserIdAndGameDate(userId, LocalDate.now())
                .orElseThrow(() -> new GameSessionNotFoundException(
                        "Game session not found for userId: " + userId));

        if (gameSession.getStatus() == GameStatus.WON) {
            throw new CanNotSubmitGuessException("Game already won. No more guesses allowed.");
        }
        if (gameSession.getStatus() == GameStatus.LOST) {
            throw new CanNotSubmitGuessException("Game over. Better luck next time!");
        }

        return gameSession;
    }

    private WordOfTheDay getTodayWordOfTheDay() {
        return wordOfTheDayRepository.findByGeneratedAt(LocalDate.now())
                .orElseThrow(() -> new WordDoesNotExistsException("Word of the day not found!"));
    }

    private Guess saveGuess(GuessRequestDto dto, GameSession gameSession, GuessUtils guessUtils) {
        Guess guess = new Guess();
        guess.setGuessedWord(dto.getGuessedWord());
        guess.setTimestamp(LocalDateTime.now());
        guess.setGameSession(gameSession);
        guess.setCorrectPositionIndices(guessUtils.getCorrectPositionsOfGuessedWord());
        guess.setMissedPositionIndices(guessUtils.getMissedPositionsOfGuessedWord());

        Guess savedGuess = guessRepository.save(guess);
        gameSession.getGuesses().add(savedGuess);

        return savedGuess;
    }

    private void updateGameStatus(GameSession gameSession, String guessedWord, GuessUtils guessUtils) {
        boolean isCorrect = guessUtils.getCorrectPositionsOfGuessedWord().size() == guessedWord.length();

        if (isCorrect) {
            gameSession.setStatus(GameStatus.WON);
        } else {
            gameSession.setRemainingLives(gameSession.getRemainingLives() - LIFE_COST_PER_WRONG_GUESS);
            if (gameSession.getRemainingLives() <= 0 || gameSession.getGuesses().size() >= MAX_GUESS_COUNT) {
                gameSession.setStatus(GameStatus.LOST);
            }
        }

        gameSessionRepository.save(gameSession);
    }

    private Long getHintCount(Long userId) {
        return hintRepository.countByGameSession_User_IdAndGameSession_GameDate(userId, LocalDate.now());
    }

    private GuessResponseDto buildResponseDto(Guess guess, GuessUtils utils, GameSession gameSession, String solution, Long hintCount) {
        String reply = replyToGuessesGenerator.generateReplyToTheGuessedWord(
                guess.getGuessedWord(), solution, gameSession.getStatus(), gameSession.getRemainingLives(), hintCount
        );

        GuessResponseDto dto = new GuessResponseDto();
        dto.setCorrectPositions(utils.getCorrectPositionsOfGuessedWord());
        dto.setMissedPositions(utils.getMissedPositionsOfGuessedWord());
        dto.setAiResponse(reply);
        dto.setGuessedWord(guess.getGuessedWord());
        return dto;
    }


}
