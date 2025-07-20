package com.thinkle_backend.controllers;

import com.thinkle_backend.dtos.requests.GetHintRequestDto;
import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.dtos.responses.GetHintResponseDto;
import com.thinkle_backend.services.WordHintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hints")
@CrossOrigin(origins = "http://localhost:3000")
public class WordHintController {

    private final WordHintService wordHintService;

    public WordHintController(WordHintService wordHintService) {
        this.wordHintService = wordHintService;
    }

    @PostMapping("/get")
    public ResponseEntity<BaseResponse<GetHintResponseDto>> getHintForHintType(
            @Valid @RequestBody GetHintRequestDto getHintRequestDto
    ){
        String hintForHintType =
                this.wordHintService.getHintForHintType(
                        getHintRequestDto.getHintType(), getHintRequestDto.getUserId()
                );
        GetHintResponseDto responseDto = new GetHintResponseDto();
        responseDto.setHintText(hintForHintType);
        return ResponseEntity.ok(BaseResponse.success(responseDto));
    }
}
