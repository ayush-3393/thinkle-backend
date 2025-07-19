package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GuessRequestDto {

    @NotNull(message = "User Id is required")
    private Long userId;

    @NotBlank(message = "Guessed word is required")
    private String guessedWord;
}
