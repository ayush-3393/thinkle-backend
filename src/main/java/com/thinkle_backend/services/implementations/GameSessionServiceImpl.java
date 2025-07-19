package com.thinkle_backend.services.implementations;

import com.thinkle_backend.dtos.requests.GameSessionRequestDto;
import com.thinkle_backend.dtos.responses.GameSessionResponseDto;
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
import java.util.Optional;


@Service
public class GameSessionServiceImpl implements GameSessionService {

    @Value("${thinkle.game.default.max-lives}")
    private Integer MAX_INITIAL_LIVES;

    private final GameSessionRepository gameSessionRepository;
    private final ThinkleUsersRepository thinkleUsersRepository;
    private final HintRepository hintRepository;
    private final WordHintRepository wordHintRepository;
    private final GuessRepository guessRepository;
    private final WordOfTheDayRepository wordOfTheDayRepository;
    private final WordOfTheDayService wordOfTheDayService;

    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  ThinkleUsersRepository thinkleUsersRepository,
                                  HintRepository hintRepository,
                                  WordHintRepository wordHintRepository,
                                  GuessRepository guessRepository,
                                  WordOfTheDayRepository wordOfTheDayRepository,
                                  WordOfTheDayService wordOfTheDayService) {
        this.gameSessionRepository = gameSessionRepository;
        this.thinkleUsersRepository = thinkleUsersRepository;
        this.hintRepository = hintRepository;
        this.wordHintRepository = wordHintRepository;
        this.guessRepository = guessRepository;
        this.wordOfTheDayRepository = wordOfTheDayRepository;
        this.wordOfTheDayService = wordOfTheDayService;
    }

    @Override
    @Transactional
    public GameSessionResponseDto createGameSession(GameSessionRequestDto gameSessionRequestDto) {

        // find user by user id
        Optional<ThinkleUsers> thinkleUsersOptional =
                this.thinkleUsersRepository.findById(gameSessionRequestDto.getUserId());

        if(thinkleUsersOptional.isEmpty()){
            throw new UserNotFoundException("User not found for the id: " + gameSessionRequestDto.getUserId());
        }

        WordOfTheDay wordOfTheDay = this.wordOfTheDayService.generateWordOfTheDay();

        GameSession gameSession = new GameSession();
        gameSession.setGameDate(LocalDate.now());
        gameSession.setRemainingLives(MAX_INITIAL_LIVES);
        gameSession.setStatus(GameStatus.IN_PROGRESS);
        gameSession.setUser(thinkleUsersOptional.get());
        gameSession.setGuesses(Collections.emptyList());
        gameSession.setWordOfTheDay(wordOfTheDay);
        gameSession.setHints(Collections.emptyList());
        this.gameSessionRepository.save(gameSession);

        GameSessionResponseDto responseDto = new GameSessionResponseDto();
        responseDto.setGameStatus(gameSession.getStatus());
        responseDto.setGuesses(gameSession.getGuesses());
        responseDto.setRemainingLives(gameSession.getRemainingLives());
        HintsInfoForSession hintsInfoForSession = new HintsInfoForSession();
        hintsInfoForSession.setNumberOfHintsUsed(0); // initial
        hintsInfoForSession.setHintDetails(Collections.emptyList());
        responseDto.setHintsInfo(hintsInfoForSession);

        return responseDto;
    }

    @Override
    public GameSessionResponseDto getGameSession(GameSessionRequestDto gameSessionRequestDto) {
        // find user by user id
        Optional<ThinkleUsers> thinkleUsersOptional =
                this.thinkleUsersRepository.findById(gameSessionRequestDto.getUserId());

        if(thinkleUsersOptional.isEmpty()){
            throw new UserNotFoundException("User not found for the id: " + gameSessionRequestDto.getUserId());
        }

        // find game session by user id for today
        Optional<GameSession> gameSessionOptional =
                this.gameSessionRepository
                        .findByUserIdAndGameDate(
                                gameSessionRequestDto.getUserId(), LocalDate.now()
                        );

        if(gameSessionOptional.isEmpty()){
            throw new GameSessionNotFoundException("Game session was not found!");
        }

        GameSession gameSession = gameSessionOptional.get();

        GameSessionResponseDto responseDto = new GameSessionResponseDto();
        responseDto.setGameStatus(gameSession.getStatus());
        responseDto.setGuesses(gameSession.getGuesses());
        responseDto.setRemainingLives(gameSession.getRemainingLives());

        HintsInfoForSession hintsInfoForSession = new HintsInfoForSession();

        List<Hint> hints =
                this.hintRepository
                        .findByGameSession_User_IdAndGameSession_GameDate(
                                gameSessionRequestDto.getUserId(), LocalDate.now()
                        );

        List<HintDetails> hintDetails = new ArrayList<>();

        for(Hint hint : hints){
            HintDetails tempHintDetails = new HintDetails();
            tempHintDetails.setHintText(hint.getWordHint().getText());
            tempHintDetails.setHintType(hint.getWordHint().getHintType().getHintType());
            hintDetails.add(tempHintDetails);
        }

        hintsInfoForSession.setNumberOfHintsUsed(hints.size());
        hintsInfoForSession.setHintDetails(hintDetails);

        responseDto.setHintsInfo(hintsInfoForSession);

        return responseDto;
    }

    @Override
    public GameSessionResponseDto getOrCreateGameSession(GameSessionRequestDto gameSessionRequestDto) {
        Optional<GameSession> gameSessionOptional =
                this.gameSessionRepository.findByUserIdAndGameDate(
                        gameSessionRequestDto.getUserId(), LocalDate.now()
                );
        if(gameSessionOptional.isPresent()){
            return getGameSession(gameSessionRequestDto);
        }
        return createGameSession(gameSessionRequestDto);
    }
}
