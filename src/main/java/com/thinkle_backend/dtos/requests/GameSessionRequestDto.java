package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GameSessionRequestDto {

    @NotNull(message = "User Id is required")
    private Long userId;
}
