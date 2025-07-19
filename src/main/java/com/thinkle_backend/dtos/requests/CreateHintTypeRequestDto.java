package com.thinkle_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateHintTypeRequestDto {
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[^ ]+$", message = "Type must not contain spaces")
    private String type;

    @NotBlank(message = "Display name is required")
    private String displayName;
}
