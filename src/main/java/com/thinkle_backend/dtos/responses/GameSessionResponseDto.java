package com.thinkle_backend.dtos.responses;

import com.thinkle_backend.models.Guess;
import com.thinkle_backend.models.enums.GameStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class GameSessionResponseDto {
    private Integer remainingLives;
    private GameStatus gameStatus;
    private HintsInfoForSession hintsInfo;
    private List<GuessResponseDto> guesses;
}
