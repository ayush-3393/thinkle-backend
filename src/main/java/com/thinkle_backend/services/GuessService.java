package com.thinkle_backend.services;

import com.thinkle_backend.dtos.requests.GuessRequestDto;
import com.thinkle_backend.dtos.responses.GuessResponseDto;

public interface GuessService {
    GuessResponseDto processGuess(GuessRequestDto guessRequestDto);
}
