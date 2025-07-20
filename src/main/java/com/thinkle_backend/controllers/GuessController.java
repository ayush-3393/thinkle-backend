package com.thinkle_backend.controllers;

import com.thinkle_backend.dtos.requests.GuessRequestDto;
import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.dtos.responses.GuessResponseDto;
import com.thinkle_backend.services.GuessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guess")
@CrossOrigin(origins = "http://localhost:3000")
public class GuessController {

    private final GuessService guessService;

    public GuessController(GuessService guessService) {
        this.guessService = guessService;
    }

    @PostMapping("/submit")
    public ResponseEntity<BaseResponse<GuessResponseDto>> processGuess(
            @Valid @RequestBody GuessRequestDto guessRequestDto
    ){
        GuessResponseDto responseDto = this.guessService.processGuess(guessRequestDto);
        return ResponseEntity.ok(BaseResponse.success(responseDto));
    }
}
