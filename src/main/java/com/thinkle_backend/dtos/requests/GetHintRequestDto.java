package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GetHintRequestDto {

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[^ ]+$", message = "Type must not contain spaces")
    private String hintType;

    @NotNull(message = "User Id is required")
    private Long userId;
}
