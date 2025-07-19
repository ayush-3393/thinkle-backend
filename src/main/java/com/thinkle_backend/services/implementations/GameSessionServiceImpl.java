package com.thinkle_backend.services.implementations;

import com.thinkle_backend.dtos.requests.GameSessionRequestDto;
import com.thinkle_backend.dtos.responses.GameSessionResponseDto;
import com.thinkle_backend.dtos.responses.GuessResponseDto;
import com.thinkle_backend.dtos.responses.HintDetails;
import com.thinkle_backend.dtos.responses.HintsInfoForSession;
import com.thinkle_backend.exceptions.GameSessionNotFoundException;
import com.thinkle_backend.exceptions.UserNotFoundException;
import com.thinkle_backend.models.*;
import com.thinkle_backend.models.enums.GameStatus;
import com.thinkle_backend.repositories.*;
import com.thinkle_backend.services.GameSessionService;
import com.thinkle_backend.services.WordOfTheDayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class GameSessionServiceImpl implements GameSessionService {

    @Value("${thinkle.game.default.max-lives}")
    private Integer MAX_INITIAL_LIVES;

    private final GameSessionRepository gameSessionRepository;
    private final ThinkleUsersRepository thinkleUsersRepository;
    private final HintRepository hintRepository;
    private final WordOfTheDayService wordOfTheDayService;

    public GameSessionServiceImpl(
            GameSessionRepository gameSessionRepository,
            ThinkleUsersRepository thinkleUsersRepository,
            HintRepository hintRepository,
            WordOfTheDayService wordOfTheDayService
    ) {
        this.gameSessionRepository = gameSessionRepository;
        this.thinkleUsersRepository = thinkleUsersRepository;
        this.hintRepository = hintRepository;
        this.wordOfTheDayService = wordOfTheDayService;
    }

    @Override
    @Transactional
    public GameSessionResponseDto createGameSession(GameSessionRequestDto requestDto) {
        ThinkleUsers user = getUserOrThrow(requestDto.getUserId());
        WordOfTheDay word = wordOfTheDayService.generateWordOfTheDay();

        GameSession session = new GameSession();
        session.setUser(user);
        session.setGameDate(LocalDate.now());
        session.setRemainingLives(MAX_INITIAL_LIVES);
        session.setStatus(GameStatus.IN_PROGRESS);
        session.setGuesses(Collections.emptyList());
        session.setWordOfTheDay(word);
        session.setHints(Collections.emptyList());

        gameSessionRepository.save(session);
        return buildEmptyGameSessionResponse(session);
    }

    @Override
    public GameSessionResponseDto getGameSession(GameSessionRequestDto requestDto) {
        ThinkleUsers user = getUserOrThrow(requestDto.getUserId());
        GameSession session = getSessionOrThrow(requestDto.getUserId(), LocalDate.now());
        return buildFullGameSessionResponse(session);
    }

    @Override
    public GameSessionResponseDto getOrCreateGameSession(GameSessionRequestDto requestDto) {
        return gameSessionRepository.findByUserIdAndGameDate(requestDto.getUserId(), LocalDate.now())
                .map(session -> getGameSession(requestDto))
                .orElseGet(() -> createGameSession(requestDto));
    }

    private ThinkleUsers getUserOrThrow(Long userId) {
        return thinkleUsersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));
    }

    private GameSession getSessionOrThrow(Long userId, LocalDate date) {
        return gameSessionRepository.findByUserIdAndGameDate(userId, date)
                .orElseThrow(() -> new GameSessionNotFoundException("Game session was not found!"));
    }

    private GameSessionResponseDto buildEmptyGameSessionResponse(GameSession session) {
        GameSessionResponseDto response = new GameSessionResponseDto();
        response.setGameStatus(session.getStatus());
        response.setRemainingLives(session.getRemainingLives());
        response.setGuesses(Collections.emptyList());

        HintsInfoForSession hints = new HintsInfoForSession();
        hints.setNumberOfHintsUsed(0);
        hints.setHintDetails(Collections.emptyList());
        response.setHintsInfo(hints);

        return response;
    }

    private GameSessionResponseDto buildFullGameSessionResponse(GameSession session) {
        GameSessionResponseDto response = new GameSessionResponseDto();
        response.setGameStatus(session.getStatus());
        response.setRemainingLives(session.getRemainingLives());
        response.setGuesses(mapGuesses(session.getGuesses()));
        response.setHintsInfo(buildHintsInfo(session.getUser().getId(), session.getGameDate()));
        return response;
    }

    private List<GuessResponseDto> mapGuesses(List<Guess> guesses) {
        List<GuessResponseDto> result = new ArrayList<>();
        for (Guess guess : guesses) {
            GuessResponseDto dto = new GuessResponseDto();
            dto.setGuessedWord(guess.getGuessedWord());
            dto.setCorrectPositions(guess.getCorrectPositionIndices());
            dto.setMissedPositions(guess.getMissedPositionIndices());
            dto.setAiResponse(null);
            result.add(dto);
        }
        return result;
    }

    private HintsInfoForSession buildHintsInfo(Long userId, LocalDate date) {
        List<Hint> hints = hintRepository.findByGameSession_User_IdAndGameSession_GameDate(userId, date);
        List<HintDetails> details = new ArrayList<>();
        for (Hint hint : hints) {
            HintDetails hd = new HintDetails();
            hd.setHintText(hint.getWordHint().getText());
            hd.setHintType(hint.getWordHint().getHintType().getHintType());
            details.add(hd);
        }

        HintsInfoForSession info = new HintsInfoForSession();
        info.setNumberOfHintsUsed(details.size());
        info.setHintDetails(details);
        return info;
    }
}

