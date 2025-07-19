package com.thinkle_backend.dtos.responses;

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
}
