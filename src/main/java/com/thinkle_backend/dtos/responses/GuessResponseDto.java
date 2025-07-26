package com.thinkle_backend.dtos.responses;

import com.thinkle_backend.models.enums.GameStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class GuessResponseDto {
    private String guessedWord;
    private List<Integer> correctPositions;
    private List<Integer> missedPositions;
    private String aiResponse;
    private Integer remainingLives;
    private GameStatus gameStatus;
}
