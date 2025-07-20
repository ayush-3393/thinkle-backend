package com.thinkle_backend.controllers;

import com.thinkle_backend.dtos.requests.GameSessionRequestDto;
import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.dtos.responses.GameSessionResponseDto;
import com.thinkle_backend.services.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:3000")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/session")
    public ResponseEntity<BaseResponse<GameSessionResponseDto>> getOrCreateGameSession(
            @Valid @RequestBody GameSessionRequestDto gameSessionRequestDto
    ){
        GameSessionResponseDto gameSessionResponseDto =
                this.gameSessionService.getOrCreateGameSession(gameSessionRequestDto);

        return ResponseEntity.ok(BaseResponse.success(gameSessionResponseDto));
    }
}
