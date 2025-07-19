package com.thinkle_backend.controllers;

import com.thinkle_backend.dtos.requests.GetHintRequestDto;
import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.services.WordHintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hints")
public class WordHintController {

    private final WordHintService wordHintService;

    public WordHintController(WordHintService wordHintService) {
        this.wordHintService = wordHintService;
    }

    @GetMapping("/get")
    public ResponseEntity<BaseResponse<String>> getHintForHintType(
            @Valid @RequestBody GetHintRequestDto getHintRequestDto
    ){
        String hintForHintType =
                this.wordHintService.getHintForHintType(
                        getHintRequestDto.getHintType(), getHintRequestDto.getUserId()
                );

        return ResponseEntity.ok(BaseResponse.success(hintForHintType));
    }
}
