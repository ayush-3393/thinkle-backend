package com.thinkle_backend.services;

import com.thinkle_backend.dtos.requests.GameSessionRequestDto;
import com.thinkle_backend.dtos.responses.GameSessionResponseDto;

public interface GameSessionService {
    GameSessionResponseDto createGameSession(GameSessionRequestDto gameSessionRequestDto);
    GameSessionResponseDto getGameSession(GameSessionRequestDto gameSessionRequestDto);
    GameSessionResponseDto getOrCreateGameSession(GameSessionRequestDto gameSessionRequestDto);
}
